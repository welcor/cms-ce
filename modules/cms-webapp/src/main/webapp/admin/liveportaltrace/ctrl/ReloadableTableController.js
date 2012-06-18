if ( !lpt )
{
    var lpt = {};
}

lpt.ReloadableTableController = function ( tableId, automaticReloadTimeInMillis )
{
    var thisCtrl = this;
    var reloadUrl;
    var clearUrl;
    var portalRequestTraceDetailController;
    var portalRequestTraceRowView;
    var portalRequestsTraceRows = {};

    var reloadIntervalId;

    var table = document.getElementById( tableId );

    this.setReloadUrl = function ( url )
    {
        reloadUrl = url;
    };

    this.setClearUrl = function ( url )
    {
        clearUrl = url;
    };

    this.setPortalRequestTraceDetailController = function ( value )
    {
        portalRequestTraceDetailController = value;
    }

    this.setPortalRequestTraceRowView = function ( value )
    {
        portalRequestTraceRowView = value;
    };

    this.init = function ()
    {
        table.addEventListener( "click", function ( event )
        {
            var clickTarget = event.target;
            var tableRow = null;
            if ( clickTarget.tagName === "TD" )
            {
                tableRow = clickTarget.parentNode;
            }
            else if ( clickTarget.tagName === "TR" )
            {
                tableRow = clickTarget;
            }

            if ( tableRow != null )
            {
                var portalRequestsTraceRow = portalRequestsTraceRows[tableRow.livePortalTraceCompletedNumber];
                portalRequestTraceDetailController.showPortalRequestTraceDetail( portalRequestsTraceRow.portalRequestTrace );
            }

        } );
    };

    this.reload = function ()
    {
        $.getJSON( reloadUrl, function ( traces )
        {
            appendTraces( traces );
        } );
    };

    this.startAutomaticReload = function ()
    {
        (function loop()
        {
            reloadIntervalId = setTimeout( function ()
                                           {
                                               thisCtrl.reload();
                                               loop();
                                           }, automaticReloadTimeInMillis );
        })();
    };

    this.stopAutomaticReload = function ()
    {
        clearInterval( reloadIntervalId );
    };

    this.clear = function ()
    {
        jQuery.ajax( {
                         url:clearUrl,
                         type:'POST',
                         cache:false,
                         async:true,
                         dataType:'json',
                         success:thisCtrl.reload
                     } );
    };

    function appendTraces( traces )
    {
        var newTableBody = document.createElement( 'tbody' );

        if ( traces.length > 0 )
        {
            for ( var i = 0; i < traces.length; i++ )
            {
                var portalRequestTraceRow = traces[i];
                portalRequestsTraceRows[portalRequestTraceRow.portalRequestTrace.requestNumber] = portalRequestTraceRow;

                var tableTR = portalRequestTraceRowView.createPortalRequestTraceTR( portalRequestTraceRow );
                newTableBody.appendChild( tableTR );
            }
        }

        var oldTableBody = table.getElementsByTagName( "tbody" )[0];
        table.replaceChild( newTableBody, oldTableBody );
    }
};