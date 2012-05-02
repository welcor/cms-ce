package com.enonic.cms.core.search.builder.indexdata;

import org.junit.Test;

import static org.junit.Assert.*;

public class ContentIndexOrderbyResolverTest
{
    @Test
    public void testNullValues()
    {
        assertNull( ContentIndexOrderbyValueResolver.resolveOrderbyValue( null ) );

    }

}
