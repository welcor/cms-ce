/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.result;

import org.jdom.Document;

import com.enonic.cms.framework.util.JDOMUtil;

import static org.junit.Assert.*;

public class FacetResultSetTestBase
{

    protected void compareIgnoreWhitespacesAndLinebreaks( String expectedXml, final Document doc )
    {
        final String resultXml = JDOMUtil.prettyPrintDocument( doc );

        final String expectedXmlTrimmed = expectedXml.replace( "\n", "" ).replace( "\r", "" );
        final String resultXmlTrimmed = resultXml.replace( "\n", "" ).replace( "\r", "" );

        // Trickery to get the nice output of diff
        if ( !expectedXmlTrimmed.equals( resultXmlTrimmed ) )
        {
            assertEquals( expectedXml, resultXml );
        }
        else
        {
            assertEquals( expectedXmlTrimmed, resultXmlTrimmed );
        }
    }
}
