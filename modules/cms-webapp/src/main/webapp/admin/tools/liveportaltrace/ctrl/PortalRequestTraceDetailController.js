if ( !lpt )
{
    var lpt = {};
}

lpt.PortalRequestTraceDetailController = function ()
{
    var portalRequestTraceDetailHtmlBuilder;

    this.setPortalRequestTraceDetailHtmlBuilder = function ( value )
    {
        portalRequestTraceDetailHtmlBuilder = value;
    };

    this.closePortalRequestTraceDetailWindow = function ()
    {
        $( "#portalRequestTraceDetail-window" ).hide();
    };

    this.showPortalRequestTraceDetail = function ( portalRequestTrace )
    {
        var html = portalRequestTraceDetailHtmlBuilder.createPortalRequestDetailTable( portalRequestTrace );

        $( "#portalRequestTraceDetail-details" ).html( html );

        $( "#portalRequestTraceDetail-window" ).show();

        $( "#trace-details-tree-table" ).treeTable( {
                                                        expandable:true,
                                                        initialState:"collapsed"
                                                    } );
    };

    this.expandAllTraceDetails = function ()
    {
        $( "#trace-details-tree-table" ).expand();
    };

    this.collapseAllTraceDetails = function ()
    {
        $( "#trace-details-tree-table" ).collapse();
    };
};



