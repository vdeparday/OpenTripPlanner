package org.opentripplanner.updater;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.onebusaway.gtfs.model.AgencyAndId;
import org.opentripplanner.routing.patch.Alert;
import org.opentripplanner.routing.patch.AlertPatch;
import org.opentripplanner.routing.patch.TimePeriod;
import org.opentripplanner.routing.patch.TranslatedString;
import org.opentripplanner.routing.services.PatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.transit.realtime.GtfsRealtime;
import com.google.transit.realtime.GtfsRealtime.EntitySelector;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.TimeRange;
import com.google.transit.realtime.GtfsRealtime.TranslatedString.Translation;

public class UpdateHandler {
    private static final Logger log = LoggerFactory.getLogger(UpdateHandler.class);

    private FeedMessage message;

    private String defaultAgencyId;

    private Set<String> patchIds = new HashSet<String>();

    private PatchService patchService;

    /** How long before the posted start of an event it should be displayed to users */
    private long earlyStart;
    
    public UpdateHandler(FeedMessage message) {
        this.message = message;
    }

    public void update() {
        for (FeedEntity entity : message.getEntityList()) {
            if (!entity.hasAlert()) {
                continue;
            }
            GtfsRealtime.Alert alert = entity.getAlert();
            String id = entity.getId();
            handleAlert(id, alert);
        }
        
        patchService.expireAllExcept(patchIds);
    }

    private void handleAlert(String id, GtfsRealtime.Alert alert) {
        Alert alertText = new Alert();
        alertText.alertDescriptionText = deBuffer(alert.getDescriptionText());
        alertText.alertHeaderText = deBuffer(alert.getHeaderText());
        alertText.alertUrl = deBuffer(alert.getUrl());
        ArrayList<TimePeriod> periods = new ArrayList<TimePeriod>();
        long bestStartTime = Long.MAX_VALUE;
        for (TimeRange activePeriod : alert.getActivePeriodList()) {
            final long start = activePeriod.hasStart() ? activePeriod.getStart() - earlyStart : 0;
            final long realStart = activePeriod.hasStart() ? activePeriod.getStart() : 0;
            if (realStart > 0 && realStart < bestStartTime) {
                bestStartTime = realStart;
            }
            final long end = activePeriod.hasEnd() ? activePeriod.getEnd() : Long.MAX_VALUE;
            periods.add(new TimePeriod(start, end));
        }
        if (bestStartTime != Long.MAX_VALUE) {
            alertText.effectiveStartDate = new Date(bestStartTime);
        }
        for (EntitySelector informed : alert.getInformedEntityList()) {
            String routeId = null;
            if (informed.hasRouteId()) {
                routeId = informed.getRouteId();
            }
            String stopId = null;
            if (informed.hasStopId()) {
                stopId = informed.getStopId();
            }

            String agencyId = informed.getAgencyId();
            if (informed.hasAgencyId()) {
                agencyId = informed.getAgencyId().intern();
            } else {
                agencyId = defaultAgencyId;
            }
            if (agencyId == null) {
                log.error("Empty agency id (and no default set) in feed; other ids are route "
                        + routeId + " and stop " + stopId);
                continue;
            }
            agencyId = agencyId.intern();

            AlertPatch patch = new AlertPatch();
            if (routeId != null) {
                patch.setRoute(new AgencyAndId(agencyId, routeId));
            }
            if (stopId != null) {
                patch.setStop(new AgencyAndId(agencyId, stopId));
            }
            patch.setTimePeriods(periods);
            patch.setId(id);
            patch.setAlert(alertText);
            patchService.apply(patch);
            patchIds.add(id);
        }
    }

    /**
     * convert a protobuf TranslatedString to a OTP TranslatedString
     * 
     * @return
     */
    private TranslatedString deBuffer(GtfsRealtime.TranslatedString buffered) {
        TranslatedString result = new TranslatedString();
        for (Translation translation : buffered.getTranslationList()) {
            String language = translation.getLanguage();
            String string = translation.getText();
            result.addTranslation(language, string);
        }
        return result;
    }

    public void setDefaultAgencyId(String defaultAgencyId) {
        this.defaultAgencyId = defaultAgencyId.intern();
    }

    public void setPatchService(PatchService patchService) {
        this.patchService = patchService;
    }

    public long getEarlyStart() {
        return earlyStart;
    }

    public void setEarlyStart(long earlyStart) {
        this.earlyStart = earlyStart;
    }

}
