if ( !lpt )
{
    var lpt = {};
}

lpt.SystemInfoController = function ( automaticReloadTimeInMillis )
{
    var thisCtrl = this;
    var refreshUrl;
    var worker;
    var timeoutId;
    var pageCacheCapacityGraphController;
    var entityCacheCapacityGraphController;
    var javaMemoryGraphController;

    var workerThreadIsSupported = false;

    this.setWorkerThreadIsSupported = function ( value )
    {
        workerThreadIsSupported = value;
    };

    this.setRefreshUrl = function ( url )
    {
        refreshUrl = url;
    };

    this.setPageCacheCapacityGraphController = function ( ctrl )
    {
        pageCacheCapacityGraphController = ctrl;
    };

    this.setEntityCacheCapacityGraphController = function ( ctrl )
    {
        entityCacheCapacityGraphController = ctrl;
    };

    this.setJavaMemoryGraphController = function ( ctrl )
    {
        javaMemoryGraphController = ctrl;
    };

    this.init = function ()
    {
        if ( workerThreadIsSupported )
        {
            worker = new Worker( "liveportaltrace/request-worker.js" );
        }
    };

    this.startAutomaticUpdate = function ()
    {
        (function loop()
        {
            timeoutId = setTimeout( function ()
                                    {
                                        thisCtrl.update();
                                        loop();
                                    }, automaticReloadTimeInMillis );
        })();
    };

    this.stopAutomaticUpdate = function ()
    {
        clearInterval( timeoutId );
    };

    this.update = function ()
    {
        if ( workerThreadIsSupported )
        {
            updateUsingThread();
        }
        else
        {
            updateWithoutUsingThread();
        }
    };

    function updateUsingThread()
    {
        worker.onmessage = function ( event )
        {
            var message = event.data;
            if ( message.operation === "reload-systeminfo" )
            {
                var systemInfo = jQuery.parseJSON( event.data.jsonData );
                updateGUI( systemInfo );
            }
        };

        worker.postMessage( {
                                "operation":"reload-systeminfo",
                                "url":refreshUrl
                            } );
    }

    function updateWithoutUsingThread()
    {
        $.getJSON( refreshUrl, function ( systemInfo )
        {
            updateGUI( systemInfo );
        } );
    }

    function updateGUI( systemInfo )
    {
        $( '#current-requests-tab-label' ).text( systemInfo.portalRequestInProgress );

        $( '#entity-cache-count' ).text( systemInfo.entityCacheStatistic.count );
        $( '#entity-cache-hit-count' ).text( systemInfo.entityCacheStatistic.hitCount );
        $( '#entity-cache-miss-count' ).text( systemInfo.entityCacheStatistic.missCount );
        $( '#entity-cache-capacity-count' ).text( systemInfo.entityCacheStatistic.capacity );

        entityCacheCapacityGraphController.add( systemInfo.entityCacheStatistic.count, systemInfo.entityCacheStatistic.capacity,
                                                systemInfo.entityCacheStatistic.hitCount, systemInfo.entityCacheStatistic.missCount );

        $( '#page-cache-count' ).text( systemInfo.pageCacheStatistic.count );
        $( '#page-cache-hit-count' ).text( systemInfo.pageCacheStatistic.hitCount );
        $( '#page-cache-miss-count' ).text( systemInfo.pageCacheStatistic.missCount );
        $( '#page-cache-capacity-count' ).text( systemInfo.pageCacheStatistic.capacity );

        pageCacheCapacityGraphController.add( systemInfo.pageCacheStatistic.count, systemInfo.pageCacheStatistic.capacity,
                                              systemInfo.pageCacheStatistic.hitCount, systemInfo.pageCacheStatistic.missCount );

        $( '#java-heap-memory-usage-init' ).text( humanReadableBytes( systemInfo.javaHeapMemoryStatistic.init ) );
        $( '#java-heap-memory-usage-used' ).text( humanReadableBytes( systemInfo.javaHeapMemoryStatistic.used ) );
        $( '#java-heap-memory-usage-committed' ).text( humanReadableBytes( systemInfo.javaHeapMemoryStatistic.committed ) );
        $( '#java-heap-memory-usage-max' ).text( humanReadableBytes( systemInfo.javaHeapMemoryStatistic.max ) );

        javaMemoryGraphController.add( systemInfo.javaHeapMemoryStatistic.used, systemInfo.javaHeapMemoryStatistic.max );

        $( '#java-non-heap-memory-usage-init' ).text( humanReadableBytes( systemInfo.javaNonHeapMemoryStatistic.init ) );
        $( '#java-non-heap-memory-usage-used' ).text( humanReadableBytes( systemInfo.javaNonHeapMemoryStatistic.used ) );
        $( '#java-non-heap-memory-usage-committed' ).text( humanReadableBytes( systemInfo.javaNonHeapMemoryStatistic.committed ) );
        $( '#java-non-heap-memory-usage-max' ).text( humanReadableBytes( systemInfo.javaNonHeapMemoryStatistic.max ) );

        $( '#java-thread-count' ).text( systemInfo.javaThreadStatistic.count );
        $( '#java-thread-peak-count' ).text( systemInfo.javaThreadStatistic.peakCount );

        var data_source_open_connection_count = systemInfo.data_source_open_connection_count;
        if ( data_source_open_connection_count == -1 )
        {
            $( '#data-source-open-connection-count' ).text( "N/A" );
        }
        else
        {
            $( '#data-source-open-connection-count' ).text( systemInfo.data_source_open_connection_count );
        }
    }

    function humanReadableBytes( size )
    {
        var suffix = ["bytes", "KB", "MB", "GB", "TB", "PB"];
        var tier = 0;

        while ( size >= 1024 )
        {
            size = size / 1024;
            tier++;
        }

        return Math.round( size * 10 ) / 10 + " " + suffix[tier];
    }
};