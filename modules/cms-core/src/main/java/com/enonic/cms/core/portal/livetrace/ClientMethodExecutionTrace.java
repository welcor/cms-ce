package com.enonic.cms.core.portal.livetrace;

public class ClientMethodExecutionTrace
    extends BaseTrace
    implements Trace, ContentIndexQuerier, RelatedContentFetcher
{
    private String methodName;

    private Traces<ContentIndexQueryTrace> contentIndexQueryTraces = new Traces<ContentIndexQueryTrace>();

    private Traces<RelatedContentFetchTrace> relatedContentFetchTraces = new Traces<RelatedContentFetchTrace>();

    ClientMethodExecutionTrace()
    {
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getMethodName()
    {
        return methodName;
    }

    void setMethodName( String methodName )
    {
        this.methodName = methodName;
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
