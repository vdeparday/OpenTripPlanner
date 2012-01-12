otp.namespace('otp.application');

otp.application.ContactPanel = {
	
	 /** the text that gets displayed in the panel */
    contactHtml: null,

    /** the panel itself */
    panel: null,

    /** the title of the panel, which gets displayed */
    panelTitle: null,

    initialize: function(config) {
        otp.configure(this, config);
        this.panel = new Ext.Panel({
                html: this.contactHtml,
                title: this.panelTitle,
                layout: 'fit',
				padding: 8
            });
    },

    getPanel: function() {
        return this.panel;
    },
	
	CLASS_NAME : "otp.application.ContactPanel"
};

otp.application.ContactPanel = new otp.Class(otp.application.ContactPanel);	