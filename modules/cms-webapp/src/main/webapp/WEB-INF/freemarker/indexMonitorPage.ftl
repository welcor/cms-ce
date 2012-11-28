<!DOCTYPE html>
<html>
<head>
    <title>Cache Info Page</title>
    <script type="text/javascript" src="../javascript/lib/jquery/jquery-1.6.2.min.js"></script>
    <script type="text/javascript" src="../javascript/tabpane.js"></script>
    <link type="text/css" rel="StyleSheet" href="../javascript/tab.webfx.css"/>
    <link type="text/css" rel="stylesheet" href="../css/admin.css"/>
</head>
<body>
<h1>Admin / System / Index Monitor</h1>

<div class="tab-pane" id="tab-main">

    <script type="text/javascript" language="JavaScript">
        var tabPane1 = new WebFXTabPane(document.getElementById("tab-main"), true);
        var baseUrl = "${baseUrl}";
    </script>

    <div class="tab-page" id="tab-page-1">
        <span class="tab">Index Monitor</span>

        <div id="indexInfo">
        </div>
    </div>
</div>

<script type="text/javascript">
    function loadData() {
        $('#indexInfo').load('${baseUrl}/tools/indexMonitor?op=info');
        setTimeout(loadData, 10000);
    }

    loadData();
</script>

</body>
</html>

