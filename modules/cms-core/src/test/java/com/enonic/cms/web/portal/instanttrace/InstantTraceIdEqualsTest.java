/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.web.portal.instanttrace;

import org.junit.Test;

import com.enonic.cms.core.AbstractEqualsTest;

public class InstantTraceIdEqualsTest
    extends AbstractEqualsTest
{

    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        return new InstantTraceId( 1l );
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        return new Object[]{new InstantTraceId( 2l )};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        return new InstantTraceId( 1l );
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        return new InstantTraceId( 1l );
    }
}
