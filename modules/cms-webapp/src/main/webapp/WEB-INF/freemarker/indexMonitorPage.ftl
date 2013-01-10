<!DOCTYPE html>
<html>
<head>
    <title>Index Monitor</title>
    <script type="text/javascript" src="../javascript/lib/jquery/jquery-1.6.2.min.js"></script>
    <script type="text/javascript" src="../javascript/tabpane.js"></script>
    <link type="text/css" rel="StyleSheet" href="../javascript/tab.webfx.css"/>
    <link type="text/css" rel="stylesheet" href="../css/admin.css"/>

    <link  href="${baseUrl}/javascript/indexing.css" rel="stylesheet"/>
    <link  href="${baseUrl}/javascript/bootstrap-progressbar.css" rel="stylesheet">
    <script src="${baseUrl}/javascript/indexing.js"></script>

    <#if reindexInProgress>
    <script>
    <!--
        reindex(false, '${baseUrl}');
    //-->
    </script>
    </#if>


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
                // location.href = "${baseUrl}/tools/reindexContent?op=custom&recreateIndex=true&reindex=true";
                reindex( "${baseUrl}/tools/reindexContent??op=custom&recreateIndex=true&reindex=true", "${baseUrl}" );
            }
        }

        function startReindex() {
            if (confirm("Reindex all content now?")) {
                // location.href = '${baseUrl}/tools/reindexContent??op=custom&reindex=true';
                reindex( "${baseUrl}/tools/reindexContent??op=custom&reindex=true", "${baseUrl}" );
            }
        }
    </script>

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

        <fieldset>
            <legend>Operations</legend>
            <input type="button" class="operation_button" name="startReindex" value="Reindex all content"
                   onclick="startReindex()" ${reindexInProgress?string("disabled","")}/>
            <input type="button" class="operation_button" name="recreateIndex" value="Rebuild index (FULL)"
                   onclick="recreateIndex()" ${reindexInProgress?string("disabled","")}/>
            <br/>

            <div>
                <div class="progress" style="width: 300px; margin-top: 20px;">
                    <div class="bar" style="width: 0"></div>
                </div>
            </div>

            <div id="message" class="operation-bottom">Click some button to start</div>

            <div class="operation-bottom">
                <a href="${baseUrl}/tools/reindexContent?op=custom&info=logLines&back=indexMonitor">View last reindex log</a><br/>
            </div>
        </fieldset>
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

