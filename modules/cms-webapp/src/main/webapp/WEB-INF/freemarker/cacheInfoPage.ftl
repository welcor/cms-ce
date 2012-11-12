<!DOCTYPE html>
<html>
<head>
    <title>Cache Info Page</title>
    <script type="text/javascript" src="../javascript/lib/jquery/jquery-1.6.2.min.js"></script>
    <script type="text/javascript" src="../javascript/tabpane.js"></script>
    <script type="text/javascript" src="../javascript/lib/knockout/knockout-2.1.0.js"></script>
    <link type="text/css" rel="StyleSheet" href="../javascript/tab.webfx.css"/>
    <link type="text/css" rel="stylesheet" href="../css/admin.css"/>
</head>
<body>
<h1>Admin / System / Cache Info</h1>

<div class="tab-pane" id="tab-main">

    <script type="text/javascript" language="JavaScript">
        var tabPane1 = new WebFXTabPane(document.getElementById("tab-main"), true);
        var baseUrl = "${baseUrl}";
    </script>

    <div class="tab-page" id="tab-page-1">
        <span class="tab">Caches</span>

    <#assign caches = ["page", "entity", "image", "xslt", "localization"]>
    <#list caches as x>
        <fieldset>
            <legend>${x?cap_first} Cache</legend>
            <div data-bind="template: { name: 'cache-template', data: ${x} }" style="float: left"></div>
            <div style="float: left; margin-left: 10px">
                <button onclick="clearCache('${x}')">Clear Cache</button><br/>
                <button onclick="clearStatistics('${x}')">Clear Statistics</button>
            </div>
        </fieldset>
        <br/>
    </#list>

    </div>
</div>

<script type="text/html" id="cache-template">
    <table>
        <tr>
            <td>
                Time to live (seconds):
            </td>
            <td data-bind="text: timeToLive">
                &nbsp;
            </td>
        </tr>
        <tr>
            <td>
                Max elements in memory:
            </td>
            <td data-bind="text: memoryCapacity">
                &nbsp;
            </td>
        </tr>
        <tr>
            <td>
                Object count:
            </td>
            <td data-bind="text: objectCount">
                &nbsp;
            </td>
        </tr>
        <tr>
            <td>
                Capacity usage (memory):
            </td>
            <td>
                <span data-bind="text: memoryCapacityUsage"></span> %
            </td>
        </tr>
        <tr>
            <td>
                Cache hits:
            </td>
            <td data-bind="text: cacheHits">
                &nbsp;
            </td>
        </tr>
        <tr>
            <td>
                Cache misses:
            </td>
            <td data-bind="text: cacheMisses">
                &nbsp;
            </td>
        </tr>
        <tr>
            <td>
                Cache clears:
            </td>
            <td data-bind="text: cacheClears">
                &nbsp;
            </td>
        </tr>
        <tr>
            <td>
                Cache effectiveness:
            </td>
            <td>
                <span data-bind="text: cacheEffectiveness"></span> %
            </td>
        </tr>
    </table>
</script>

<script type="text/javascript">
    function clearCache(id) {
        sendOperation(id, "clear-cache");
    }

    function clearStatistics(id) {
        sendOperation(id, "clear-statistics");
    }

    function sendOperation(id, op) {
        var request = $.ajax({
            type: "POST",
            url: "${baseUrl}/tools/cacheInfo?cache=" + id + "&op=" + op
        });

        request.done();
        loadData();
    }

    function loadData() {
        var request = $.ajax({
            type: "GET",
            datatype: "json",
            url: "${baseUrl}/tools/cacheInfo?op=info"
        });

        request.done(function (response) {
            ko.applyBindings(response);
        });

        setTimeout(loadData, 5000);
    }

    loadData();
</script>

</body>
</html>
