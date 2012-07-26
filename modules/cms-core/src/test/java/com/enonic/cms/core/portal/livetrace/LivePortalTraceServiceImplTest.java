package com.enonic.cms.core.portal.livetrace;


import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.core.portal.datasource.DatasourcesType;
import com.enonic.cms.core.structure.page.WindowKey;
import com.enonic.cms.core.time.MockTimeService;

import static org.junit.Assert.*;

public class LivePortalTraceServiceImplTest
{
    private MockTimeService timeService = new MockTimeService();

    private LivePortalTraceServiceImpl service = new LivePortalTraceServiceImpl();

    @Before
    public void before()
    {
        service.setEnabled( "true" );
        service.setTimeService( timeService );
        service.init();
    }

    @Test
    public void duration_of_pageInstructionPostProcessingTrace_when_rendering_a_page_with_two_windows()
    {
        DateTime now = new DateTime( 1970, 1, 1, 0, 59, 59, 980 );
        timeService.setTimeNow( now );

        PortalRequestTrace portalRequestTrace = PortalRequestTracer.startTracing( "http://localhost:8080/site/0", service );
        forwardTime( 5 );
        // 1970-01-01 00:59:59.985

        PageRenderingTrace pageRenderingTrace = PageRenderingTracer.startTracing( service );

        simulateDatasourceExecution( DatasourcesType.PAGETEMPLATE, "myMethod", 10 );
        // 1970-01-01 00:59:59.995

        simulateViewTransformation( 5 );
        // 1970-01-01 01:00:00.000

        InstructionPostProcessingTrace pageInstructionPostProcessingTrace = InstructionPostProcessingTracer.startTracingForPage( service );
        forwardTime( 3 );
        // 1970-01-01 01:00:00.003

        simulateWindowExecution( new WindowKey( "1:1" ), 10 );
        // 1970-01-01 01:00:00.013

        simulateWindowExecution( new WindowKey( "1:2" ), 10 );
        // 1970-01-01 01:00:00.023

        forwardTime( 3 );
        // 1970-01-01 01:00:00.026

        InstructionPostProcessingTracer.stopTracing( pageInstructionPostProcessingTrace, service );
        PageRenderingTracer.stopTracing( pageRenderingTrace, service );
        PortalRequestTracer.stopTracing( portalRequestTrace, service );

        assertEquals( 6, pageInstructionPostProcessingTrace.getDuration().getAsMilliseconds() );
    }

    @Test
    public void duration_of_pageInstructionPostProcessingTrace_when_rendering_a_page_which_uses_function_isWindowEmpty_with_one_window()
    {
        DateTime now = new DateTime( 1970, 1, 1, 0, 59, 59, 980 );
        timeService.setTimeNow( now );

        PortalRequestTrace portalRequestTrace = PortalRequestTracer.startTracing( "http://localhost:8080/site/0", service );
        forwardTime( 5 );
        // 1970-01-01 00:59:59.985
        PageRenderingTrace pageRenderingTrace = PageRenderingTracer.startTracing( service );

        simulateDatasourceExecution( DatasourcesType.PAGETEMPLATE, "myMethod", 10 );
        // 1970-01-01 00:59:59.995

        ViewTransformationTrace pageViewTransformationTrace = ViewTransformationTracer.startTracing( service );

        ViewFunctionTrace viewFunctionTrace = ViewFunctionTracer.startTracing( "isWindowEmpty", service );

        // window being rendered by isWindowEmpty function
        simulateWindowExecution( new WindowKey( "1:1" ), 10 );
        // 1970-01-01 01:00:00.010

        ViewFunctionTracer.stopTracing( viewFunctionTrace, service );
        ViewTransformationTracer.stopTracing( pageViewTransformationTrace, service );

        InstructionPostProcessingTrace pageInstructionPostProcessingTrace = InstructionPostProcessingTracer.startTracingForPage( service );
        forwardTime( 3 );
        // 1970-01-01 01:00:00.013

        simulateWindowExecution( new WindowKey( "1:1" ), 3 );
        // 1970-01-01 01:00:00.023

        forwardTime( 3 );
        // 1970-01-01 01:00:00.026

        InstructionPostProcessingTracer.stopTracing( pageInstructionPostProcessingTrace, service );
        PageRenderingTracer.stopTracing( pageRenderingTrace, service );
        PortalRequestTracer.stopTracing( portalRequestTrace, service );

        assertEquals( 6, pageInstructionPostProcessingTrace.getDuration().getAsMilliseconds() );
    }

    @Test
    public void duration_of_windowInstructionPostProcessingTrace_when_rendering_a_window()
    {
        DateTime now = new DateTime( 1970, 1, 1, 0, 59, 59, 980 );
        timeService.setTimeNow( now );

        PortalRequestTrace portalRequestTrace = PortalRequestTracer.startTracing( "http://localhost:8080/site/0", service );
        forwardTime( 5 );

        WindowRenderingTrace windowRenderingTrace = WindowRenderingTracer.startTracing( new WindowKey( "1:1" ), service );
        forwardTime( 10 );

        InstructionPostProcessingTrace windowInstructionPostProcessingTrace =
            InstructionPostProcessingTracer.startTracingForWindow( service );
        forwardTime( 2 );
        InstructionPostProcessingTracer.stopTracing( windowInstructionPostProcessingTrace, service );

        WindowRenderingTracer.stopTracing( windowRenderingTrace, service );
        PortalRequestTracer.stopTracing( portalRequestTrace, service );

        assertEquals( 2, windowInstructionPostProcessingTrace.getDuration().getAsMilliseconds() );
    }

    private ViewTransformationTrace simulateViewTransformation( int timeInMillis )
    {
        ViewTransformationTrace trace = ViewTransformationTracer.startTracing( service );
        forwardTime( timeInMillis );
        ViewTransformationTracer.stopTracing( trace, service );
        return trace;
    }

    private WindowRenderingTrace simulateWindowExecution( WindowKey windowKey, int timeInMillis )
    {
        WindowRenderingTrace trace = WindowRenderingTracer.startTracing( windowKey, service );
        forwardTime( timeInMillis );
        WindowRenderingTracer.stopTracing( trace, service );
        return trace;
    }

    private DatasourceExecutionTrace simulateDatasourceExecution( DatasourcesType type, String methodName, int timeInMillis )
    {
        DatasourceExecutionTrace trace = DatasourceExecutionTracer.startTracing( type, methodName, service );
        forwardTime( timeInMillis );
        DatasourceExecutionTracer.stopTracing( trace, service );
        return trace;
    }

    private void forwardTime( int milliseconds )
    {
        DateTime now = timeService.getNowAsDateTime();
        DateTime newNow = now.plusMillis( milliseconds );
        timeService.setTimeNow( newNow );
    }
}
