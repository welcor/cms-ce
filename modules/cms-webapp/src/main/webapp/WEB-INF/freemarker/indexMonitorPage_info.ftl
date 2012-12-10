<div>
    <fieldset>
        <legend>Index status</legend>
        <table>
            <tr>
                <td>
                    Index exists:
                </td>
                <td>${indexExists?string("yes", "no")}
                </td>
            </tr>
            <tr>
                <td>
                    Number of indexed contents:
                </td>
                <td>${numberOfContent}</td>
            </tr>
            <tr>
                <td>
                    Number of indexed attachments:
                </td>
                <td>${numberOfBinaries}</td>
            </tr>
        </table>
    </fieldset>
    <br/>
<#if error??>
    <fieldset>
        <legend>Errors</legend>
        <table>
            <tr>
                <td>
                    Error:
                </td>
                <td>${error}</td>
            </tr>
        </table>
    </fieldset>
    <br/>
</#if>
    <fieldset>
        <legend>Cluster health</legend>
        <table>
            <tr>
                <td>
                    Status:
                </td>
                <td>${clusterStatus}</td>
            </tr>
            <tr>
                <td>
                    Nodes:
                </td>
                <td>${numberOfNodes}</td>
            </tr>
            <tr>
                <td>
                    Active shards:
                </td>
                <td>${activeShards}</td>
            </tr>
            <tr>
                <td>
                    Active primary shards:
                </td>
                <td>${activePrimaryShards}</td>
            </tr>
            <tr>
                <td>
                    Relocation shards:
                </td>
                <td>${relocatingShards}</td>
            </tr>
            <tr>
                <td>
                    Unassigned shards:
                </td>
                <td>${unassignedShards}</td>
            </tr>
        </table>
    </fieldset>
    <br/>
    <fieldset>
        <legend>Validation Failures</legend>
        <ul>
        <#list validationFailures as entry>
            <li>${entry}</li>
        </#list>
        </ul>
    </fieldset>
    <br/>
    <fieldset>
        <legend>Operations</legend>
        <input type="button" class="button_text" name="startReindex" value="Reindex all content"
               onclick="startReindex()" ${reindexInProgress?string("disabled","")}/>
        <input type="button" class="button_text" name="recreateIndex" value="Rebuild index"
               onclick="recreateIndex()" ${reindexInProgress?string("disabled","")}/>
    <#if reindexInProgress>
        <a href="${baseUrl}/tools/reindexContent?op=custom"> Reindex in progress</a>
    </#if>
    </fieldset>
</div>
