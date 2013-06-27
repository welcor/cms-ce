/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.xslt.portal;

import java.util.Map;

import javax.xml.transform.Transformer;

import org.junit.Test;

import static org.junit.Assert.*;

import com.enonic.cms.core.xslt.base.SaxonXsltProcessorTest;

public class PortalXsltProcessorImplTest
    extends SaxonXsltProcessorTest<PortalXsltProcessorImpl>
{
    @Override
    protected PortalXsltProcessorImpl createProcessor( final Transformer transformer )
        throws Exception
    {
        return new PortalXsltProcessorImpl( transformer );
    }

    @Test
    public void testCustomParameterTypes()
        throws Exception
    {
        final PortalXsltProcessor processor = createProcessor( getClass(), "customParameterTypes.xsl" );
        assertNotNull( processor );

        final Map<String, String> map = processor.getCustomParameterTypes();
        assertNotNull( map );

        assertEquals( 3, map.size() );

        assertTrue( map.containsKey( "param1" ) );
        assertEquals( null, map.get( "param1" ) );

        assertTrue( map.containsKey( "param2" ) );
        assertEquals( null, map.get( "param2" ) );

        assertTrue( map.containsKey( "param3" ) );
        assertEquals( "content", map.get( "param3" ) );

        assertFalse( map.containsKey( "param4" ) );
    }

    @Test
    public void testCustomParameterTypes_noParams()
        throws Exception
    {
        final PortalXsltProcessor processor = createProcessor( getClass(), "customParameterTypes_noParams.xsl" );
        assertNotNull( processor );

        final Map<String, String> map = processor.getCustomParameterTypes();
        assertNotNull( map );

        assertEquals( 0, map.size() );
    }
}
