[#ftl]

[#macro queryMeasureDetails measure]
<div class="measureTable">
${measure.querySignature.queryDisplayValue}"
    <table>
        <th>SourceName</th>
        <th>TotalHits</th>
        <th>AvgTime</th>
        <th>MaxTime</th>
        <th>MinTime</th>
        [#list measure.sourceStats?keys as key]
            <tr>
                <td> ${key}</td>
                <td> ${measure.sourceStats[key].totalHits}</td>
                <td> ${measure.sourceStats[key].avgTime}</td>
                <td> ${measure.sourceStats[key].maxTime}</td>
                <td> ${measure.sourceStats[key].minTime}</td>
            </tr>
        [/#list]
    </table>
</div>
[/#macro]

[#macro queryDiffDetails entry]
<div class="measureTable">
${entry.querySignature.queryDisplayValue}"
    <table>
        <th>New only (count: ${entry.newSize}, total : ${entry.newTotalCount})</th>
        <th>Old only (count: ${entry.oldSize}, total : ${entry.oldTotalCount})</th>
        <th>Full ES-query</th>
        <th>Full Hibernate-query</th>
        <tr>
            <td>
                [#list entry.inNewOnly as contentKey]
                    <span id="diffListDetails- ${contentKey}" onclick="queryContent( ${contentKey} )">  ${contentKey}<br></span>
                [/#list]
            </td>
            <td>
                [#list entry.inOldOnly as contentKey]
                    <span id="diffListDetails- ${contentKey}" onclick="queryContent( ${contentKey} )">  ${contentKey}<br></span>
                [/#list]
            </td>
            <td>
                ${entry.elasticSearchQuery}
            </td>
            <td>
                ${entry.hibernateQuery}
            </td>

        </tr>
    </table>
</div>
[/#macro]

