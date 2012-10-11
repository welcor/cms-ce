[#ftl]
<html>
<head>
    <title>Cluster Info Page</title>
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
        <h2>Node: ${nodeName}: Enabled: ${isEnabled}</h2>
        <h2>Members</h2>
        <ul>
            [#list members as clusterMember]
             <li>${clusterMember}</li>
            [/#list]
        </ul>
    </div>

</div>
</body>
</html>
