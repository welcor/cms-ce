/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.livetrace;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;

import com.enonic.cms.core.portal.datasource.DataSourceType;
import com.enonic.cms.core.structure.page.WindowKey;
import com.enonic.cms.core.time.TimeService;

/**
 * Oct 6, 2010
 */
@Service("livePortalTraceService")
public class LivePortalTraceServiceImpl
    implements LivePortalTraceService
{
    private static final Logger LOG = LoggerFactory.getLogger( LivePortalTraceServiceImpl.class );

    private static AtomicLong requestCounter = new AtomicLong();

    private TimeService timeService;

    private boolean enabled = false;

    private int historySize;

    private int longestSize;

    private CurrentPortalRequests currentPortalRequests = new CurrentPortalRequests();

    private CompletedPortalRequests completedPortalRequests;

    private LongestPortalRequests longestPortalPageRequests;

    private LongestPortalRequests longestPortalAttachmentRequests;

    private LongestPortalRequests longestPortalImageRequests;

    private final static ThreadLocal<CurrentTrace> CURRENT_TRACE = new ThreadLocal<CurrentTrace>();

    @PostConstruct
    public void init()
    {
        if ( enabled )
        {
            LOG.info( "Live Portal Tracing is enabled [historySize=" + historySize + ", longestSize=" + longestSize + "]" );

            longestPortalPageRequests = new LongestPortalRequests( longestSize );
            longestPortalAttachmentRequests = new LongestPortalRequests( longestSize );
            longestPortalImageRequests = new LongestPortalRequests( longestSize );
            completedPortalRequests = new CompletedPortalRequests( historySize );
        }
        else
        {
            LOG.info( "Live Portal Tracing is not enabled" );
        }
    }

    public boolean tracingEnabled()
    {
        return enabled;
    }

    public PortalRequestTrace startPortalRequestTracing( final String url )
    {
        checkEnabled();
        final long requestNumber = requestCounter.incrementAndGet();
        PortalRequestTrace portalRequestTrace = new PortalRequestTrace( requestNumber, url );
        currentPortalRequests.add( portalRequestTrace );

        portalRequestTrace.setStartTime( timeService.getNowAsDateTime() );
        final CurrentTrace currentTrace = new CurrentTrace();
        currentTrace.setPortalRequestTrace( portalRequestTrace );
        CURRENT_TRACE.set( currentTrace );

        currentTrace.setPageRenderingTrace( null );
        return portalRequestTrace;
    }

    public PageRenderingTrace startPageRenderTracing( final PortalRequestTrace portalRequestTrace )
    {
        Preconditions.checkNotNull( portalRequestTrace );

        final PageRenderingTrace pageRenderingTrace = new PageRenderingTrace();
        pageRenderingTrace.setStartTime( timeService.getNowAsDateTime() );
        portalRequestTrace.setPageRenderingTrace( pageRenderingTrace );

        getCurrentTrace().setPageRenderingTrace( pageRenderingTrace );

        return pageRenderingTrace;
    }

    public WindowRenderingTrace startWindowRenderTracing( final WindowKey windowKey )
    {
        final PortalRequestTrace portalRequestTrace = getCurrentTrace().getPortalRequestTrace();
        if ( portalRequestTrace == null )
        {
            return null;
        }

        final WindowRenderingTrace windowRenderingTrace = new WindowRenderingTrace( windowKey.toString() );
        final ViewFunctionTrace currentViewFunctionTrace = getCurrentTrace().getViewFunctionTrace();
        if ( currentViewFunctionTrace != null )
        {
            currentViewFunctionTrace.addTrace( windowRenderingTrace );
        }
        else if ( portalRequestTrace.hasPageRenderingTrace() )
        {
            portalRequestTrace.getPageRenderingTrace().addWindowRenderingTrace( windowRenderingTrace );
        }
        else
        {
            portalRequestTrace.setWindowRenderingTrace( windowRenderingTrace );
        }

        windowRenderingTrace.setStartTime( timeService.getNowAsDateTime() );

        getCurrentTrace().setWindowRenderingTrace( windowRenderingTrace );

        return windowRenderingTrace;
    }

    @Override
    public DatasourceExecutionTrace startDatasourceExecutionTracing( final DataSourceType datasourcesType,
                                                                     final String datasourceMethodName )
    {
        final DatasourceExecutionTrace datasourceExecutionTrace = new DatasourceExecutionTrace( datasourceMethodName );
        datasourceExecutionTrace.setStartTime( timeService.getNowAsDateTime() );

        if ( datasourcesType == DataSourceType.PAGETEMPLATE )
        {
            final PageRenderingTrace pageRenderingTrace = getCurrentTrace().getPageRenderingTrace();
            if ( pageRenderingTrace == null )
            {
                return null;
            }
            pageRenderingTrace.addDatasourceExecutionTrace( datasourceExecutionTrace );
        }
        else
        {
            final WindowRenderingTrace windowRenderingTrace = getCurrentTrace().getWindowRenderingTrace();
            if ( windowRenderingTrace == null )
            {
                return null;
            }
            windowRenderingTrace.addDatasourceExecutionTrace( datasourceExecutionTrace );
        }

        getCurrentTrace().setDatasourceExecutionTrace( datasourceExecutionTrace );
        return datasourceExecutionTrace;
    }

    @Override
    public ClientMethodExecutionTrace startClientMethodExecutionTracing( final String methodName )
    {
        Preconditions.checkNotNull( methodName );

        final DatasourceExecutionTrace currentDatasourceExecutionTrace = getCurrentTrace().getDatasourceExecutionTrace();
        if ( currentDatasourceExecutionTrace == null )
        {
            return null;
        }
        final ClientMethodExecutionTrace trace = new ClientMethodExecutionTrace();
        trace.setMethodName( methodName );
        trace.setStartTime( timeService.getNowAsDateTime() );
        currentDatasourceExecutionTrace.addClientMethodExecutionTrace( trace );

        getCurrentTrace().setClientMethodExecutionTrace( trace );

        return trace;
    }

    @Override
    public ContentIndexQueryTrace startContentIndexQueryTracing()
    {
        final ContentIndexQuerier currentQuerier = getCurrentTrace().getCurrentContentIndexQuerier();
        if ( currentQuerier != null )
        {
            final ContentIndexQueryTrace trace = new ContentIndexQueryTrace();
            trace.setStartTime( timeService.getNowAsDateTime() );
            currentQuerier.addContentIndexQueryTrace( trace );
            return trace;
        }
        else
        {
            return null;
        }
    }

    @Override
    public RelatedContentFetchTrace startRelatedContentFetchTracing()
    {
        final RelatedContentFetcher relatedContentFetcher = getCurrentTrace().getCurrentRelatedContentFetcher();
        if ( relatedContentFetcher != null )
        {
            final RelatedContentFetchTrace trace = new RelatedContentFetchTrace();
            trace.setStartTime( timeService.getNowAsDateTime() );
            relatedContentFetcher.addRelatedContentFetchTrace( trace );
            return trace;
        }
        else
        {
            return null;
        }
    }

    public ViewTransformationTrace startViewTransformationTracing()
    {
        final ViewTransformationTrace trace = new ViewTransformationTrace();
        trace.setStartTime( timeService.getNowAsDateTime() );

        final WindowRenderingTrace windowRenderingTrace = getCurrentTrace().getWindowRenderingTrace();
        if ( windowRenderingTrace != null )
        {
            windowRenderingTrace.setViewTransformationTrace( trace );
            getCurrentTrace().setWindowViewTransformationTrace( trace );
        }
        else
        {
            final PageRenderingTrace pageRenderingTrace = getCurrentTrace().getPageRenderingTrace();
            if ( pageRenderingTrace != null )
            {
                pageRenderingTrace.setViewTransformationTrace( trace );
                getCurrentTrace().setPageViewTransformationTrace( trace );
            }
            else
            {
                return null;
            }
        }

        return trace;
    }

    public ViewFunctionTrace startViewFunctionTracing( final String functionName )
    {
        final ViewFunctionTrace trace = new ViewFunctionTrace();
        trace.setStartTime( timeService.getNowAsDateTime() );
        trace.setName( functionName );

        ViewTransformationTrace viewTransformationTrace = getCurrentTrace().getPageViewTransformationTrace();
        if ( viewTransformationTrace != null )
        {
            viewTransformationTrace.addViewFunctionTrace( trace );
            getCurrentTrace().setViewFunctionTrace( trace );
            return trace;
        }
        else
        {
            viewTransformationTrace = getCurrentTrace().getWindowViewTransformationTrace();
            if ( viewTransformationTrace != null )
            {
                viewTransformationTrace.addViewFunctionTrace( trace );
                getCurrentTrace().setViewFunctionTrace( trace );
                return trace;
            }
            else
            {
                return null;
            }
        }
    }

    public InstructionPostProcessingTrace startInstructionPostProcessingTracingForWindow()
    {
        final WindowRenderingTrace windowRenderingTrace = getCurrentTrace().getWindowRenderingTrace();
        if ( windowRenderingTrace == null )
        {
            return null;
        }

        final InstructionPostProcessingTrace instructionPostProcessingTrace = new InstructionPostProcessingTrace();
        instructionPostProcessingTrace.setStartTime( timeService.getNowAsDateTime() );
        windowRenderingTrace.setInstructionPostProcessingTrace( instructionPostProcessingTrace );
        return instructionPostProcessingTrace;
    }

    public InstructionPostProcessingTrace startInstructionPostProcessingTracingForPage()
    {
        final PageRenderingTrace pageRenderingTrace = getCurrentTrace().getPageRenderingTrace();
        if ( pageRenderingTrace == null )
        {
            return null;
        }
        final InstructionPostProcessingTrace instructionPostProcessingTrace = new InstructionPostProcessingTrace();
        instructionPostProcessingTrace.setStartTime( timeService.getNowAsDateTime() );
        pageRenderingTrace.setInstructionPostProcessingTrace( instructionPostProcessingTrace );
        return instructionPostProcessingTrace;
    }

    public AttachmentRequestTrace startAttachmentRequestTracing( final PortalRequestTrace portalRequestTrace )
    {
        Preconditions.checkNotNull( portalRequestTrace );

        AttachmentRequestTrace newTrace = new AttachmentRequestTrace();
        newTrace.setStartTime( timeService.getNowAsDateTime() );
        portalRequestTrace.setAttachmentRequestTrace( newTrace );
        return newTrace;
    }

    public ImageRequestTrace startImageRequestTracing( final PortalRequestTrace portalRequestTrace )
    {
        Preconditions.checkNotNull( portalRequestTrace );

        ImageRequestTrace newTrace = new ImageRequestTrace();
        newTrace.setStartTime( timeService.getNowAsDateTime() );
        portalRequestTrace.setImageRequestTrace( newTrace );
        getCurrentTrace().setImageRequestTrace( newTrace );
        return newTrace;
    }

    public void stopTracing( final PortalRequestTrace portalRequestTrace )
    {
        checkEnabled();
        Preconditions.checkNotNull( portalRequestTrace );

        portalRequestTrace.setStopTime( timeService.getNowAsDateTime() );

        currentPortalRequests.remove( portalRequestTrace );

        portalRequestTrace.postProcess();

        completedPortalRequests.add( portalRequestTrace );

        if ( portalRequestTrace.hasPageRenderingTrace() || portalRequestTrace.hasWindowRenderingTrace() )
        {
            longestPortalPageRequests.add( portalRequestTrace );
        }
        else if ( portalRequestTrace.hasAttachmentRequsetTrace() )
        {
            longestPortalAttachmentRequests.add( portalRequestTrace );
        }
        else if ( portalRequestTrace.hasImageRequestTrace() )
        {
            longestPortalImageRequests.add( portalRequestTrace );
        }

        getCurrentTrace().setPortalRequestTrace( null );
    }

    public void stopTracing( final PageRenderingTrace pageRenderTrace )
    {
        Preconditions.checkNotNull( pageRenderTrace );

        pageRenderTrace.setStopTime( timeService.getNowAsDateTime() );

        getCurrentTrace().setPageRenderingTrace( null );
    }

    public void stopTracing( final WindowRenderingTrace windowRenderingTrace )
    {
        Preconditions.checkNotNull( windowRenderingTrace );

        windowRenderingTrace.setStopTime( timeService.getNowAsDateTime() );

        getCurrentTrace().setWindowRenderingTrace( null );
    }

    public void stopTracing( final AttachmentRequestTrace attachmentRequestTrace )
    {
        Preconditions.checkNotNull( attachmentRequestTrace );

        attachmentRequestTrace.setStopTime( timeService.getNowAsDateTime() );
    }

    public void stopTracing( final DatasourceExecutionTrace datasourceExecutionTrace )
    {
        Preconditions.checkNotNull( datasourceExecutionTrace );
        datasourceExecutionTrace.setStopTime( timeService.getNowAsDateTime() );

        getCurrentTrace().setDatasourceExecutionTrace( null );
    }

    @Override
    public void stopTracing( final ClientMethodExecutionTrace clientMethodExecutionTrace )
    {
        Preconditions.checkNotNull( clientMethodExecutionTrace );
        clientMethodExecutionTrace.setStopTime( timeService.getNowAsDateTime() );

        getCurrentTrace().setClientMethodExecutionTrace( null );
    }

    @Override
    public void stopTracing( final ViewTransformationTrace trace )
    {
        Preconditions.checkNotNull( trace );
        trace.setStopTime( timeService.getNowAsDateTime() );

        getCurrentTrace().removeCurrentViewTransformationTrace();
    }

    @Override
    public void stopTracing( final ViewFunctionTrace trace )
    {
        Preconditions.checkNotNull( trace );
        trace.setStopTime( timeService.getNowAsDateTime() );

        getCurrentTrace().setViewFunctionTrace( null );
    }

    @Override
    public void stopTracing( final ContentIndexQueryTrace contentIndexQueryTrace )
    {
        Preconditions.checkNotNull( contentIndexQueryTrace );
        contentIndexQueryTrace.setStopTime( timeService.getNowAsDateTime() );
    }

    @Override
    public void stopTracing( final RelatedContentFetchTrace trace )
    {
        Preconditions.checkNotNull( trace );
        trace.setStopTime( timeService.getNowAsDateTime() );
    }

    public void stopTracing( final InstructionPostProcessingTrace instructionPostProcessingTrace )
    {
        Preconditions.checkNotNull( instructionPostProcessingTrace );

        if ( getCurrentTrace().isInPageRenderingTrace() )
        {
            final Traces<WindowRenderingTrace> windowRenderingTraces = getCurrentTrace().getPageRenderingTrace().getWindowRenderingTraces();
            int windowsTotalPeriod = windowRenderingTraces != null ? windowRenderingTraces.getTotalPeriodInMilliseconds() : 0;
            final long stopTime = timeService.getNowAsDateTime().getMillis();
            final long startTime = instructionPostProcessingTrace.getStartTime().getMillis();
            final long duration = ( stopTime - startTime ) - windowsTotalPeriod;
            instructionPostProcessingTrace.setDurationInMilliseconds( duration );
        }
        else if ( getCurrentTrace().getWindowRenderingTrace() != null )
        {
            final long stopTime = timeService.getNowAsDateTime().getMillis();
            final long startTime = instructionPostProcessingTrace.getStartTime().getMillis();
            final long duration = stopTime - startTime;
            instructionPostProcessingTrace.setDurationInMilliseconds( duration );
        }
    }

    public void stopTracing( final ImageRequestTrace imageRequestTrace )
    {
        Preconditions.checkNotNull( imageRequestTrace );

        imageRequestTrace.setStopTime( timeService.getNowAsDateTime() );
    }

    @Override
    public int getNumberOfPortalRequestTracesInProgress()
    {
        checkEnabled();
        return currentPortalRequests.getSize();
    }

    public List<PortalRequestTrace> getCurrentPortalRequestTraces()
    {
        checkEnabled();
        return currentPortalRequests.getList();
    }

    public List<PortalRequestTrace> getLongestTimePortalPageRequestTraces()
    {
        checkEnabled();
        return longestPortalPageRequests.getList();
    }

    public List<PortalRequestTrace> getLongestTimePortalAttachmentRequestTraces()
    {
        checkEnabled();
        return longestPortalAttachmentRequests.getList();
    }

    public List<PortalRequestTrace> getLongestTimePortalImageRequestTraces()
    {
        checkEnabled();
        return longestPortalImageRequests.getList();
    }

    public List<PortalRequestTrace> getCompletedAfter( long historyRecordNumber )
    {
        checkEnabled();
        return completedPortalRequests.getCompletedAfter( historyRecordNumber );
    }

    public List<PortalRequestTrace> getCompletedBefore( long historyRecordNumber )
    {
        checkEnabled();
        return completedPortalRequests.getCompletedBefore( historyRecordNumber );
    }

    public PortalRequestTrace getCurrentPortalRequestTrace()
    {
        if ( !enabled )
        {
            return null;
        }

        CurrentTrace currentTrace = CURRENT_TRACE.get();
        if ( currentTrace == null )
        {
            return null;
        }
        return currentTrace.getPortalRequestTrace();
    }

    @Override
    public void clearLongestPageRequestsTraces()
    {
        longestPortalPageRequests.clear();
    }

    @Override
    public void clearLongestAttachmentRequestTraces()
    {
        longestPortalAttachmentRequests.clear();
    }

    @Override
    public void clearLongestImageRequestTraces()
    {
        longestPortalImageRequests.clear();
    }

    private void checkEnabled()
    {
        Preconditions.checkArgument( enabled, "Unexpected call when Live Portal Tracing is disabled" );
    }

    public CurrentTrace getCurrentTrace()
    {
        CurrentTrace currentTrace = CURRENT_TRACE.get();
        if ( currentTrace == null )
        {
            currentTrace = new CurrentTrace();
            CURRENT_TRACE.set( currentTrace );
        }
        return currentTrace;
    }

    @Autowired
    public void setTimeService( TimeService timeService )
    {
        this.timeService = timeService;
    }

    @Value("${cms.livePortalTrace.enabled}")
    public void setEnabled( String enabled )
    {
        this.enabled = Boolean.valueOf( enabled );
    }

    @Value("${cms.livePortalTrace.history.size}")
    public void setHistorySize( int value )
    {
        this.historySize = value;
    }

    @Value("${cms.livePortalTrace.longest.size}")
    public void setLongestSize( int value )
    {
        this.longestSize = value;
    }
}
