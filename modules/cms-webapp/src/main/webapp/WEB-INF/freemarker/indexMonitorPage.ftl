[#ftl]
[#import "indexMonitorLibrary.ftl" as lib/]
<html>
<head>
    <title>Index Monitor page</title>
     <link type="text/css" rel="stylesheet" href="css/admin.css"/>
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
            background-color:#EEEEEE;
        }

         .measureTable {
                    padding: 8px;
                    margin: 10px;
                    border: 1px dotted #000000;
                    background-color:#FFFFFF;
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

    </style>
</head>
<body>
    <h1>Info</h1>
    <div class="infoBox">
        <b>Elasticsearch index properties</b>
        <ul>
            <li><span class="keyField">Count</span> = <span class="valueField">${newIndexNumberOfContent}</span> </li>
        </ul>
    </div>
     <div class="infoBox">
     <h2>Query Measures <a href="${baseUrl}/adminpage?page=914&op=indexMonitor&clear=true"> [ Clear ] </a></h2>

              Order by
              <a href="${baseUrl}/adminpage?page=914&op=indexMonitor&orderby=AvgTime">[AvgTime]</a>
              <a href="${baseUrl}/adminpage?page=914&op=indexMonitor&orderby=AvgTimeDiff">[AvgTime diff]</a>
              <a href="${baseUrl}/adminpage?page=914&op=indexMonitor&orderby=TotalHits">[TotalHits]</a>

              [#list indexQueryMeasurerSnapshot as measure]
                  [@lib.queryMeasureDetails measure=measure/]
              [/#list]
    </div>

</body>
</html>
