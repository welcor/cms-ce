[#ftl]

<h3>Query Result</h3>


<div class="measureTable">
    <table>
        <th>Key</th>
        <th>Value</th>
        [#list contentFields?keys as key]
        <tr>
            <td>
                ${key}
            </td>
            <td>
                ${contentFields[key]}
            </td>
        </tr>
        [/#list]
    </table>
</div>




