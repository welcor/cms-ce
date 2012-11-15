<div>
<#list cacheList as x>
    <fieldset>
        <legend>${x.name?cap_first} Cache</legend>
        <div style="float: left">
            <table>
                <tr>
                    <td>
                        Time to live (seconds):
                    </td>
                    <td>${x.timeToLive}</td>
                </tr>
                <tr>
                    <td>
                        Max elements in memory:
                    </td>
                    <td>${x.memoryCapacity}</td>
                </tr>
                <tr>
                    <td>
                        Object count:
                    </td>
                    <td>${x.count}</td>
                </tr>
                <tr>
                    <td>
                        Capacity usage (memory):
                    </td>
                    <td>${x.memoryCapacityUsage} %</td>
                </tr>
                <tr>
                    <td>
                        Cache hits:
                    </td>
                    <td>${x.hitCount}</td>
                </tr>
                <tr>
                    <td>
                        Cache misses:
                    </td>
                    <td>${x.missCount}</td>
                </tr>
                <tr>
                    <td>
                        Cache clears:
                    </td>
                    <td>${x.removeAllCount}</td>
                </tr>
                <tr>
                    <td>
                        Cache effectiveness:
                    </td>
                    <td>${x.effectiveness} %</td>
                </tr>
            </table>
        </div>
        <div style="float: left; margin-left: 10px">
            <button onclick="clearCache('${x.name}')">Clear Cache</button>
        </div>
    </fieldset>
</#list>
    <br/>
</div>
