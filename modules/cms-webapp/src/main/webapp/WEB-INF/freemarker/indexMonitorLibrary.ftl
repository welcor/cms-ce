[#ftl]

[#macro queryMeasureDetails measure]
    <div class="measureTable">
    ${measure.query}"
    <table>
    <th>SourceName</th><th>TotalHits</th><th>AvgTime</th><th>MaxTime</th><th>MinTime</th>
     [#list measure.sourceStats as sourceStat]
         <tr>
         <td>${sourceStat.sourceName}</td>
         <td>${sourceStat.totalHits}</td>
         <td>${sourceStat.avgTime}</td>
         <td>${sourceStat.maxTime}</td>
         <td>${sourceStat.minTime}</td>
         </tr>
     [/#list]
     </table>
    </div>
[/#macro]