/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.web.webdav;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class DavResourceImplTest
{
    @Test
    public void testAlterProperties()
        throws DavException
    {
        final DavResourceImpl resource = new DavResourceImpl( null, null, null, null, null );

        MultiStatusResponse response = resource.alterProperties( null );

        assertEquals( "/", response.getHref() );
        assertEquals( 1, response.getStatus().length );
        assertEquals( 1, response.getStatus()[0].getStatusCode() );
    }
}
