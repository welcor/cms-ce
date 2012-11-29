if ( !lpt )
{
    var lpt = {};
}

lpt.PortalRequestTraceDetailHtmlBuilder = function ()
{
    function buildImageRequestTrace( imageRequestTrace, id )
    {
        var html = "";

        html += "<tr id='node-" + id + "'>";
        html += "<td>Image request</td><td>" + imageRequestTrace.duration.asHRFormat + "</td>";
        html += "</tr>";

        html += "<tr id='node-" + id + "-1' class='child-of-node-" + id + "'>";
        html += "<td>Cache</td><td>" + buildCacheUsage( imageRequestTrace.cacheUsage, true ) + "</td>";
        html += "</tr>";
        html += "<tr id='node-" + id + "-2' class='child-of-node-" + id + "'>";
        html += "<td>Size</td><td>" + imageRequestTrace.sizeInBytes + "</td>";
        html += "</tr>";
        html += "<tr id='node-" + id + "-3' class='child-of-node-" + id + "'>";
        html += "<td>Content key</td><td>" + imageRequestTrace.contentKey + "</td>";
        html += "</tr>";
        html += "<tr id='node-" + id + "-4' class='child-of-node-" + id + "'>";
        html += "<td>Label</td><td>" + imageRequestTrace.label + "</td>";
        html += "</tr>";
        html += "<tr id='node-" + id + "-5' class='child-of-node-" + id + "'>";
        html += "<td>Format</td><td>" + imageRequestTrace.imageParamFormat + "</td>";
        html += "</tr>";
        html += "<tr id='node-" + id + "-6' class='child-of-node-" + id + "'>";
        html += "<td>Quality</td><td>" + imageRequestTrace.imageParamQuality + "</td>";
        html += "</tr>";
        html += "<tr id='node-" + id + "-7' class='child-of-node-" + id + "'>";
        html += "<td>Filter</td><td>" + imageRequestTrace.imageParamFilter + "</td>";
        html += "</tr>";
        html += "<tr id='node-" + id + "-8' class='child-of-node-" + id + "'>";
        html += "<td>Background color</td><td>" + imageRequestTrace.imageParamBackgroundColor + "</td>";
        html += "</tr>";

        return html;
    }

    function buildAttachmentRequestTrace( attachmentRequestTrace, id )
    {
        var html = "";

        html += "<tr id='node-" + id + "'>";
        html += "<td>Attachment request</td><td>" + attachmentRequestTrace.duration.asHRFormat + "</td>";
        html += "</tr>";

        html += "<tr id='node-" + id + "-1' class='child-of-node-" + id + "'>";
        html += "<td>Size</td><td>" + attachmentRequestTrace.sizeInBytes + "</td>";
        html += "</tr>";
        html += "<tr id='node-" + id + "-2' class='child-of-node-" + id + "'>";
        html += "<td>Content key</td><td>" + attachmentRequestTrace.contentKey + "</td>";
        html += "</tr>";
        html += "<tr id='node-" + id + "-3' class='child-of-node-" + id + "'>";
        html += "<td>Binary key</td><td>" + attachmentRequestTrace.binaryDataKey + "</td>";
        html += "</tr>";

        return html;
    }

    function buildPageRenderingTrace( pageRenderingTrace, id )
    {
        var html = "";
        html += "<tr id='node-" + id + "'>";
        html += "<td>Page</td><td>" + pageRenderingTrace.duration.asHRFormat + "</td>";
        html += "</tr>";
        html += "<tr id='node-" + id + "-1' class='child-of-node-" + id + "'>";
        html += "<td>Cache</td><td>" + buildCacheUsage( pageRenderingTrace.cacheUsage, true ) + "</td>";
        html += "</tr>";
        html += "<tr id='node-" + id + "-2' class='child-of-node-" + id + "'>";
        html += "<td>Renderer</td><td>" + buildUser( pageRenderingTrace.renderer ) + "</td>";
        html += "</tr>";

        if ( !pageRenderingTrace.usedCachedResult === true )
        {
            if ( pageRenderingTrace.datasourceExecutionTraces != null )
            {
                // Datasource executions
                html += "<tr id='node-" + id + "-3' class='child-of-node-" + id + "'>";
                html += "<td>Datasource Executions</td><td>" + pageRenderingTrace.datasourceExecutionTraces.totalPeriodInHRFormat + "</td>";
                html += "</tr>";
                html += buildDatasourceExecutionTraces( pageRenderingTrace.datasourceExecutionTraces, id + "-3" );
            }
            else
            {
                html += "<tr id='node-" + id + "-3' class='child-of-node-" + id + "'>";
                html += "<td>Datasource Executions</td><td></td>";
                html += "</tr>";
            }

            // View transformation
            if ( pageRenderingTrace.viewTransformationTrace != null )
            {
                html += buildViewTransformationTrace( pageRenderingTrace.viewTransformationTrace, id + "-4" );
            }
        }
        // Windows
        html += buildWindowRenderingTraces( pageRenderingTrace.windowRenderingTraces, id + "-5" );

        // Instruction Post Processing trace
        if ( pageRenderingTrace.instructionPostProcessingTrace != null )
        {
            html += buildInstructionPostProcessingTrace( pageRenderingTrace.instructionPostProcessingTrace, id + "-6" );
        }

        return html;
    }

    function buildWindowRenderingTraces( windowRenderingTraces, id )
    {
        var html = "";

        if ( windowRenderingTraces == null )
        {
            html += "<tr id='node-" + id + "' class='child-of-node-" + resolveParentId( id ) + "'>";
            html += "<td>Window rendering traces</td><td></td>";
            html += "</tr>";
            return "";
        }

        html += "<tr id='node-" + id + "' class='child-of-node-" + resolveParentId( id ) + "'>";
        html += "<td>Window rendering traces</td><td>" + windowRenderingTraces.totalPeriodInHRFormat + "</td>";
        html += "</tr>";

        jQuery.each( windowRenderingTraces.list, function ( i, windowRenderingTrace )
        {
            var childId = id + "-" + (i + 1);

            html += buildWindowRenderingTrace( windowRenderingTrace, childId, id );
        } );
        return html;
    }

    function buildWindowRenderingTrace( windowRenderingTrace, id )
    {
        var html = "";

        var parentId = resolveParentId( id );
        if ( parentId == null )
        {
            html += "<tr id='node-" + id + "'>";
        }
        else
        {
            html += "<tr id='node-" + id + "' class='child-of-node-" + parentId + "'>";
        }

        html += "<td>" + windowRenderingTrace.portletName + "</td><td>" + windowRenderingTrace.duration.asHRFormat + "</td>";
        html += "</tr>";

        var childCount = 1;
        var childId = id + "-" + (childCount++);

        html += "<tr id='node-" + childId + "' class='child-of-node-" + id + "'>";
        html += "<td>Cache usage</td><td>" + buildCacheUsage( windowRenderingTrace.cacheUsage, true ) + "</td>";
        childId = id + "-" + (childCount++);

        html += "</tr>";
        html += "<tr id='node-" + childId + "' class='child-of-node-" + id + "'>";
        html += "<td>Renderer</td><td>" + buildUser( windowRenderingTrace.renderer ) + "</td>";
        html += "</tr>";

        if ( !windowRenderingTrace.usedCachedResult === true )
        {
            childId = id + "-" + (childCount++);

            if ( windowRenderingTrace.datasourceExecutionTraces != null )
            {
                html += "<tr id='node-" + childId + "' class='child-of-node-" + id + "'>";
                html +=
                    "<td>Datasource Executions</td><td>" + windowRenderingTrace.datasourceExecutionTraces.totalPeriodInHRFormat + "</td>";
                html += "</tr>";
                html += buildDatasourceExecutionTraces( windowRenderingTrace.datasourceExecutionTraces, childId );
            }
            else
            {
                html += "<tr id='node-" + childId + "' class='child-of-node-" + id + "'>";
                html += "<td>Datasource Executions</td><td></td>";
                html += "</tr>";
            }

            // View transformation
            if ( windowRenderingTrace.viewTransformationTrace != null )
            {
                childId = id + "-" + (childCount++);
                html += buildViewTransformationTrace( windowRenderingTrace.viewTransformationTrace, childId );
            }
        }

        // Instruction Post Processing trace
        if ( windowRenderingTrace.instructionPostProcessingTrace != null )
        {
            childId = id + "-" + (childCount);
            html += buildInstructionPostProcessingTrace( windowRenderingTrace.instructionPostProcessingTrace, childId );
        }

        return html;
    }

    function buildDatasourceExecutionTraces( datasourceExecutionTraces, parentId )
    {
        var html = "";
        jQuery.each( datasourceExecutionTraces.list, function ( i, datasourceExecutionTrace )
        {

            var id = parentId + "-" + (i + 1);

            html += "<tr id='node-" + id + "' class='child-of-node-" + parentId + "'>";
            html += "<td>" + datasourceExecutionTrace.methodName + "</td><td>" + datasourceExecutionTrace.duration.asHRFormat + "</td>";
            html += "</tr>";
            html += "<tr id='node-" + id + "-1' class='child-of-node-" + id + "'>";
            html += "<td>Executed</td><td>" + datasourceExecutionTrace.executed + "</td>";
            html += "</tr>";
            html += "<tr id='node-" + id + "-2' class='child-of-node-" + id + "'>";
            html += "<td>Runnable condition</td><td>" + emptyIfNull( datasourceExecutionTrace.runnableCondition ) + "</td>";
            html += "</tr>";
            html += "<tr id='node-" + id + "-3' class='child-of-node-" + id + "'>";
            html += "<td>Used cached result (request scoped)</td><td>" + booleanToYesNo( datasourceExecutionTrace.cacheUsed ) + "</td>";
            html += "</tr>";

            // Method arguments
            html += "<tr id='node-" + id + "-4' class='child-of-node-" + id + "'>";
            html += "<td>Method arguments</td><td></td>";
            html += "</tr>";
            html += buildDatasourceMethodArguments( datasourceExecutionTrace.datasourceMethodArguments, id + "-4" );

            var counter = 4;

            if ( datasourceExecutionTrace.contentIndexQueryTraces != null )
            {
                html += buildContentIndexQueryTraces( datasourceExecutionTrace.contentIndexQueryTraces, id + "-" + (++counter) );
            }

            if ( datasourceExecutionTrace.relatedContentFetchTraces != null )
            {
                html += buildRelatedContentFetchTraces( datasourceExecutionTrace.relatedContentFetchTraces, id + "-" + (++counter) );
            }

            // Client Method Execution traces
            if ( datasourceExecutionTrace.clientMethodExecutionTraces != null )
            {
                var clientMethodExecutionTracesId = id + "-" + (++counter);

                html += "<tr id='node-" + clientMethodExecutionTracesId + "' class='child-of-node-" + id + "'>";
                html += "<td>Client Method Execution traces</td><td>" +
                    datasourceExecutionTrace.clientMethodExecutionTraces.totalPeriodInHRFormat + "</td>";
                html += "</tr>";
                html +=
                    buildClientMethodExecutionTraces( datasourceExecutionTrace.clientMethodExecutionTraces, clientMethodExecutionTracesId );
            }

        } );
        return html;
    }

    function buildDatasourceMethodArguments( datasourceMethodArguments, parentId )
    {
        var html = "";
        jQuery.each( datasourceMethodArguments, function ( i, datasourceMethodArgument )
        {
            html += "<tr id='node-" + parentId + "-1' class='child-of-node-" + parentId + "'>";
            html += "<td>" + datasourceMethodArgument.name + "</td>";
            html += "<td>";
            html += datasourceMethodArgument.value;
            if ( datasourceMethodArgument.override != null )
            {
                html += "( " + datasourceMethodArgument.override + " )";
            }
            html += "</td>";
            html += "</tr>";
        } );
        return html;
    }

    function buildInstructionPostProcessingTrace( instructionPostProcessingTrace, id )
    {
        var html = "";

        html += "<tr id='node-" + id + "' class='child-of-node-" + resolveParentId( id ) + "'>";
        html += "<td>Instruction post processing</td><td>" + instructionPostProcessingTrace.duration.asHRFormat + "</td>";
        html += "</tr>";

        return html;
    }

    function buildClientMethodExecutionTraces( clientMethodExecutionTraces, parentId )
    {
        var html = "";
        jQuery.each( clientMethodExecutionTraces.list, function ( i, clientMethodExecutionTrace )
        {

            var id = parentId + "-" + (i + 1);

            html += "<tr id='node-" + id + "' class='child-of-node-" + parentId + "'>";
            html += "<td>" + clientMethodExecutionTrace.methodName + "</td><td>" + clientMethodExecutionTrace.duration.asHRFormat + "</td>";
            html += "</tr>";

            var counter = 0;

            if ( clientMethodExecutionTrace.contentIndexQueryTraces != null )
            {
                html += buildContentIndexQueryTraces( clientMethodExecutionTrace.contentIndexQueryTraces, id + "-" + (++counter) );
            }
            if ( clientMethodExecutionTrace.relatedContentFetchTraces != null )
            {
                html += buildRelatedContentFetchTraces( clientMethodExecutionTrace.relatedContentFetchTraces, id + "-" + (++counter) );
            }

        } );
        return html;
    }

    function buildContentIndexQueryTraces( contentIndexQueryTraces, id )
    {
        var html = "";

        html += "<tr id='node-" + id + "' class='child-of-node-" + resolveParentId( id ) + "'>";
        html += "<td>Content index queries (" + contentIndexQueryTraces.list.length + ")</td><td>" +
            contentIndexQueryTraces.totalPeriodInHRFormat + "</td>";
        html += "</tr>";

        jQuery.each( contentIndexQueryTraces.list, function ( i, contentIndexQueryTrace )
        {
            html += buildContentIndexQueryTrace( contentIndexQueryTrace, id + "-" + (i + 1), (i + 1) );

        } );
        return html;
    }

    function buildContentIndexQueryTrace( contentIndexQueryTrace, id, queryNumber )
    {
        var html = "";

        html += "<tr id='node-" + id + "' class='child-of-node-" + resolveParentId( id ) + "'>";
        html += "<td>Query #" + queryNumber + "</td><td>" + contentIndexQueryTrace.duration.asHRFormat + "</td>";
        html += "</tr>";

        html += "<tr id='node-" + id + "-1' class='child-of-node-" + id + "'>";
        html += "<td>Index</td><td>" + contentIndexQueryTrace.index + "</td>";
        html += "</tr>";
        html += "<tr id='node-" + id + "-2' class='child-of-node-" + id + "'>";
        html += "<td>Count</td><td>" + contentIndexQueryTrace.count + "</td>";
        html += "</tr>";
        html += "<tr id='node-" + id + "-3' class='child-of-node-" + id + "'>";
        html += "<td>Match count</td><td>" + contentIndexQueryTrace.matchCount + "</td>";
        html += "</tr>";
        html += "<tr id='node-" + id + "-4' class='child-of-node-" + id + "'>";
        html += "<td>Query</td><td>" + contentIndexQueryTrace.query + "</td>";
        html += "</tr>";
        html += "<tr id='node-" + id + "-5' class='child-of-node-" + id + "'>";
        html += "<td>Content filter</td><td>" + contentIndexQueryTrace.contentFilter + "</td>";
        html += "</tr>";
        html += "<tr id='node-" + id + "-6' class='child-of-node-" + id + "'>";
        html += "<td>Section filter</td><td>" + contentIndexQueryTrace.sectionFilter + "</td>";
        html += "</tr>";
        html += "<tr id='node-" + id + "-7' class='child-of-node-" + id + "'>";
        html += "<td>Category filter</td><td>" + contentIndexQueryTrace.categoryFilter + "</td>";
        html += "</tr>";
        html += "<tr id='node-" + id + "-8' class='child-of-node-" + id + "'>";
        html += "<td>Content type filter</td><td>" + contentIndexQueryTrace.contentTypeFilter + "</td>";
        html += "</tr>";
        html += "<tr id='node-" + id + "-9' class='child-of-node-" + id + "'>";
        html += "<td>Category access type filter</td><td>" + contentIndexQueryTrace.categoryAccessTypeFilter + "</td>";
        html += "</tr>";
        html += "<tr id='node-" + id + "-10' class='child-of-node-" + id + "'>";
        html += "<td>Security filter</td><td>" + contentIndexQueryTrace.securityFilter + "</td>";
        html += "</tr>";
        html += "<tr id='node-" + id + "-11' class='child-of-node-" + id + "'>";
        html += "<td>Translated query</td><td>" + JSON.stringify( contentIndexQueryTrace.translatedQuery ) + "</td>";
        html += "</tr>";
        html += "<tr id='node-" + id + "-12' class='child-of-node-" + id + "'>";
        html += "<td>Duration in Elastic Search</td><td>" + contentIndexQueryTrace.durationInElasticSearch.asHRFormat + "</td>";
        html += "</tr>";

        return html;
    }

    function buildRelatedContentFetchTraces( relatedContentFetchTraces, id )
    {
        var html = "";

        html += "<tr id='node-" + id + "' class='child-of-node-" + resolveParentId( id ) + "'>";
        html += "<td>Related content fetches(" + relatedContentFetchTraces.list.length + ")</td><td>" +
            relatedContentFetchTraces.totalPeriodInHRFormat + "</td>";
        html += "</tr>";

        jQuery.each( relatedContentFetchTraces.list, function ( i, relatedContentFetchTrace )
        {
            html += buildRelatedContentFetchTrace( relatedContentFetchTrace, id + "-" + (i + 1), (i + 1) );

        } );
        return html;
    }

    function buildRelatedContentFetchTrace( relatedContentFetchTrace, id, fetchNumber )
    {
        var html = "";

        html += "<tr id='node-" + id + "' class='child-of-node-" + resolveParentId( id ) + "'>";
        html += "<td>Fetch #" + fetchNumber + "</td><td>" + relatedContentFetchTrace.duration.asHRFormat + "</td>";
        html += "</tr>";

        html += "<tr id='node-" + id + "-2' class='child-of-node-" + id + "'>";
        html += "<td>Max parent level</td><td>" + relatedContentFetchTrace.maxParentLevel + "</td>";
        html += "</tr>";

        html += "<tr id='node-" + id + "-3' class='child-of-node-" + id + "'>";
        html += "<td>Max children level</td><td>" + relatedContentFetchTrace.maxChildrenLevel + "</td>";
        html += "</tr>";

        html += "<tr id='node-" + id + "-4' class='child-of-node-" + id + "'>";
        html += "<td>Parent fetches</td><td>" + relatedContentFetchTrace.parentFetches + "</td>";
        html += "</tr>";
        html += "<tr id='node-" + id + "-5' class='child-of-node-" + id + "'>";
        html += "<td>Children fetches</td><td>" + relatedContentFetchTrace.childrenFetches + "</td>";
        html += "</tr>";

        return html;
    }

    function buildViewTransformationTrace( viewTransformationTrace, id )
    {
        var html = "";

        html += "<tr id='node-" + id + "' class='child-of-node-" + resolveParentId( id ) + "'>";
        html += "<td>View transformation</td><td>" + viewTransformationTrace.duration.asHRFormat + "</td>";
        html += "</tr>";
        html += "<tr id='node-" + id + "-1' class='child-of-node-" + id + "'>";
        html += "<td>View</td><td>" + viewTransformationTrace.view + "</td>";
        html += "</tr>";

        if ( viewTransformationTrace.viewFunctionTraces != null )
        {
            html += buildViewFunctionTraces( viewTransformationTrace.viewFunctionTraces, id + "-2" );
        }

        return html;
    }

    function buildViewFunctionTraces( viewFunctionTraces, id )
    {
        var html = "";
        html += "<tr id='node-" + id + "' class='child-of-node-" + resolveParentId( id ) + "'>";
        html += "<td>View functions</td><td>" + viewFunctionTraces.totalPeriodInHRFormat + "</td>";
        html += "</tr>";

        jQuery.each( viewFunctionTraces.list, function ( i, viewFunctionTrace )
        {
            var childId = id + "-" + (i + 1);
            html += buildViewFunctionTrace( viewFunctionTrace, childId );
        } );
        return html;
    }

    function buildViewFunctionTrace( viewFunctionTrace, id )
    {
        var html = "";

        var windowRenderingTrace = null;
        if ( viewFunctionTrace.traces != null )
        {
            windowRenderingTrace = viewFunctionTrace.traces.list[0];
        }
        var windowRenderingTracePortletName = "?";
        if ( windowRenderingTrace != null )
        {
            windowRenderingTracePortletName = windowRenderingTrace.portletName;
        }

        html += "<tr id='node-" + id + "' class='child-of-node-" + resolveParentId( id ) + "'>";
        html += "<td>" + viewFunctionTrace.name + "( '" + windowRenderingTracePortletName + "' )</td><td>" +
            viewFunctionTrace.duration.asHRFormat + "</td>";
        html += "</tr>";

        html += "<tr id='node-" + id + "-1' class='child-of-node-" + id + "'>";
        html += "<td>Arguments:</td><td></td>";
        html += "</tr>";

        jQuery.each( viewFunctionTrace.arguments, function ( i, argument )
        {
            html += buildViewFunctionArgument( argument, id + "-1-" + (i + 1) );
        } );

        if ( windowRenderingTrace != null )
        {
            html += buildWindowRenderingTrace( windowRenderingTrace, id + "-2" );
        }

        return html;
    }

    function buildViewFunctionArgument( viewFunctionArgument, id )
    {
        var html = "";

        html += "<tr id='node-" + id + "' class='child-of-node-" + resolveParentId( id ) + "'>";
        html += "<td>" + viewFunctionArgument.name + "</td><td>" + viewFunctionArgument.value + "</td>";
        html += "</tr>";

        return html;
    }

    function buildPortalRequestType( type )
    {
        if ( type === "W" )
        {
            return "Window";
        }
        else if ( type === "P" )
        {
            return "Page";
        }
        else if ( type === "I" )
        {
            return "Image";
        }
        else if ( type === "A" )
        {
            return "Attachment";
        }
        else
        {
            return "Unknown";
        }
    }

    function buildDateTime( dateTime )
    {
        var date = new Date( dateTime );
        var html = "";
        html += date.toLocaleDateString() + " " + date.toLocaleTimeString();
        return html;
    }

    function buildUser( user )
    {
        if ( user == null )
        {
            return "?";
        }
        else if ( user.userStoreName != null )
        {
            return user.userStoreName + "/" + user.userName;
        }
        else
        {
            return user.userName;
        }
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

    function booleanToYesNo( b )
    {
        if ( b == null )
        {
            return "";
        }
        else if ( b )
        {
            return "Yes";
        }
        else
        {
            return "No";
        }
    }

    function emptyIfNull( s )
    {
        if ( s == null )
        {
            return "";
        }
        return s;
    }

    function resolveParentId( id )
    {
        // 1-2-1 -> 1-2
        var index = id.lastIndexOf( "-" );
        if ( index === -1 )
        {
            return null;
        }
        return id.slice( 0, index );
    }

    return {
        createPortalRequestDetailTable:function ( portalRequestTrace )
        {
            var html = "";
            html += "<table id='trace-details-tree-table'>";
            html += "<tr id='node-1'>";
            if ( portalRequestTrace.completedNumber === 0 )
            {
                html += "<td width='50%'>Request #</td><td>" + portalRequestTrace.requestNumber + "</td>";
            }
            else
            {
                html += "<td width='50%'>Completed #</td><td>" + portalRequestTrace.completedNumber + "</td>";
            }
            html += "</tr>";
            html += "<tr id='node-2'>";
            html += "<td>Started</td><td>" + buildDateTime( portalRequestTrace.duration.startTime ) + "</td>";
            html += "</tr>";
            html += "<tr id='node-3'>";
            html += "<td>Duration</td><td>" + portalRequestTrace.duration.asHRFormat + "</td>";
            html += "</tr>";
            html += "<tr id='node-4'>";
            html += "<td>Mode</td><td>" + portalRequestTrace.mode + "</td>";
            html += "</tr>";
            html += "<tr id='node-5'>";
            html += "<td>URL</td><td>" + portalRequestTrace.url + "</td>";
            html += "</tr>";
            html += "<tr id='node-6'>";
            html += "<td>Site</td><td>" + portalRequestTrace.siteName + "</td>";
            html += "</tr>";
            html += "<tr id='node-7'>";
            html += "<td>Type</td><td>" + buildPortalRequestType( portalRequestTrace.type ) + "</td>";
            html += "</tr>";
            html += "<tr id='node-8'>";
            html += "<td>Requester</td><td>" + buildUser( portalRequestTrace.requester ) + "</td>";
            html += "</tr>";

            // HTTP request
            html += "<tr id='node-9'>";
            html += "<td>HTTP Request:</td><td></td>";
            html += "<tr id='node-9-1' class='child-of-node-9'>";
            html += "<td>Remote address</td><td>" + portalRequestTrace.httpRequest.remoteAddress + "</td>";
            html += "</tr>";
            html += "<tr id='node-9-2' class='child-of-node-9'>";
            html += "<td>User agent</td><td>" + portalRequestTrace.httpRequest.userAgent + "</td>";
            html += "</tr>";
            html += "<tr id='node-9-3' class='child-of-node-9'>";
            html += "<td>Character encoding</td><td>" + portalRequestTrace.httpRequest.characterEncoding + "</td>";
            html += "</tr>";
            html += "<tr id='node-9-4' class='child-of-node-9'>";
            html += "<td>ContentType</td><td>" + emptyIfNull( portalRequestTrace.httpRequest.contentType ) + "</td>";
            html += "</tr>";

            if ( portalRequestTrace.pageRenderingTrace != null )
            {
                html += buildPageRenderingTrace( portalRequestTrace.pageRenderingTrace, "10" );
            }
            else if ( portalRequestTrace.windowRenderingTrace != null )
            {
                html += buildWindowRenderingTrace( portalRequestTrace.windowRenderingTrace, "10" );
            }
            else if ( portalRequestTrace.imageRequestTrace != null )
            {
                html += buildImageRequestTrace( portalRequestTrace.imageRequestTrace, "10" );
            }
            else if ( portalRequestTrace.attachmentRequestTrace != null )
            {
                html += buildAttachmentRequestTrace( portalRequestTrace.attachmentRequestTrace, "10" );
            }

            html += "</table>";

            return html;
        }
    };
};

