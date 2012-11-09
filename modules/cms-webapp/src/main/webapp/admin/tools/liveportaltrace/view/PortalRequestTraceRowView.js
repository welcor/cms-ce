if ( !lpt )
{
    var lpt = {};
}

lpt.PortalRequestTraceRowView = function ()
{
    function buildCacheUsages( portalRequestTrace )
    {
        var html = "";

        jQuery.each( portalRequestTrace.cacheUsages.list, function ( i, cacheUsage )
        {
            if ( i === 1 )
            {
                html += "-";
            }
            html += buildCacheUsage( cacheUsage, false );
        } );

        return html;
    }

    function buildCacheUsage( cacheUsage, includeBlockingTime )
    {
        if ( cacheUsage.cacheable == null )
        {
            return "<span class='cache-color-not-cacheable'>?</span>";
        }
        else if ( cacheUsage.cacheable === false )
        {
            return "<span class='cache-color-not-cacheable'>O</span>";
        }
        else if ( cacheUsage.usedCachedResult == null )
        {
            return "<span class='cache-color-cache-miss'>?</span>";
        }
        else if ( cacheUsage.usedCachedResult === false )
        {
            return "<span class='cache-color-cache-miss'>X</span>";
        }
        else
        {
            if ( cacheUsage.concurrencyBlocked === true )
            {
                var html = "";
                html += "<span class='cache-color-cache-hit-blocked'>&radic;</span>";
                if ( includeBlockingTime )
                {
                    html += " (blocked for " + cacheUsage.concurrencyBlockingTime + " ms)";
                }
                return html;
            }
            else
            {
                return "<span class='cache-color-cache-hit'>&radic;</span>";
            }
        }
    }

    return {
        createPortalRequestTraceTR:function ( portalRequestTraceRow )
        {
            var tr = document.createElement( "tr" );
            tr.livePortalTraceRequestNumber = portalRequestTraceRow.portalRequestTrace.requestNumber;
            tr.livePortalTraceCompletedNumber = portalRequestTraceRow.portalRequestTrace.completedNumber;

            var td1 = document.createElement( "td" );
            td1.innerHTML = portalRequestTraceRow.completedNumber;
            td1.className = "id-column";
            tr.appendChild( td1 );

            var td2 = document.createElement( "td" );
            td2.innerHTML = portalRequestTraceRow.type;
            td2.className = "type-column";
            tr.appendChild( td2 );

            var td3 = document.createElement( "td" );
            td3.innerHTML = portalRequestTraceRow.url;
            td3.title = portalRequestTraceRow.url;
            td3.className = "url-column";
            tr.appendChild( td3 );

            var td4 = document.createElement( "td" );
            td4.innerHTML = portalRequestTraceRow.started;
            td4.className = "startTime-column";
            td4.title = portalRequestTraceRow.started;
            tr.appendChild( td4 );

            var td5 = document.createElement( "td" );
            td5.innerHTML = portalRequestTraceRow.duration.asHRFormat;
            td5.className = "duration-column";
            td5.title = portalRequestTraceRow.duration.asHRFormat;
            tr.appendChild( td5 );

            var td6 = document.createElement( "td" );
            td6.innerHTML = buildCacheUsages( portalRequestTraceRow.portalRequestTrace );
            td6.className = "cacheUsage-column";
            tr.appendChild( td6 );

            return tr;
        }
    };
};

