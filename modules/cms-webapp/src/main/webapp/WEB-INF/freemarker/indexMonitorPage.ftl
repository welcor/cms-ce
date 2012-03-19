[#ftl]
[#import "indexMonitorLibrary.ftl" as lib/]
<html>
<head>
    <title>Index Monitor page</title>
    <script type="text/javascript" src="javascript/lib/jquery/jquery-1.6.2.min.js"></script>
    <script type="text/javascript" src="javascript/tabpane.js"></script>
    <script type="text/javascript" src="indexmonitor/indexmonitor.js"></script>
    <link type="text/css" rel="stylesheet" href="css/admin.css"/>
    <link type="text/css" rel="StyleSheet" href="javascript/tab.webfx.css"/>
    <link rel="stylesheet" type="text/css" href="indexmonitor/indexmonitor.css"/>
 </head>
<body>
<h1>Info</h1>

<div class="tab-pane" id="tab-main">

    <script type="text/javascript" language="JavaScript">
        var tabPane1 = new WebFXTabPane( document.getElementById( "tab-main" ), true );

        var baseUrl = "${baseUrl}";

    </script>

    <div class="tab-page" id="tab-page-1">
        <span class="tab">Elasticsearch Index Properties</span>

        <h2>Index types</h2>
        <ul>
            <li><span class="keyField">Content</span> = <span class="valueField">(${numberOfContent})</span></li>
            <li><span class="keyField">Binaries</span> = <span class="valueField">(${numberOfBinaries})</span></li>
        </ul>
    </div>
    <div class="tab-page" id="tab-page-2">
        <span class="tab">Elasticsearch Queries</span>

        <div class="infoBox">
            Order by
            <a id="btnAvgTimeSort" href="javascript:;">[AvgTime]</a>
            <a id="btnMaxTimeSort" href="javascript:;">[MaxTime]</a>
            <a id="btnAvgTimeDiffSort" href="javascript:;">[AvgTime diff]</a>
            <a id="btnTotalHitsSort" href="javascript:;">[TotalHits]</a>

            <div id="measuresList">
            </div>

        </div>
    </div>
    <div class="tab-page" id="tab-page-3">
        <span class="tab">Resultset Diff</span>

        <a id="btnRefreshDiffList" href="javascript:;">[Refresh]</a>

        <div class="infoBox">
            <div id="diffList">
            </div>
        </div>
    </div>

    <div id="queryContent-window">
        <div>
           Content Index Details (<a href="javascript: void(0);" onclick="closeQueryContentWindow()">Close</a>)
            <div>
                <div id="queryContent">

                </div>
            </div>
        </div>
    </div>


</div>
</body>
</html>
