/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.livetrace;

/**
 * Oct 6, 2010
 */
public class WindowRenderingTrace
    extends BaseTrace
    implements Trace
{
    private String windowKey;

    private String portletName;

    private User renderer;

    private CacheUsage cacheUsage = new CacheUsage();

    private Traces<DatasourceExecutionTrace> datasourceExecutionTraces;

    private ViewTransformationTrace viewTransformationTrace;

    private InstructionPostProcessingTrace instructionPostProcessingTrace;

    WindowRenderingTrace( String windowKey )
    {
        this.windowKey = windowKey;
    }

    String getWindowKey()
    {
        return windowKey;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getPortletName()
    {
        return portletName;
    }

    public void setPortletName( String portletName )
    {
        this.portletName = portletName;
    }

    @SuppressWarnings("UnusedDeclaration")
    public User getRenderer()
    {
        return renderer;
    }

    public CacheUsage getCacheUsage()
    {
        return cacheUsage;
    }

    public void setRenderer( User renderer )
    {
        this.renderer = renderer;
    }

    public void addDatasourceExecutionTrace( DatasourceExecutionTrace trace )
    {
        if ( datasourceExecutionTraces == null )
        {
            datasourceExecutionTraces = Traces.create();
        }
        this.datasourceExecutionTraces.add( trace );
    }

    void setViewTransformationTrace( ViewTransformationTrace viewTransformationTrace )
    {
        this.viewTransformationTrace = viewTransformationTrace;
    }

    void setInstructionPostProcessingTrace( InstructionPostProcessingTrace instructionPostProcessingTrace )
    {
        this.instructionPostProcessingTrace = instructionPostProcessingTrace;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Traces<DatasourceExecutionTrace> getDatasourceExecutionTraces()
    {
        return datasourceExecutionTraces;
    }

    @SuppressWarnings("UnusedDeclaration")
    public ViewTransformationTrace getViewTransformationTrace()
    {
        return viewTransformationTrace;
    }

    @SuppressWarnings("UnusedDeclaration")
    public InstructionPostProcessingTrace getInstructionPostProcessingTrace()
    {
        return instructionPostProcessingTrace;
    }
}
