YAHOO.namespace("esl.container");

function init() {
	// Loading panel
	YAHOO.esl.container.loading = new YAHOO.widget.Panel("loadingPanel", { width:"240px", fixedcenter:true, close:false, visible:false, effect:{effect:YAHOO.widget.ContainerEffect.FADE, duration:0.1}});
	YAHOO.esl.container.loading.setHeader("<div class='tl'></div><span>Loading, please wait...</span><div class='tr'></div>");
	YAHOO.esl.container.loading.setBody('<img src="/ESL/images/yui/rel_interstitial_loading.gif" />');
	YAHOO.esl.container.loading.render(document.body);
}

YAHOO.util.Event.addListener(window, "load", init);