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

    <script type="text/javascript">
        <!--
        function jumpToLast() {
            location.href = "#last";
        }

        function doDeleteIndex() {
            if (confirm("Are you sure you want to recreate the index? All data will be deleted and a full reindex will be needed")) {
                location.href = "servlet/tools/com.enonic.cms.core.tools.IndexMonitorController?op=custom&recreateIndex=true";
            }
        }
        //-->
    </script>

</head>
<body>
<h1>Info</h1>

<div class="tab-pane" id="tab-main">

    <script type="text/javascript" language="JavaScript">
        var tabPane1 = new WebFXTabPane(document.getElementById("tab-main"), true);
        var baseUrl = "${baseUrl}";
    </script>

    <div class="tab-page" id="tab-page-1">
        <span class="tab">Elasticsearch Index Properties</span>

    [#if indexExists?? && indexExists == true]
        <input type="button" class="button_text" name="recreateIndex" value="Recreate index" onclick="doDeleteIndex()"/>
    [/#if]

    [#if indexExists??]
        <h2>Node status</h2>
        <ul>
            <li>
                [#if indexExists == true]
                    <span class="keyField">Index exists:</span> = <span class="valueField">TRUE</span>
                [#else]
                    <span class="keyField">Index exists:</span> = <span class="valueField">FALSE</span>
                [/#if]
            </li>
        </ul>
    [/#if]

    [#if error??]
        <h2>Errors</h2>
        <ul>
            <li><span class="keyField">Error</span> = <span class="valueField">(${error})</span></li>
        </ul>
    [/#if]

    [#if numberOfContent?? && numberOfBinaries??]
        <h2>Index types</h2>
        <ul>
            <li><span class="keyField">Content</span> = <span class="valueField">(${numberOfContent})</span></li>
            <li><span class="keyField">Binaries</span> = <span class="valueField">(${numberOfBinaries})</span></li>
        </ul>
    [/#if]

        <h2>Cluster health</h2>
        <ul>
            <li><span class="keyField">Cluster status</span> = <span class="valueField">(${clusterStatus})</span></li>
            </li>
            <li><span class="keyField">Nodes</span> = <span class="valueField">(${numberOfNodes})</span></li>
            </li>
            <li><span class="keyField">Active Shards</span> = <span class="valueField">(${activeShards})</span></li>
            </li>
            <li><span class="keyField">Active primary shards</span> = <span class="valueField">(${activePrimaryShards})</span></li>
            </li>
            <li><span class="keyField">Relocation shards</span> = <span class="valueField">(${relocatingShards})</span></li>
            </li>
            <li><span class="keyField">Unassigned shards</span> = <span class="valueField">(${unassignedShards})</span></li>
            </li>
        </ul>

        <h2>Validation Failures:</h2>
        <ul>
        [#list validationFailures as entry]
            <li>${entry}</li>
        [/#list]
        </ul>

    </div>

</div>
</body>
</html>
