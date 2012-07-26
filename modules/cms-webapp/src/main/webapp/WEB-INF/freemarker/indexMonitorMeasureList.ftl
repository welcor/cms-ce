[#ftl]
[#import "indexMonitorLibrary.ftl" as lib/]

<h3>Total hits on index: ${totalHitsOnIndex}</h3>

<h3>Recorded queries: ${numberOfRecoredQueries} <a href="${baseUrl}/adminpage?page=914&op=indexMonitor&clear=true"> [ Clear
    ] </a></h3>

[#list indexQueryMeasurerSnapshot as measure]
[@lib.queryMeasureDetails measure=measure/]
[/#list]

