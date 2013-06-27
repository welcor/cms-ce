/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.xslt.functions.admin;

import org.junit.Test;

import static org.junit.Assert.*;

public class UniqueIdGeneratorImplTest
{
    @Test
    public void testUniqueness()
    {
        final UniqueIdGeneratorImpl function = new UniqueIdGeneratorImpl();
        final String id1 = function.generateUniqueId();
        final String id2 = function.generateUniqueId();

        assertNotNull( id1 );
        assertNotNull( id2 );
        assertFalse( id1.equals( id2 ) );
    }
}
