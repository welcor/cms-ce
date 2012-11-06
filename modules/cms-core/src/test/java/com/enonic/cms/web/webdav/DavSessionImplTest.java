package com.enonic.cms.web.webdav;

import org.junit.Test;

import static org.junit.Assert.*;

public class DavSessionImplTest
{
    @Test
    public void testTokens()
    {
        final DavSessionImpl session = new DavSessionImpl();
        final String myToken = "myToken";

        session.addLockToken( myToken );
        assertEquals( 1, session.getLockTokens().length );
        assertEquals( myToken, session.getLockTokens()[0] );

        session.removeLockToken( myToken );
        assertEquals( 0, session.getLockTokens().length );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddReference()
    {
        final DavSessionImpl session = new DavSessionImpl();
        session.addReference( new Object() );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemoveReference()
    {
        final DavSessionImpl session = new DavSessionImpl();
        session.removeReference( new Object() );
    }
}
