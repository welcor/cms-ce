package com.enonic.cms.core.search.result;

import org.jdom.Document;
import org.jdom.Element;
import org.junit.Test;

public class TermFacetResultSetXmlCreatorTest
    extends FacetResultSetTestBase
{

    @Test
    public void simple_term_facet_result()
    {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<root>\n" +
            "  <facets>\n" +
            "    <terms name=\"MyTermFacet\" total=\"100\" missing=\"5\" other=\"10\">\n" +
            "      <term count=\"10\">term1sef</term>\n" +
            "      <term count=\"12\">s2312</term>\n" +
            "      <term count=\"14\">123 fsef __´!#!$# term3</term>\n" +
            "      <term count=\"16\">term4</term>\n" +
            "      <term count=\"18\">term5</term>\n" +
            "      <term count=\"20\">term6</term>\n" +
            "    </terms>\n" +
            "  </facets>\n" +
            "</root>";

        FacetResultSetXmlCreator resultSetXmlCreator = new FacetResultSetXmlCreator();

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

        resultSetXmlCreator.addFacetResultXml( doc, facetsResultSet );

        compareIgnoreWhitespacesAndLinebreaks( expected, doc );
    }


}
