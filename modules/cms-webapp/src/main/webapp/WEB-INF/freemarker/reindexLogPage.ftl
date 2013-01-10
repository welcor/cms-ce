[#ftl]
<!DOCTYPE html>

<html lang="en">

<head>
    <title>Reindex Content Tool</title>
    [#if reindexInProgress == true]
    <meta http-equiv="refresh" content="3"/>
    [/#if]
    <link href="../css/admin.css" rel="stylesheet"/>
    <link href="${baseUrl}/javascript/indexing.css" rel="stylesheet"/>
</head>

<body>

    <h1>Admin /

    [#if back == 'reindexContent']
        <a href="${baseUrl}/adminpage?page=400&op=browse">Content types</a>
    [/#if]

    [#if back == 'indexMonitor']
        System
    [/#if]

    / Reindex log messages</h1>

    <div class="infoBox" >
        <b>${reindexInProgress?string("Log Messages", "Last Log Messages")}</b>
        <div class="messages monospace">
            [#list reindexLog as entry]
                ${entry}<br/>
            [/#list]
        </div>
    </div>

    <input type="button" value="Back" onclick="location.href = '${baseUrl}/tools/${back}'">

</body>
</html>
