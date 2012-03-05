[#ftl]
[#import "indexMonitorLibrary.ftl" as lib/]

<h3>Queries with diff</h3>

[#list queryResultDiffList as entry]
[@lib.queryDiffDetails entry=entry/]
[/#list]

