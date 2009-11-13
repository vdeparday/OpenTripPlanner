package org.opentripplanner.api.ws;

import java.util.Date;
import java.util.List;
import javax.ws.rs.core.MediaType;

public interface RequestInf {

    public static enum ModeType {
        walk, bus, train, bike
    }
    public static enum OptimizeType {
        transfers, quick, flat
    }

    public static String FROM = "from";
    public static String TO = "to";
    public static String DATE = "date";
    public static String TIME = "time";

    public static String WALK = "walk";
    public static String OPTIMIZE = "optimize";
    public static String MODE = "mode";
    public static String NUMBER_ITINERARIES = "numItineraries";
    public static String OUTPUT_FORMAT = "outputFormat";

    public static String DEPART_AFTER = "after";
    public static String ARRIVE_BY = "by";

    /**
     * @return the from
     */
    public String getFrom();

    /**
     * @param from
     *            the from to set
     */
    public void setFrom(String from);

    /**
     * @return the to
     */
    public String getTo();

    /**
     * @param to
     *            the to to set
     */
    public void setTo(String to);

    /**
     * @return the walk
     */
    public Double getWalk();

    /**
     * @param walk
     *            the walk to set
     */
    public void setWalk(Double walk);

    /**
     * @return the modes
     */
    public List<ModeType> getModes();

    /**
     * @param modes
     *            the modes to set
     */
    public void addMode(ModeType mode);

    /** */
    public void addMode(List<ModeType> mList);

    /**
     * @return the optimize
     */
    public List<OptimizeType> getOptimize();

    /**
     * @param optimize
     *            the optimize to set
     */
    public void addOptimize(OptimizeType opt);

    /** */
    public void addOptimize(List<OptimizeType> oList);

    /**
     * @return the dateTime
     */
    public Date getDateTime();

    /**
     * @param dateTime
     *            the dateTime to set
     */
    public void setDateTime(Date dateTime);

    /**
     * @param dateTime
     *            the dateTime to set
     */
    public void setDateTime(String date, String time);

    /**
     * @return the departAfter
     */
    public boolean isDepartAfter();

    /**
     * @return the departAfter
     */
    public boolean isArriveBy();

    /**
     * @param departAfter
     *            the departAfter to set
     */
    public void setDepartAfter();

    /**
     * @param departAfter
     *            the departAfter to set
     */
    public void setArriveBy();

    /**
     * @return the outputFormat
     */
    public MediaType getOutputFormat();

    /**
     * @param outputFormat
     *            the outputFormat to set
     */
    public void setOutputFormat(MediaType outputFormat);

    /**
     * @return the numItineraries
     */
    public Integer getNumItineraries();

    /**
     * @param numItineraries
     *            the numItineraries to set
     */
    public void setNumItineraries(Integer numItineraries);

}