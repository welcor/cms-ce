package com.enonic.cms.core.portal.rendering.tracing;

import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.cms.core.security.PortalSecurityHolder;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.servlet.ServletRequestAccessor;

import static org.junit.Assert.*;

public class RenderTraceTest
{

    private final UserKey user1 = new UserKey( "AAA" );

    private final UserKey user2 = new UserKey( "BBB" );

    private final UserKey user3 = new UserKey( "CCC" );

    private final UserKey user4 = new UserKey( "DDD" );

    @After
    public void after()
    {
        PortalSecurityHolder.setLoggedInUser( null );
    }

    @Test
    public void only_traces_with_page_info_are_kept()
    {
        ServletRequestAccessor.setRequest( new MockHttpServletRequest() );
        PortalSecurityHolder.setLoggedInUser( user1 );

        // exercise: simulate 4 page traces and 4 non-page traces
        for ( int i = 1; i <= 8; i++ )
        {
            RenderTrace.enter();
            boolean even = i % 2 == 0;
            if ( even )
            {
                RenderTrace.enterPage( i );
            }
            RenderTrace.exit();
        }

        List<RenderTraceInfo> history = getHistoryForUser( user1 );

        // verify: only 4 traces are left in history
        assertEquals( 4, history.size() );

        // verify: all 4 have page info
        for ( RenderTraceInfo info : history )
        {
            assertNotNull( info.getPageInfo() );
        }
    }

    @Test
    public void max_ten_traces_are_kept_for_one_user()
    {
        ServletRequestAccessor.setRequest( new MockHttpServletRequest() );
        PortalSecurityHolder.setLoggedInUser( user2 );

        // exercise
        for ( int i = 1; i <= 30; i++ )
        {
            RenderTrace.enter();
            if ( i % 2 == 0 )
            {
                RenderTrace.enterPage( i );
            }
            RenderTrace.exit();
        }

        List<RenderTraceInfo> history = getHistoryForUser( user2 );

        // verify: only 10 traces are left
        assertEquals( 10, history.size() );

        // verify: all have page info
        for ( RenderTraceInfo info : history )
        {
            assertNotNull( info.getPageInfo() );
        }
    }

    @Test
    public void max_ten_traces_are_kept_per_user()
        throws InterruptedException
    {

        UserTraceSimulator userTraceSimulator1 = new UserTraceSimulator( user3 );
        UserTraceSimulator userTraceSimulator2 = new UserTraceSimulator( user4 );
        userTraceSimulator1.start();
        userTraceSimulator2.start();
        userTraceSimulator1.join();
        userTraceSimulator2.join();

        assertEquals( 10, userTraceSimulator1.getHistory().size() );
        assertEquals( 10, userTraceSimulator2.getHistory().size() );
    }

    private static List<RenderTraceInfo> getHistoryForUser( UserKey userKey )
    {
        //noinspection unchecked
        return (List) ServletRequestAccessor.getRequest().getSession().getAttribute( RenderTrace.HISTORY_PREFIX + userKey );
    }

    private static class UserTraceSimulator
        extends Thread
    {
        private UserKey user;

        private List<RenderTraceInfo> history;

        private UserTraceSimulator( UserKey user )
        {
            this.user = user;
        }

        private List<RenderTraceInfo> getHistory()
        {
            return history;
        }

        @Override
        public void run()
        {
            ServletRequestAccessor.setRequest( new MockHttpServletRequest() );
            PortalSecurityHolder.setLoggedInUser( user );

            for ( int i = 1; i <= 30; i++ )
            {
                RenderTrace.enter();
                if ( i % 2 == 0 )
                {
                    RenderTrace.enterPage( i );
                }
                RenderTrace.exit();

                try
                {
                    sleep( 10 );
                }
                catch ( InterruptedException e )
                {
                    e.printStackTrace();
                }
            }

            history = getHistoryForUser( user );
        }
    }
}