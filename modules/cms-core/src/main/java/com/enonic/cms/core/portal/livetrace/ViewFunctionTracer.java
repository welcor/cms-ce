package com.enonic.cms.core.portal.livetrace;


public class ViewFunctionTracer
{
    public static ViewFunctionTrace startTracing( String functionName, final LivePortalTraceService livePortalTraceService )
    {
        if ( !livePortalTraceService.tracingEnabled() )
        {
            return null;
        }

        return livePortalTraceService.startViewFunctionTracing( functionName );
    }

    public static void stopTracing( final ViewFunctionTrace trace, final LivePortalTraceService livePortalTraceService )
    {
        if ( trace != null )
        {
            livePortalTraceService.stopTracing( trace );
        }
    }

    public static void traceFunctionArgument( String name, String value, ViewFunctionTrace trace )
    {
        if ( trace != null )
        {
            trace.addArgument( new ViewFunctionArgument( name, value ) );
        }
    }

    public static void traceFunctionArgument( String name, String[] value, ViewFunctionTrace trace )
    {
        if ( trace != null )
        {
            trace.addArgument( new ViewFunctionArgument( name, value ) );
        }
    }
}
