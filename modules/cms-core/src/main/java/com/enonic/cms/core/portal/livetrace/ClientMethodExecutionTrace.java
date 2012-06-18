package com.enonic.cms.core.portal.livetrace;

public class ClientMethodExecutionTrace
    extends BaseTrace
    implements Trace, ContentIndexQuerier
{
    private String methodName;

    private Traces<ContentIndexQueryTrace> contentIndexQueryTraces = new Traces<ContentIndexQueryTrace>();

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

    @SuppressWarnings("UnusedDeclaration")
    public Traces<ContentIndexQueryTrace> getContentIndexQueryTraces()
    {
        return contentIndexQueryTraces;
    }
}
