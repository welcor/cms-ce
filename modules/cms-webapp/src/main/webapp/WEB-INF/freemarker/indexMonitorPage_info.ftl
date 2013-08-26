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
                    Indexed documents:
                </td>
                <td>${numberOfDocuments}</td>
            </tr>
            <tr>
                <td>
                    * Contents:
                </td>
                <td>${numberOfContent} (of ${totalNumberOfContent})</td>
            </tr>
            <tr>
                <td>
                    * Attachments:
                </td>
                <td>${numberOfBinaries}</td>
            </tr>
            </tr>
        </table>
    </fieldset>
    <fieldset>
        <legend>Index storage</legend>
        <table>
            <tr>
                <td>
                    Primary shards storage size:
                </td>
                <td>${primaryStorageSize}</td>
            </tr>
            <tr>
                <td>
                    Total storage size:
                </td>
                <td>${totalStorageSize}</td>
            </tr>
        </table>
    </fieldset>
    <br/>
    <fieldset>
        <legend>Index health</legend>
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

<#if errors??>
    <fieldset>
        <legend>Errors</legend>
        <ul>
            <#list errors as entry>
                <li>${entry}</li>
            </#list>
        </ul>
    </fieldset>
    <br/>
</#if>
<#if validationFailures??>
    <fieldset>
        <legend>Validation Failures</legend>
        <ul>
            <#list validationFailures as entry>
                <li>${entry}</li>
            </#list>
        </ul>
    </fieldset>
</#if>
    <br/>
    <fieldset>
        <legend>Operations</legend>
        <input type="button" class="operation_button" name="startReindex" value="Reindex all content"
               onclick="startReindex()" ${reindexInProgress?string("disabled","")}/>
        <input type="button" class="operation_button" name="recreateIndex" value="Rebuild index (FULL)"
               onclick="recreateIndex()" ${reindexInProgress?string("disabled","")}/>
        <br/>
    <#if reindexInProgress>
        <div class="operation-bottom">
            <a href="${baseUrl}/tools/reindexContent?op=custom">Reindex in progress...</a><br/>
        </div>
    </#if>

    <#if lastReindexTime??>
        <div class="operation-bottom">
            Last reindex: ${lastReindexTime} ( took ${lastReindexTimeUsed} )
        </div>
    </#if>

    </fieldset>
</div>
