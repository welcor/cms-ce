[#ftl]
[#import "indexMonitorLibrary.ftl" as lib/]
<html>
<head>
    <title>Index Monitor page</title>
    <script type="text/javascript" src="javascript/lib/jquery/jquery-1.6.2.min.js"></script>
    <link type="text/css" rel="stylesheet" href="css/admin.css"/>
    <script type="text/javascript" src="javascript/tabpane.js"></script>
    <link type="text/css" rel="StyleSheet" href="javascript/tab.webfx.css"/>
    <style type="text/css">
        h1 {
            font-size: 22pt;
        }

        body {
            font-size: 12pt;
        }

        .infoBox {
            font-size: 10pt;
            padding: 8px;
            margin: 10px;
            border: 1px dotted #000000;
            background-color: #EEEEEE;
        }

        .measureTable {
            padding: 8px;
            margin: 10px;
            border: 1px dotted #000000;
            background-color: #FFFFFF;
            font-size: 8pt;
        }

        .infoBox tt {
            font-size: 10pt;
        }

        .keyField {
            color: #000080
        }

        .valueField {
            color: #008000
        }

        #queryContent-window {
            background-color: #d3d3d3;
            border: 2px solid #A0A0A0;
            border-radius: 4px;
            padding: 5px;
            position: fixed;
            width: 50%;
            max-height: 60%;
            overflow-y: auto;
            overflow-x: auto;
            top: 10px;
            right: 10px;
            display: none;
            z-index: 999;
        }

    </style>
</head>
<body>
<h1>Info</h1>

<div class="tab-pane" id="tab-main">

    <script type="text/javascript" language="JavaScript">
        var tabPane1 = new WebFXTabPane( document.getElementById( "tab-main" ), true );
    </script>

    <script type="text/javascript" language="JavaScript">


        function queryContent(contentKey) {
            openQueryContentWindow();
            $( '#queryContent' ).load( "${baseUrl}/adminpage?page=914&op=indexMonitor&queryContent=true&contentKey=" + contentKey );
        }

        function closeQueryContentWindow()
        {
            $( "#queryContent-window" ).hide();
        }

        function openQueryContentWindow()
        {
            $( "#queryContent-window" ).show();
        }

        function loadMeasureList( orderBy )
        {
            $( '#measuresList' ).load( "${baseUrl}/adminpage?page=914&op=indexMonitor&measuresList=true&orderby=" + orderBy );
        }

        function loadDiffList()
        {
            $( '#diffList' ).load( "${baseUrl}/adminpage?page=914&op=indexMonitor&diffList=true" );
        }


        function loadQueryWindow()
        {
            $( '#queryContent' ).load( "${baseUrl}/adminpage?page=914&op=indexMonitor&queryContent=true&contentKey=47194" );
        }


        $( document ).ready( function ()
                             {
                                 loadMeasureList( 'AvgTime' );
                                 loadDiffList();
                                 loadQueryWindow();

                                 $( '#btnAvgTimeSort' ).click( function ()
                                                               {
                                                                   loadMeasureList( 'AvgTime' );
                                                               } );

                                 $( '#btnMaxTimeSort' ).click( function ()
                                                               {
                                                                   loadMeasureList( 'MaxTime' );
                                                               } );

                                 $( '#btnAvgTimeDiffSort' ).click( function ()
                                                                   {
                                                                       loadMeasureList( 'AvgTimeDiff' );
                                                                   } );

                                 $( '#btnTotalHitsSort' ).click( function ()
                                                                 {
                                                                     loadMeasureList( 'TotalHits' );
                                                                 } );
                                 $( '#btnRefreshDiffList' ).click( function ()
                                                                   {
                                                                       loadDiffList();
                                                                   } );
                                 $( '#btnQueryContent' ).click( function ()
                                                                   {
                                                                       loadQueryWindow();
                                                                   } );

                             } );

    </script>


    <div class="tab-page" id="tab-page-1">
        <span class="tab">Elasticsearch Index Properties</span>

        <h2>Index types</h2>
        <ul>
            <li><span class="keyField">Content</span> = <span class="valueField">(${newIndexNumberOfContent})</span></li>
            <li><span class="keyField">Binaries</span> = <span class="valueField">(0)</span></li>
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
