<div>
    <table>
        <tr>
            <td>Cluster Name:</td>
            <td>${clusterName}</td>
        </tr>
    </table>
<#list nodeList?sort as x>
    <fieldset>
        <legend>${x.name}</legend>

        <#if x.isMaster>
        <div style="float: left;background: lightgreen">
        <#else>
        <div style="float: left">
        </#if>

        <div style="float: left">
            <table>
                <tr>
                    <td>
                        Node:
                    </td>
                    <td>${x.name}</td>
                </tr>
                <tr>
                    <td>
                        Hostname:
                    </td>
                    <td>${x.hostName}</td>
                </tr>
                <tr>
                    <td>
                        Master:
                    </td>
                    <td>${x.isMaster?string}</td>
                </tr>
                <tr>
                    <td>
                        Transport Address:
                    </td>
                    <td>${x.transportAddress}</td>
                </tr>
                <tr>
                    <td>
                        JVM version:
                    </td>
                    <td>${x.jvmVersion}</td>
                </tr>
                <tr>
                    <td>
                        JVM max memory:
                    </td>
                    <td>${x.jvmDirectMemoryMax}</td>
                </tr>
                <tr>
                    <td>
                        JVM heap memory:
                    </td>
                    <td>${x.jvmHeapMax} (${x.jvmHeapInit} initial)</td>
                </tr>
                <tr>
                    <td>
                        JVM non-heap memory:
                    </td>
                    <td>${x.jvmNonHeapMax} (${x.jvmNonHeapInit} initial)</td>
                </tr>
            </table>
        </div>
    </fieldset>
</#list>
    <br/>
</div>
