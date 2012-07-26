if ( !lpt )
{
    var lpt = {};
}

lpt.CompletedPortalRequestsTableController = function ( tableId, automaticRefreshTimeInMillis )
{
    var thisCtrl = this;
    var refreshIntervalId;
    var initialRowsToLoad = 50;
    var loadCompletedAfterUrl;
    var loadCompletedBeforeUrl;
    var lastCompletedNumber = -1;
    var firstCompletedNumber = -1;
    var completedRequestsMap = {};
    var worker;
    var completedRequestsGraphController;
    var portalRequestTraceDetailController;
    var portalRequestTraceRowView;
    var table = document.getElementById( tableId );
    var tableBody = table.getElementsByTagName( "tbody" )[0];
    var workerThreadIsSupported = false;

    this.setWorkerThreadIsSupported = function ( value )
    {
        workerThreadIsSupported = value;
    };

    this.setLoadCompletedAfterUrl = function ( url )
    {
        loadCompletedAfterUrl = url;
    };

    this.setLoadCompletedBeforeUrl = function ( url )
    {
        loadCompletedBeforeUrl = url;
    };

    this.setPortalRequestTraceRowView = function ( value )
    {
        portalRequestTraceRowView = value;
    };

    this.setCompletedRequestsGraphController = function ( ctrl )
    {
        completedRequestsGraphController = ctrl;
    };

    this.setPortalRequestTraceDetailController = function ( ctrl )
    {
        portalRequestTraceDetailController = ctrl;
    };

    this.init = function ()
    {
        if ( workerThreadIsSupported )
        {
            worker = new Worker( "liveportaltrace/request-worker.js" );
        }

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
                portalRequestTraceDetailController.showPortalRequestTraceDetail( completedRequestsMap[tableRow.livePortalTraceCompletedNumber] );
            }

        } );
    };

    this.loadNew = function ()
    {
        if ( workerThreadIsSupported )
        {
            loadNewUsingThread();
        }
        else
        {
            loadNewWithoutUsingThread();
        }
    };

    this.reload = function ()
    {
        if ( workerThreadIsSupported )
        {
            reloadUsingThread();
        }
        else
        {
            reloadWithoutUsingThread();
        }
    };

    this.startAutomaticRefresh = function ()
    {
        (function loop()
        {
            refreshIntervalId = setTimeout( function ()
                                            {
                                                thisCtrl.loadNew();
                                                loop();
                                            }, automaticRefreshTimeInMillis );
        })();
    };

    this.stopAutomaticRefresh = function ()
    {
        clearInterval( refreshIntervalId );
    };

    function loadNewUsingThread()
    {
        worker.onmessage = function ( event )
        {
            var message = event.data;
            if ( message.operation === "load-new" )
            {
                var traces = jQuery.parseJSON( event.data.jsonData );
                insertAtTop( traces );
            }
        };

        worker.postMessage( {
                                "operation":"load-new",
                                "url":resolveCompletedAfterUrl( lastCompletedNumber, null )
                            } );
    }

    function loadNewWithoutUsingThread()
    {
        $.getJSON( resolveCompletedAfterUrl( lastCompletedNumber, null ), function ( traces )
        {
            insertAtTop( traces );
        } );
    }

    function reloadUsingThread()
    {
        worker.onmessage = function ( event )
        {
            var message = event.data;
            if ( message.operation === "load-latest" )
            {
                insertAtTop( jQuery.parseJSON( event.data.jsonData ) );
                worker.postMessage( {
                                        "operation":"load-rest-of-history",
                                        "url":resolveCompletedBeforeUrl( getFirstCompletedNumber() )
                                    } );

            }
            else if ( message.operation === "load-rest-of-history" )
            {
                appendTraces( jQuery.parseJSON( event.data.jsonData ) );
            }
        };

        worker.postMessage( {
                                "operation":"load-latest",
                                "url":resolveCompletedAfterUrl( lastCompletedNumber, initialRowsToLoad )
                            } );
    }

    function reloadWithoutUsingThread()
    {
        jQuery.getJSON( resolveCompletedAfterUrl( lastCompletedNumber, initialRowsToLoad ), function ( firstTraces )
        {
            insertAtTop( firstTraces );
            jQuery.getJSON( resolveCompletedBeforeUrl( getFirstCompletedNumber() ), function ( restOfTraces )
            {
                appendTraces( restOfTraces );
            } );
        } );
    }

    function insertAtTop( traces )
    {
        if ( traces.length > 0 )
        {
            traces.reverse();
            for ( var i = 0, length = traces.length; i < length; i++ )
            {
                var row = traces[i];
                completedRequestsMap[ row.portalRequestTrace.completedNumber ] = row.portalRequestTrace;

                var firstTr = tableBody.getElementsByTagName( "tr" )[0];
                tableBody.insertBefore( portalRequestTraceRowView.createPortalRequestTraceTR( row ), firstTr );
            }

            lastCompletedNumber = traces[traces.length - 1].completedNumber;
            firstCompletedNumber = traces[0].completedNumber;
        }
        completedRequestsGraphController.add( traces );
    }

    function appendTraces( traces )
    {
        for ( var i = 0, length = traces.length; i < length; i++ )
        {
            var row = traces[i];
            completedRequestsMap[ row.portalRequestTrace.completedNumber ] = row.portalRequestTrace;
            tableBody.appendChild( portalRequestTraceRowView.createPortalRequestTraceTR( row ) );
        }
    }

    function resolveCompletedAfterUrl( afterCompletedNumber, count )
    {
        if ( count == null )
        {
            return loadCompletedAfterUrl + afterCompletedNumber;
        }
        else
        {
            return loadCompletedAfterUrl + afterCompletedNumber + "&count=" + count;
        }
    }

    function resolveCompletedBeforeUrl( beforeCompletedNumber )
    {
        return loadCompletedBeforeUrl + beforeCompletedNumber;
    }

    function getFirstCompletedNumber()
    {
        return firstCompletedNumber;
    }
};
