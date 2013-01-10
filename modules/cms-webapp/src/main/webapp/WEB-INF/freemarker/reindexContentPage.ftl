[#ftl]
<!DOCTYPE html>

<html lang="en">
<head>
    <title>Reindex Content Tool</title>

    <link  href="../css/admin.css" rel="stylesheet"/>
    <link  href="${baseUrl}/javascript/indexing.css" rel="stylesheet"/>
    <link  href="${baseUrl}/javascript/bootstrap-progressbar.css" rel="stylesheet">
    <script src="${baseUrl}/javascript/lib/jquery/jquery-1.7.2.min.js"></script>
    <script src="${baseUrl}/javascript/indexing.js"></script>

    <script>
    <!--
        function startReindex() {
            if (confirm("Are you sure you want to start the reindexing of all content?")) {
                reindex( "${baseUrl}/tools/reindexContent??op=custom&reindex=true", "${baseUrl}" );
            }
        }

        ${reindexInProgress?string("reindex(false, '${baseUrl}');", "")}
    //-->
    </script>
</head>

<body>
    <h1>Admin / <a href="${baseUrl}/adminpage?page=400&op=browse">Content types</a> / Reindex all content</h1>

    <div class="infoBox">
        <div>
            <strong>Reindexing of all content might take a long time, possibly affecting your live sites.</strong>
        </div>

        <div style="margin-top: 20px">
            <input type="button" class="button_text operation_button" id="startReindex" value="Start" onclick="startReindex()"/>
        </div>

        <div>
            <div class="progress" style="width: 300px; margin-top: 20px;">
                <div class="bar" style="width: 0"></div>
            </div>
        </div>

        <div id="message">Click start to reindex.</div>

        <div style="margin-top: 4px">
            <a id="view" href="${baseUrl}/tools/reindexContent??op=custom&info=logLines&back=reindexContent">View last reindex log</a>
        </div>
    </div>
</body>
</html>
