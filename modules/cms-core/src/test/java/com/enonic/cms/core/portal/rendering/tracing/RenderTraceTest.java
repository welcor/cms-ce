package com.enonic.cms.core.portal.rendering.tracing;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.cms.core.security.PortalSecurityHolder;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.servlet.ServletRequestAccessor;

import static org.junit.Assert.*;

public class RenderTraceTest
{
    private MockHttpServletRequest request = new MockHttpServletRequest();

    @Before
    public void setup()
    {
        ServletRequestAccessor.setRequest( request );
    }

    @Test
    public void test_trace_per_user_scope_session()
        throws Exception
    {
        UserKey userKey = new UserKey( "ABC" );
        PortalSecurityHolder.setLoggedInUser( userKey );
        assertEquals( userKey, PortalSecurityHolder.getLoggedInUser() );

        for ( int i = 0; i < 15; i++ )
        {
            RenderTrace.enter();
            RenderTrace.exit();
        }

        List history = (List) ServletRequestAccessor.getRequest().getSession().getAttribute( RenderTrace.HISTORY_PREFIX + userKey );
        // verify
        assertEquals( 10, history.size() );
    }
}