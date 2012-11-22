[#ftl]
[#if livePortalTraceEnabled == 0]
<html>
<body>
<h1>
    Admin / Live Portal Trace is not enabled!
</h1>
</body>
</html>
[#else]
<html>
<head>
    <title>Admin / Live Portal Trace </title>
    <script type="text/javascript" src="../javascript/lib/jquery/jquery-1.7.2.min.js"></script>
    <script type="text/javascript" src="../javascript/lib/jquery/jquery.sparkline.min-2.0.js"></script>
    <script type="text/javascript" src="../javascript/lib/jquery/jquery.treeTable.js"></script>
    <script type="text/javascript" src="liveportaltrace/Utility.js"></script>
    <script type="text/javascript" src="liveportaltrace/view/PortalRequestTraceDetailHtmlBuilder.js"></script>
    <script type="text/javascript" src="liveportaltrace/view/PortalRequestTraceRowView.js"></script>
    <script type="text/javascript" src="liveportaltrace/ctrl/PortalRequestTraceDetailController.js"></script>
    <script type="text/javascript" src="liveportaltrace/ctrl/CompletedPortalRequestsTableController.js"></script>
    <script type="text/javascript" src="liveportaltrace/ctrl/ReloadableTableController.js"></script>
    <script type="text/javascript" src="liveportaltrace/ctrl/SystemInfoController.js"></script>
    <script type="text/javascript" src="liveportaltrace/ctrl/AutomaticUpdateController.js"></script>
    <script type="text/javascript" src="liveportaltrace/ctrl/JavaMemoryGraphController.js"></script>
    <script type="text/javascript" src="liveportaltrace/ctrl/CompletedRequestsGraphController.js"></script>
    <script type="text/javascript" src="liveportaltrace/ctrl/PageCacheCapacityGraphController.js"></script>
    <script type="text/javascript" src="liveportaltrace/ctrl/EntityCacheCapacityGraphController.js"></script>
    <script type="text/javascript" src="../javascript/tabpane.js"></script>
    <link rel="stylesheet" type="text/css" href="../css/tools/jquery-ui-1.8.21.css"/>
    <link rel="stylesheet" type="text/css" href="liveportaltrace/jquery.treeTable.css"/>
    <link rel="stylesheet" type="text/css" href="liveportaltrace/live-portal-trace.css"/>
    <link type="text/css" rel="stylesheet" href="../css/admin.css"/>
    <link type="text/css" rel="StyleSheet" href="../javascript/tab.webfx.css"/>
</head>
<body>

<h1>Admin / Live Portal Trace</h1>

<table>
    <tr>
        <td style="margin-right: 10px">
            <input type="checkbox" checked="checked" id="auto-update" onclick="automaticUpdateController.switchAutomaticUpdate()"/><label
                for="auto-update">Auto update</label>
            &nbsp;&nbsp;&nbsp;System time: <span id="system-time">?</span>
            &nbsp;&nbsp;&nbsp;
            System up-time: <span id="system-up-time">?</span>
        </td>
    </tr>
    <tr>
        <td style="margin-right: 10px">

            <table id="system-info-table">
                <tr style="border-bottom: 1px solid #DDDDDD; border-top: 1px solid #DDDDDD">
                    <th class="system-info-group-name-td">
                        <a onclick="$('#entity-cache-details-row').toggle();" href="javascript: void(0);">
                            Entity cache
                        </a>
                    </th>
                    <td colspan="8"><span id="graph-entity-cache"></span></td>
                </tr>
                <tr id="entity-cache-details-row" style="display: none; background-color: #EEEEEE; border-bottom: 1px solid #DDDDDD">
                    <th class="system-info-group-name-td"></th>
                    <td class="system-info-label">count:</td>
                    <td class="system-info-value" id="entity-cache-count"></td>
                    <td class="system-info-label">eff.:</td>
                    <td class="system-info-value" id="entity-cache-effectiveness"></td>
                    <td class="system-info-label">hit count:</td>
                    <td class="system-info-value" id="entity-cache-hit-count"></td>
                    <td class="system-info-label">miss count:</td>
                    <td class="system-info-value" id="entity-cache-miss-count"></td>
                    <td class="system-info-label">capacity:</td>
                    <td class="system-info-value" id="entity-cache-capacity-count"></td>
                    <td class="system-info-label">cap. usage:</td>
                    <td class="system-info-value" id="entity-cache-capacity-usage"></td>
                </tr>
                <tr style="border-bottom: 1px solid #DDDDDD">
                    <th class="system-info-group-name-td">
                        <a onclick="$('#page-cache-details-row').toggle();" href="javascript: void(0);">
                            Page cache
                        </a>
                    </th>
                    <td colspan="8"><span id="graph-page-cache"></span></td>
                </tr>
                <tr id="page-cache-details-row" style="display: none; background-color: #EEEEEE; border-bottom: 1px solid #DDDDDD">
                    <th class="system-info-group-name-td"></th>
                    <td class="system-info-label">count:</td>
                    <td class="system-info-value" id="page-cache-count"></td>
                    <td class="system-info-label">eff.:</td>
                    <td class="system-info-value" id="page-cache-effectiveness"></td>
                    <td class="system-info-label">hit count:</td>
                    <td class="system-info-value" id="page-cache-hit-count"></td>
                    <td class="system-info-label">miss count:</td>
                    <td class="system-info-value" id="page-cache-miss-count"></td>
                    <td class="system-info-label">capacity:</td>
                    <td class="system-info-value" id="page-cache-capacity-count"></td>
                    <td class="system-info-label">cap. usage:</td>
                    <td class="system-info-value" id="page-cache-capacity-usage"></td>
                </tr>
                <tr style="border-bottom: 1px solid #DDDDDD">
                    <th class="system-info-group-name-td">
                        <a onclick="$('.java-memory-details-rows').toggle();" href="javascript: void(0);">
                            Java Memory
                        </a>
                    </th>
                    <td colspan="8"><span id="graph-memory"></span></td>
                </tr>
                <tr class="java-memory-details-rows" style="display: none; background-color: #EEEEEE; border-bottom: 1px solid #DDDDDD">
                    <th class="system-info-group-name-td">Heap</th>
                    <td class="system-info-label">used:</td>
                    <td class="system-info-value" id="java-heap-memory-usage-used"></td>
                    <td class="system-info-label">commited:</td>
                    <td class="system-info-value" id="java-heap-memory-usage-committed"></td>
                    <td class="system-info-label">max:</td>
                    <td class="system-info-value" id="java-heap-memory-usage-max"></td>
                    <td class="system-info-label">init:</td>
                    <td class="system-info-value" id="java-heap-memory-usage-init"></td>
                </tr>
                <tr class="java-memory-details-rows" style="display: none; background-color: #EEEEEE; border-bottom: 1px solid #DDDDDD">
                    <th class="system-info-group-name-td">
                        Non Heap
                    </th>
                    <td class="system-info-label">used:</td>
                    <td class="system-info-value" id="java-non-heap-memory-usage-used"></td>
                    <td class="system-info-label">commited:</td>
                    <td class="system-info-value" id="java-non-heap-memory-usage-committed"></td>
                    <td class="system-info-label">max:</td>
                    <td class="system-info-value" id="java-non-heap-memory-usage-max"></td>
                    <td class="system-info-label">init:</td>
                    <td class="system-info-value" id="java-non-heap-memory-usage-init"></td>
                </tr>
                <tr style="border-bottom: 1px solid #DDDDDD">
                    <th class="system-info-group-name-td">Misc.</th>
                    <td class="system-info-label">thread count:</td>
                    <td class="system-info-value" id="java-thread-count"></td>
                    <td class="system-info-label">peak thread count:</td>
                    <td class="system-info-value" id="java-thread-peak-count"></td>
                    <td class="system-info-label"></td>
                    <td class="system-info-value" id="data-source-open-connection-count"></td>
                    <td></td>
                    <td></td>
                </tr>
            </table>
        </td>
    </tr>
</table>

<div class="tab-pane" id="tab-main">

    <script type="text/javascript" language="JavaScript">
        var tabPane1 = new WebFXTabPane( document.getElementById( "tab-main" ), true );
    </script>

    <!-- Completed portal requests -->
    <div class="tab-page" id="tab-page-1">
        <span class="tab">Completed requests</span>
        <script type="text/javascript" language="JavaScript">
            tabPane1.addTabPage( document.getElementById( "tab-page-1" ) );
        </script>
        <button class="button_text" id="fetch-recent-history" onclick="completedPortalRequestsTableController.loadNew()" disabled="true">
            Fetch recent
        </button>
        <div>
            <span id="graph-completed-requests-pr-second"></span>
            last: <span id="last-number-of-completed-request-pr-second">0</span>,
            peak: <span id="peak-number-of-completed-request-pr-second">0</span>
        </div>
        <div class="listBox" style="height: 500px">
            <table id="completedPortalRequestTraces-table" class="trace-table" cellspacing="0">
                <thead>
                <tr>
                    <th style="width: 5%; text-align: center">#</th>
                    <th style="width: 5%; text-align: center">Type</th>
                    <th style="width: 45%">URL</th>
                    <th style="width: 20%; padding-left: 10px">Started</th>
                    <th style="width: 10%; text-align: right">Duration</th>
                    <th style="width: 15%; text-align: left; padding-left: 10px">Cache usage</th>
                </tr>
                </thead>
                <tbody id="newPastPortalRequestTraces-table-body">
                </tbody>
            </table>
        </div>
    </div>

    <!-- Current portal requests -->
    <div class="tab-page" id="tab-page-2">
        <span class="tab">Current portal requests (<span id="current-requests-tab-label"></span>)</span>

        <script type="text/javascript" language="JavaScript">
            tabPane1.addTabPage( document.getElementById( "tab-page-2" ) );
        </script>
        <button class="button_text" id="reloadCurrentPortalRequests" onclick="currentPageRequestsController.reload()">Refresh
        </button>
        <div class="listBox" style="height: 500px" id="window-current">
            <table id="currentPageRequests-table" class="trace-table" cellspacing="0">
                <thead>
                <tr>
                    <th style="width: 5%; text-align: center">#</th>
                    <th style="width: 5%; text-align: center">Type</th>
                    <th style="width: 45%">URL</th>
                    <th style="width: 20%; padding-left: 10px">Started</th>
                    <th style="width: 10%; text-align: right">Duration</th>
                    <th style="width: 15%; text-align: left; padding-left: 10px">Cache usage</th>
                </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
        </div>
    </div>


    <!-- Longest portal page requests -->
    <div class="tab-page" id="tab-page-3">
        <span class="tab">Longest page requests</span>
        <script type="text/javascript" language="JavaScript">
            tabPane1.addTabPage( document.getElementById( "tab-page-3" ) );
        </script>
        <button class="button_text" id="reloadLongestPortalPageRequests" onclick="longestPageRequestsController.reload()">Refresh
        </button>
        <button id="clearLongestPageRequestTraces" onclick="longestPageRequestsController.clear()">Clear</button>

        <div class="listBox" style="height: 500px" id="window-longest-pagerequests">
            <table id="longestPageRequests-table" class="trace-table" cellspacing="0">
                <thead>
                <tr>
                    <th style="width: 5%; text-align: center">#</th>
                    <th style="width: 5%; text-align: center">Type</th>
                    <th style="width: 45%">URL</th>
                    <th style="width: 20%; padding-left: 10px">Started</th>
                    <th style="width: 10%; text-align: right">Duration</th>
                    <th style="width: 15%; text-align: left; padding-left: 10px">Cache usage</th>
                </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
        </div>
    </div>

    <!-- Longest portal attachment requests -->
    <div class="tab-page" id="tab-page-4">
        <span class="tab">Longest attachment requests</span>
        <script type="text/javascript" language="JavaScript">
            tabPane1.addTabPage( document.getElementById( "tab-page-4" ) );
        </script>

        <button class="button_text" id="reloadLongestPortalAttachmentRequests"
                onclick="longestAttachmentRequestsController.reload()">Refresh
        </button>
        <button id="clearLongestAttachmentRequestTraces" onclick="longestAttachmentRequestsController.clear()">Clear</button>

        <div class="listBox" style="height: 500px" id="window-longest-attachmentrequests">
            <table id="longestAttachmentRequests-table" class="trace-table" cellspacing="0">
                <thead>
                <tr>
                    <th style="width: 5%; text-align: center">#</th>
                    <th style="width: 5%; text-align: center">Type</th>
                    <th style="width: 45%">URL</th>
                    <th style="width: 20%; padding-left: 10px">Started</th>
                    <th style="width: 10%; text-align: right">Duration</th>
                    <th style="width: 15%; text-align: left; padding-left: 10px">Cache usage</th>
                </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
        </div>
    </div>

    <!-- Longest portal image requests -->
    <div class="tab-page" id="tab-page-5">
        <span class="tab">Longest image requests</span>
        <script type="text/javascript" language="JavaScript">
            tabPane1.addTabPage( document.getElementById( "tab-page-5" ) );
        </script>
        <button class="button_text" id="reloadLongestPortalImageRequests" onclick="longestImageRequestsController.reload()">
            Refresh
        </button>
        <button id="clearLongestImageRequestTraces" onclick="longestImageRequestsController.clear()">Clear</button>

        <div class="listBox" style="height: 500px" id="window-longest-imagerequests">
            <table id="longestImageRequests-table" class="trace-table" cellspacing="0">
                <thead>
                <tr>
                    <th style="width: 5%; text-align: center">#</th>
                    <th style="width: 5%; text-align: center">Type</th>
                    <th style="width: 45%">URL</th>
                    <th style="width: 20%; padding-left: 10px">Started</th>
                    <th style="width: 10%; text-align: right">Duration</th>
                    <th style="width: 15%; text-align: left; padding-left: 10px">Cache usage</th>
                </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
        </div>
    </div>

</div>


<div id="portalRequestTraceDetail-window">
    Trace Details (<a href="javascript: void(0);"
                      onclick="portalRequestTraceDetailController.closePortalRequestTraceDetailWindow()">Close</a>)
    <!--<a href="javascript: void(0);" onclick="portalRequestTraceDetailController.expandAllTraceDetails()">expand all</a>
<a href="javascript: void(0);" onclick="portalRequestTraceDetailController.collapseAllTraceDetails()">collapse all</a>-->

    <div id="portalRequestTraceDetail-details">

    </div>
</div>

<p>
<blockquote style="font-style: italic;">
    Legend: <span class="cache-color-not-cacheable">O</span> not cacheable, <span class="cache-color-cache-hit">&radic;</span>
    cache hit, <span class="cache-color-cache-miss">X</span> cache miss, <span class="cache-color-cache-hit-blocked">&radic;</span> cache
    hit with concurrency block
</blockquote>
</p>

<script type="text/javascript">

    setupAllTabs();

    if ( !lpt )
    {
        var lpt = {};
    }

    lpt.resolveURLAndAddParams = function ( params )
    {
        return "livePortalTrace?" + params;
    };

    var portalRequestTraceDetailHtmlBuilder = lpt.PortalRequestTraceDetailHtmlBuilder();
    var portalRequestTraceRowView = lpt.PortalRequestTraceRowView();

    var portalRequestTraceDetailController = new lpt.PortalRequestTraceDetailController();
    portalRequestTraceDetailController.setPortalRequestTraceDetailHtmlBuilder( portalRequestTraceDetailHtmlBuilder );

    var completedRequestsGraphController = new lpt.CompletedRequestsGraphController();

    var completedPortalRequestsTableController = new lpt.CompletedPortalRequestsTableController( "completedPortalRequestTraces-table",
                                                                                                 1000 );
    completedPortalRequestsTableController.setWorkerThreadIsSupported( lpt.WorkerUtility.isWorkerSupported() );
    completedPortalRequestsTableController.setLoadCompletedAfterUrl( lpt.resolveURLAndAddParams( "history=true&completed-after=" ) );
    completedPortalRequestsTableController.setLoadCompletedBeforeUrl( lpt.resolveURLAndAddParams( "history=true&completed-before=" ) );
    completedPortalRequestsTableController.setCompletedRequestsGraphController( completedRequestsGraphController );
    completedPortalRequestsTableController.setPortalRequestTraceDetailController( portalRequestTraceDetailController );
    completedPortalRequestsTableController.setPortalRequestTraceRowView( portalRequestTraceRowView );
    completedPortalRequestsTableController.init();

    var currentPageRequestsController = new lpt.ReloadableTableController( "currentPageRequests-table", 2000 );
    currentPageRequestsController.setReloadUrl( lpt.resolveURLAndAddParams( "window=current" ) );
    currentPageRequestsController.setPortalRequestTraceDetailController( portalRequestTraceDetailController );
    currentPageRequestsController.setPortalRequestTraceRowView( portalRequestTraceRowView );
    currentPageRequestsController.init();

    var longestPageRequestsController = new lpt.ReloadableTableController( "longestPageRequests-table", 2000 );
    longestPageRequestsController.setReloadUrl( lpt.resolveURLAndAddParams( "window=longestpagerequests" ) );
    longestPageRequestsController.setClearUrl( lpt.resolveURLAndAddParams( "command=clear-longestpagerequests" ) );
    longestPageRequestsController.setPortalRequestTraceDetailController( portalRequestTraceDetailController );
    longestPageRequestsController.setPortalRequestTraceRowView( portalRequestTraceRowView );
    longestPageRequestsController.init();

    var longestAttachmentRequestsController = new lpt.ReloadableTableController( "longestAttachmentRequests-table", 2000 );
    longestAttachmentRequestsController.setReloadUrl( lpt.resolveURLAndAddParams( "window=longestattachmentrequests" ) );
    longestAttachmentRequestsController.setClearUrl( lpt.resolveURLAndAddParams( "command=clear-longestattachmentrequests" ) );
    longestAttachmentRequestsController.setPortalRequestTraceDetailController( portalRequestTraceDetailController );
    longestAttachmentRequestsController.setPortalRequestTraceRowView( portalRequestTraceRowView );
    longestAttachmentRequestsController.init();

    var longestImageRequestsController = new lpt.ReloadableTableController( "longestImageRequests-table", 2000 );
    longestImageRequestsController.setReloadUrl( lpt.resolveURLAndAddParams( "window=longestimagerequests" ) );
    longestImageRequestsController.setClearUrl( lpt.resolveURLAndAddParams( "command=clear-longestimagerequests" ) );
    longestImageRequestsController.setPortalRequestTraceDetailController( portalRequestTraceDetailController );
    longestImageRequestsController.setPortalRequestTraceRowView( portalRequestTraceRowView );
    longestImageRequestsController.init();

    currentPageRequestsController.reload();
    longestPageRequestsController.reload();
    longestAttachmentRequestsController.reload();
    longestImageRequestsController.reload();
    completedPortalRequestsTableController.reload();

    var pageCacheCapacityGraphController = new lpt.PageCacheCapacityGraphController();
    var entityCacheCapacityGraphController = new lpt.EntityCacheCapacityGraphController();
    var javaMemoryGraphController = new lpt.JavaMemoryGraphController();

    var systemInfoController = new lpt.SystemInfoController( 1000 );
    systemInfoController.setWorkerThreadIsSupported( lpt.WorkerUtility.isWorkerSupported() );
    systemInfoController.setRefreshUrl( lpt.resolveURLAndAddParams( "system-info=true" ) );
    systemInfoController.setPageCacheCapacityGraphController( pageCacheCapacityGraphController );
    systemInfoController.setEntityCacheCapacityGraphController( entityCacheCapacityGraphController );
    systemInfoController.setJavaMemoryGraphController( javaMemoryGraphController );
    systemInfoController.init();

    var automaticUpdateController = new lpt.AutomaticUpdateController();
    automaticUpdateController.setCompletedPortalRequestsTableController( completedPortalRequestsTableController );
    automaticUpdateController.setCurrentPageRequestsController( currentPageRequestsController );
    automaticUpdateController.setLongestPageRequestsController( longestPageRequestsController );
    automaticUpdateController.setLongestAttachmentRequestsController( longestAttachmentRequestsController );
    automaticUpdateController.setLongestImageRequestsController( longestImageRequestsController );
    automaticUpdateController.setSystemInfoController( systemInfoController );
    automaticUpdateController.startAutomaticUpdate();

</script>

</body>
</html>
[/#if]
