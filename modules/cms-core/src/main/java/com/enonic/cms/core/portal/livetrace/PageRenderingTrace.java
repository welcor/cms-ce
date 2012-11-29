/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.livetrace;

/**
 * Oct 6, 2010
 */
public class PageRenderingTrace
    extends BaseTrace
    implements Trace
{
    private User renderer;

    private CacheUsage cacheUsage = new CacheUsage();

    private Traces<WindowRenderingTrace> windowRenderingTraces;

    private Traces<DatasourceExecutionTrace> datasourceExecutionTraces;

    private ViewTransformationTrace viewTransformationTrace;

    private InstructionPostProcessingTrace instructionPostProcessingTrace;

    @SuppressWarnings("UnusedDeclaration")
    public User getRenderer()
    {
        return renderer;
    }

    public void setRenderer( User renderer )
    {
        this.renderer = renderer;
    }

    @SuppressWarnings("UnusedDeclaration")
    public CacheUsage getCacheUsage()
    {
        return cacheUsage;
    }

    void addWindowRenderingTrace( WindowRenderingTrace trace )
    {
        if ( windowRenderingTraces == null )
        {
            windowRenderingTraces = Traces.create();
        }
        windowRenderingTraces.add( trace );
    }

    public Traces<WindowRenderingTrace> getWindowRenderingTraces()
    {
        return windowRenderingTraces;
    }

    void addDatasourceExecutionTrace( DatasourceExecutionTrace trace )
    {
        if ( datasourceExecutionTraces == null )
        {
            datasourceExecutionTraces = Traces.create();
        }
        datasourceExecutionTraces.add( trace );
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

    void setViewTransformationTrace( ViewTransformationTrace viewTransformationTrace )
    {
        this.viewTransformationTrace = viewTransformationTrace;
    }

    @SuppressWarnings("UnusedDeclaration")
    public InstructionPostProcessingTrace getInstructionPostProcessingTrace()
    {
        return instructionPostProcessingTrace;
    }

    void setInstructionPostProcessingTrace( InstructionPostProcessingTrace instructionPostProcessingTrace )
    {
        this.instructionPostProcessingTrace = instructionPostProcessingTrace;
    }
}
