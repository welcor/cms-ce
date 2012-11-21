package com.enonic.cms.itest.search;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.search.result.FacetResultSetXmlCreator;

import static org.junit.Assert.*;

public class ContentIndexServiceFacetTestBase
    extends ContentIndexServiceTestBase
{

    protected void createAndCompareResultAsXml( final ContentResultSet result, final FacetResultSetXmlCreator facetResultSetXmlCreator,
                                                final String expectedXml )
    {
        Document doc = new Document();
        doc.addContent( new Element( "content" ) );

        facetResultSetXmlCreator.addFacetResultXml( doc, result.getFacetsResultSet() );
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
