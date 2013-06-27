/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.livetrace;

public class ClientMethodExecutionTrace
    extends BaseTrace
    implements Trace, ContentIndexQuerier, RelatedContentFetcher
{
    private String methodName;

    private Traces<ContentIndexQueryTrace> contentIndexQueryTraces;

    private Traces<RelatedContentFetchTrace> relatedContentFetchTraces;

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
        if ( contentIndexQueryTraces == null )
        {
            contentIndexQueryTraces = Traces.create();
        }
        contentIndexQueryTraces.add( trace );
    }

    @Override
    public void addRelatedContentFetchTrace( final RelatedContentFetchTrace trace )
    {
        if ( relatedContentFetchTraces == null )
        {
            relatedContentFetchTraces = Traces.create();
        }
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
