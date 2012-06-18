/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.livetrace;

import java.util.List;

import com.enonic.cms.core.portal.datasource.DatasourcesType;
import com.enonic.cms.core.structure.page.WindowKey;

/**
 * Oct 6, 2010
 */
public interface LivePortalTraceService
{
    boolean tracingEnabled();

    PortalRequestTrace startPortalRequestTracing( String url );

    PageRenderingTrace startPageRenderTracing( PortalRequestTrace portalRequestTrace );

    WindowRenderingTrace startWindowRenderTracing( WindowKey windowKey );

    DatasourceExecutionTrace startDatasourceExecutionTracing( DatasourcesType datasourcesType, String datasourceMethodName );

    ClientMethodExecutionTrace startClientMethodExecutionTracing( String methodName );

    ContentIndexQueryTrace startContentIndexQueryTracing();

    ViewTransformationTrace startViewTransformationTracing();

    ViewFunctionTrace startViewFunctionTracing( String functionName );

    InstructionPostProcessingTrace startInstructionPostProcessingTracingForWindow();

    InstructionPostProcessingTrace startInstructionPostProcessingTracingForPage();

    AttachmentRequestTrace startAttachmentRequestTracing( PortalRequestTrace portalRequestTrace );

    ImageRequestTrace startImageRequestTracing( PortalRequestTrace portalRequestTrace );

    PortalRequestTrace getCurrentPortalRequestTrace();

    CurrentTrace getCurrentTrace();

    void stopTracing( PortalRequestTrace livePortalRequestTrace );

    void stopTracing( PageRenderingTrace pageRenderTrace );

    void stopTracing( WindowRenderingTrace windowRenderingTrace );

    void stopTracing( DatasourceExecutionTrace datasourceExecutionTrace );

    void stopTracing( ClientMethodExecutionTrace clientMethodExecutionTrace );

    void stopTracing( ViewTransformationTrace trace );

    void stopTracing( ViewFunctionTrace trace );

    void stopTracing( ContentIndexQueryTrace contentIndexQueryTrace );

    void stopTracing( AttachmentRequestTrace attachmentRequestTrace );

    void stopTracing( ImageRequestTrace imageRequestTrace );

    void stopTracing( InstructionPostProcessingTrace instructionPostProcessingTrace );

    int getNumberOfPortalRequestTracesInProgress();

    List<PortalRequestTrace> getCurrentPortalRequestTraces();

    List<PortalRequestTrace> getCompletedAfter( long historyNumber );

    List<PortalRequestTrace> getCompletedBefore( long compltedNumber );

    List<PortalRequestTrace> getLongestTimePortalPageRequestTraces();

    List<PortalRequestTrace> getLongestTimePortalAttachmentRequestTraces();

    List<PortalRequestTrace> getLongestTimePortalImageRequestTraces();

    void clearLongestPageRequestsTraces();

    void clearLongestAttachmentRequestTraces();

    void clearLongestImageRequestTraces();
}
