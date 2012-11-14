package com.enonic.cms.core.search.result;

import org.jdom.Document;
import org.jdom.Element;
import org.junit.Test;
import org.springframework.util.StringUtils;

import com.enonic.cms.framework.util.JDOMUtil;

import static org.junit.Assert.*;

public class FacetResultXmlCreatorTest
{
    @Test
    public void testStuff()
    {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<root>\n" +
            "  <facets>\n" +
            "    <facet name=\"MyTermFacet\" total=\"100\" missing=\"5\" other=\"10\">\n" +
            "      <result term=\"term1sef \">\n" +
            "        <count>10</count>\n" +
            "      </result>\n" +
            "      <result term=\" s2312\">\n" +
            "        <count>12</count>\n" +
            "      </result>\n" +
            "      <result term=\"123 fsef __´!#!$# term3\">\n" +
            "        <count>14</count>\n" +
            "      </result>\n" +
            "      <result term=\"term4\">\n" +
            "        <count>16</count>\n" +
            "      </result>\n" +
            "      <result term=\"term5\">\n" +
            "        <count>18</count>\n" +
            "      </result>\n" +
            "      <result term=\"term6\">\n" +
            "        <count>20</count>\n" +
            "      </result>\n" +
            "    </facet>\n" +
            "  </facets>\n" +
            "</root>";

        FacetResultXmlCreator resultXmlCreator = new FacetResultXmlCreator();

        Document doc = new Document( new Element( "root" ) );

        FacetsResultSet facetsResultSet = new FacetsResultSet();
        TermsFacetResultSet termFacetResult = new TermsFacetResultSet();
        termFacetResult.setName( "MyTermFacet" );
        termFacetResult.setOther( 10L );
        termFacetResult.setTotal( 100L );
        termFacetResult.setMissing( 5L );
        termFacetResult.addResult( "term1sef ", 10 );
        termFacetResult.addResult( " s2312", 12 );
        termFacetResult.addResult( "123 fsef __´!#!$# term3", 14 );
        termFacetResult.addResult( "term4", 16 );
        termFacetResult.addResult( "term5", 18 );
        termFacetResult.addResult( "term6", 20 );

        facetsResultSet.addFacetResultSet( termFacetResult );

        resultXmlCreator.addFacetResultXml( doc, facetsResultSet );

        compareIgnoreWhitespacesAndLinebreaks( expected, doc );
    }

    private void compareIgnoreWhitespacesAndLinebreaks( String expected, final Document doc )
    {
        String resultString = JDOMUtil.prettyPrintDocument( doc );

        System.out.println(resultString);

        expected = StringUtils.trimAllWhitespace( expected );
        resultString = StringUtils.trimAllWhitespace( resultString );
        assertEquals( StringUtils.trimTrailingWhitespace( expected ), StringUtils.trimTrailingWhitespace( resultString ) );
    }
}
