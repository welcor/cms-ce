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
        <th>New size</th>
        <th>Old size</th>
        <th>Diff</th>
        <tr>
            <td>
                ${entry.newResultContentKeys?size}
            </td>
            <td>
                ${entry.oldResultContentKeys?size}
            </td>
            <td>
                [#list entry.diff as contentKey]
                <span id="diffListDetails- ${contentKey}" onclick="queryContent( ${contentKey} )">  ${contentKey}<br></span>

                [/#list]
            </td>

        </tr>
    </table>
</div>
[/#macro]

