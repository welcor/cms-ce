/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.web.portal.instanttrace;

import org.junit.Test;

import static org.junit.Assert.*;

public class InstantTraceIdTest
{

    @Test
    public void constructor_with_string()
    {
        InstantTraceId instantTraceId = new InstantTraceId( "1" );
        assertEquals( new Long( 1 ), instantTraceId.getTraceCompletedNumber() );
    }

    @Test
    public void tostring()
    {
        InstantTraceId instantTraceId = new InstantTraceId( (long) 1 );
        assertEquals( "1", instantTraceId.toString() );
    }
}
