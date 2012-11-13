package com.enonic.cms.core.search.result;

import org.elasticsearch.common.Strings;
import org.jdom.Document;
import org.jdom.Element;
import org.junit.Test;

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
            "    <MyTermFacet>\n" +
            "      <result>\n" +
            "        <term>term1sef</term>\n" +
            "        <count>10</count>\n" +
            "      </result>\n" +
            "      <result>\n" +
            "        <term>s2312</term>\n" +
            "        <count>12</count>\n" +
            "      </result>\n" +
            "      <result>\n" +
            "        <term>123 fsef __´!#!$# term3</term>\n" +
            "        <count>14</count>\n" +
            "      </result>\n" +
            "      <result>\n" +
            "        <term>term4</term>\n" +
            "        <count>16</count>\n" +
            "      </result>\n" +
            "      <result>\n" +
            "        <term>term5</term>\n" +
            "        <count>18</count>\n" +
            "      </result>\n" +
            "      <result>\n" +
            "        <term>term6</term>\n" +
            "        <count>20</count>\n" +
            "      </result>\n" +
            "    </MyTermFacet>\n" +
            "  </facets>\n" +
            "</root>";

        FacetResultXmlCreator resultXmlCreator = new FacetResultXmlCreator();

        Document doc = new Document( new Element( "root" ) );

        FacetsResultSet facetsResultSet = new FacetsResultSet();
        TermsFacetResultSet termFacetResult = new TermsFacetResultSet();
        termFacetResult.setName( "MyTermFacet" );
        termFacetResult.setRequiredSize( 10L );
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
        expected = Strings.trimAllWhitespace( expected );
        resultString = Strings.trimAllWhitespace( resultString );
        assertEquals( Strings.trimTrailingWhitespace( expected ), Strings.trimTrailingWhitespace( resultString ) );
    }
}
