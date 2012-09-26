package com.enonic.cms.web.portal.instanttrace;

import org.junit.Test;

import com.enonic.cms.core.portal.livetrace.PortalRequestTrace;

import static org.junit.Assert.*;


public class InstantTraceSessionObjectTest
{
    @Test
    public void adding_max_number_of_traces_then_all_is_kept()
    {
        // exercise
        InstantTraceSessionObject object = new InstantTraceSessionObject( 3 );
        object.addTrace( new InstantTraceId( "ABC:1" ), new PortalRequestTrace( 1, "http://myurl.com" ) );
        object.addTrace( new InstantTraceId( "ABC:2" ), new PortalRequestTrace( 2, "http://myurl.com" ) );
        object.addTrace( new InstantTraceId( "ABC:3" ), new PortalRequestTrace( 3, "http://myurl.com" ) );

        // verify
        assertNotNull( object.getTrace( new InstantTraceId( "ABC:1" ) ) );
        assertNotNull( object.getTrace( new InstantTraceId( "ABC:2" ) ) );
        assertNotNull( object.getTrace( new InstantTraceId( "ABC:3" ) ) );
    }

    @Test
    public void adding_more_traces_than_max_then_first_one_is_removed()
    {
        InstantTraceSessionObject object = new InstantTraceSessionObject( 3 );
        object.addTrace( new InstantTraceId( "ABC:1" ), new PortalRequestTrace( 1, "http://myurl.com" ) );
        object.addTrace( new InstantTraceId( "ABC:2" ), new PortalRequestTrace( 2, "http://myurl.com" ) );
        object.addTrace( new InstantTraceId( "ABC:3" ), new PortalRequestTrace( 3, "http://myurl.com" ) );

        // exercise
        object.addTrace( new InstantTraceId( "ABC:4" ), new PortalRequestTrace( 4, "http://myurl.com" ) );

        // verify
        assertNull( object.getTrace( new InstantTraceId( "ABC:1" ) ) );
        assertNotNull( object.getTrace( new InstantTraceId( "ABC:2" ) ) );
        assertNotNull( object.getTrace( new InstantTraceId( "ABC:3" ) ) );
        assertNotNull( object.getTrace( new InstantTraceId( "ABC:4" ) ) );
    }
}
