package com.enonic.cms.core.portal.instanttrace;

import com.enonic.cms.core.portal.livetrace.PortalRequestTrace;

public class CurrentTrace
{
    private final static ThreadLocal<PortalRequestTrace> CURRENT_TRACE = new ThreadLocal<PortalRequestTrace>();

    public static void setCurrentTrace( PortalRequestTrace portalRequestTrace )
    {
        CURRENT_TRACE.set( portalRequestTrace );
    }

    public static PortalRequestTrace popLastPortalRequestTrace()
    {
        PortalRequestTrace renderingTrace = CURRENT_TRACE.get();
        CURRENT_TRACE.set( null );

        return renderingTrace;
    }

}
