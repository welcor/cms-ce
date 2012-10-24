package com.enonic.cms.core.portal.livetrace;


import java.util.ArrayList;
import java.util.List;

public class DatasourceExecutionTrace
    extends BaseTrace
    implements ContentIndexQuerier, RelatedContentFetcher, Trace
{
    private String methodName;

    private MaxLengthedString runnableCondition = new MaxLengthedString();

    private boolean isExecuted;

    private boolean isCacheUsed = false;

    private List<DatasourceMethodArgument> datasourceMethodArgumentList = new ArrayList<DatasourceMethodArgument>();

    private Traces<ClientMethodExecutionTrace> clientMethodExecutionTraceTraces = new Traces<ClientMethodExecutionTrace>();

    private Traces<ContentIndexQueryTrace> contentIndexQueryTraces = new Traces<ContentIndexQueryTrace>();

    private Traces<RelatedContentFetchTrace> relatedContentFetchTraces = new Traces<RelatedContentFetchTrace>();

    DatasourceExecutionTrace( String methodName )
    {
        this.methodName = methodName;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getMethodName()
    {
        return methodName;
    }

    @SuppressWarnings("UnusedDeclaration")
    public boolean isExecuted()
    {
        return isExecuted;
    }

    void setExecuted( boolean executed )
    {
        isExecuted = executed;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getRunnableCondition()
    {
        return runnableCondition != null ? runnableCondition.toString() : null;
    }

    void setRunnableCondition( String runnableCondition )
    {
        this.runnableCondition = new MaxLengthedString( runnableCondition );
    }

    @SuppressWarnings("UnusedDeclaration")
    public boolean isCacheUsed()
    {
        return isCacheUsed;
    }

    void setCacheUsed( boolean cacheUsed )
    {
        isCacheUsed = cacheUsed;
    }

    void addDatasourceMethodArgument( DatasourceMethodArgument datasourceMethodArgument )
    {
        datasourceMethodArgumentList.add( datasourceMethodArgument );
    }

    @SuppressWarnings("UnusedDeclaration")
    public List<DatasourceMethodArgument> getDatasourceMethodArguments()
    {
        return datasourceMethodArgumentList;
    }

    void addClientMethodExecutionTrace( ClientMethodExecutionTrace trace )
    {
        clientMethodExecutionTraceTraces.add( trace );
    }

    @SuppressWarnings("UnusedDeclaration")
    public Traces<ClientMethodExecutionTrace> getClientMethodExecutionTraces()
    {
        return clientMethodExecutionTraceTraces;
    }

    @Override
    public void addContentIndexQueryTrace( ContentIndexQueryTrace trace )
    {
        contentIndexQueryTraces.add( trace );
    }

    @Override
    public void addRelatedContentFetchTrace( final RelatedContentFetchTrace trace )
    {
        relatedContentFetchTraces.add( trace );
    }

    @SuppressWarnings("UnusedDeclaration")
    public Traces<ContentIndexQueryTrace> getContentIndexQueryTraces()
    {
        return contentIndexQueryTraces;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Traces<RelatedContentFetchTrace> getRelatedContentFetchTraces()
    {
        return relatedContentFetchTraces;
    }
}
