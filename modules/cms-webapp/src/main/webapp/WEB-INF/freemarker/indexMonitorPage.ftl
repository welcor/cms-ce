<!DOCTYPE html>
<html>
<head>
    <title>Index Monitor</title>
    <script type="text/javascript" src="../javascript/lib/jquery/jquery-1.6.2.min.js"></script>
    <script type="text/javascript" src="../javascript/tabpane.js"></script>
    <link type="text/css" rel="StyleSheet" href="../javascript/tab.webfx.css"/>
    <link type="text/css" rel="stylesheet" href="../css/admin.css"/>

    <style type="text/css">
        .operation_button {
            margin-right: 5px;
        }

    </style>

    <script type="text/javascript">
        function recreateIndex() {
            if (confirm("WARNING: Are you sure you want to rebuild the index?\n\n" +
                        "All data will be deleted, and a full reindex will be done. " +
                        "This will affect your live sites by making content not available until it has been reindexed.")) {
                location.href = "reindexContent?op=custom&recreateIndex=true&reindex=true";
            }
        }

        function startReindex() {
            if (confirm("Reindex all content now?")) {
                location.href = 'reindexContent??op=custom&reindex=true';
            }
        }
    </script>

</head>
<body>
<h1>Admin / System / Index Monitor</h1>

<div class="tab-pane" id="tab-main">

    <script type="text/javascript" language="JavaScript">
        var tabPane1 = new WebFXTabPane(document.getElementById("tab-main"), true);
    </script>

    <div class="tab-page" id="tab-page-1">
        <span class="tab">Index Monitor</span>

        <div id="indexInfo">
        </div>
    </div>
</div>

<script type="text/javascript">
    function loadData() {
        $('#indexInfo').load('indexMonitor?op=info');
        setTimeout(loadData, 10000);
    }

    loadData();
</script>

</body>
</html>

