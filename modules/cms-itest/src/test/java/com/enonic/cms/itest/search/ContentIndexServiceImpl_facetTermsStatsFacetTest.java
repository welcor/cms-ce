package com.enonic.cms.itest.search;

import java.util.Iterator;

import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.search.result.FacetResultSet;
import com.enonic.cms.core.search.result.FacetsResultSet;
import com.enonic.cms.core.search.result.TermsStatsFacetResultEntry;
import com.enonic.cms.core.search.result.TermsStatsFacetResultSet;

import static org.junit.Assert.*;

public class ContentIndexServiceImpl_facetTermsStatsFacetTest
    extends ContentIndexServiceFacetTestBase
{

    @Test
    public void simple()
    {
        createAndIndexContent( 1, new String[]{"10", "a"}, new String[]{"data/price", "data/term"} );
        createAndIndexContent( 2, new String[]{"200", "a"}, new String[]{"data/price", "data/term"} );
        createAndIndexContent( 3, new String[]{"300", "a"}, new String[]{"data/price", "data/term"} );
        createAndIndexContent( 4, new String[]{"0", "b"}, new String[]{"data/price", "data/term"} );
        createAndIndexContent( 5, new String[]{"-100", "c"}, new String[]{"data/price", "data/term"} );
        createAndIndexContent( 6, new String[]{"0", "c"}, new String[]{"data/price", "data/term"} );
        createAndIndexContent( 7, new String[]{"1000", "c"}, new String[]{"data/price", "data/term"} );
        createAndIndexContent( 7, new String[]{"999", "c"}, new String[]{"data/price", "data/term"} );
        flushIndex();

        ContentIndexQuery query = new ContentIndexQuery( "" );

        final String facetDefinition = "<facets>\n" +
            "    <terms-stats name=\"myTermsStatsFacet\">\n" +
            "        <index>data/term</index>\n" +
            "        <value-index>data/price</value-index>\n" +
            "    </terms-stats >\n" +
            "</facets>";
        query.setFacets( facetDefinition );

        final ContentResultSet contentResultSet = contentIndexService.query( query );

        final FacetsResultSet facetsResultSet = contentResultSet.getFacetsResultSet();
        assertNotNull( facetsResultSet );
        assertTrue( facetsResultSet.iterator().hasNext() );

        final FacetResultSet termsStatsFacet = facetsResultSet.iterator().next();
        assertNotNull( termsStatsFacet );
        assertTrue( termsStatsFacet instanceof TermsStatsFacetResultSet );

        String expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<content>\n" +
            "  <facets>\n" +
            "    <terms-stats name=\"myTermsStatsFacet\">\n" +
            "      <term total=\"899.0\" count=\"3\" min=\"-100.0\" mean=\"299.6666666666667\" max=\"999.0\">c</term>\n" +
            "      <term total=\"510.0\" count=\"3\" min=\"10.0\" mean=\"170.0\" max=\"300.0\">a</term>\n" +
            "      <term total=\"0.0\" count=\"1\" min=\"0.0\" mean=\"0.0\" max=\"0.0\">b</term>\n" +
            "    </terms-stats>\n" +
            "  </facets>\n" +
            "</content>";

        createAndCompareResultAsXml( contentResultSet, expectedXml );
    }

    @Test
    public void orderby_min()
    {
        createAndIndexContent( 1, new String[]{"10", "a"}, new String[]{"data/price", "data/term"} );
        createAndIndexContent( 2, new String[]{"200", "a"}, new String[]{"data/price", "data/term"} );
        createAndIndexContent( 3, new String[]{"300", "a"}, new String[]{"data/price", "data/term"} );
        createAndIndexContent( 4, new String[]{"0", "b"}, new String[]{"data/price", "data/term"} );
        createAndIndexContent( 5, new String[]{"-100", "c"}, new String[]{"data/price", "data/term"} );
        createAndIndexContent( 6, new String[]{"0", "c"}, new String[]{"data/price", "data/term"} );
        createAndIndexContent( 7, new String[]{"1000", "c"}, new String[]{"data/price", "data/term"} );
        flushIndex();

        ContentIndexQuery query = new ContentIndexQuery( "" );

        final String facetDefinition = "<facets>\n" +
            "    <terms-stats name=\"myTermsStatsFacet\">\n" +
            "        <index>data/term</index>\n" +
            "        <value-index>data/price</value-index>\n" +
            "        <orderby>min</orderby>\n" +
            "    </terms-stats >\n" +
            "</facets>";
        query.setFacets( facetDefinition );

        final ContentResultSet contentResultSet = contentIndexService.query( query );

        final FacetsResultSet facetsResultSet = contentResultSet.getFacetsResultSet();
        final FacetResultSet termsStatsFacet = facetsResultSet.iterator().next();
        TermsStatsFacetResultSet termFacetResultSet = (TermsStatsFacetResultSet) termsStatsFacet;
        final Iterator<TermsStatsFacetResultEntry> termsStatsFacetResultEntryIterator = termFacetResultSet.getResults().iterator();

        assertEquals( "c", termsStatsFacetResultEntryIterator.next().getTerm() );
        assertEquals( "b", termsStatsFacetResultEntryIterator.next().getTerm() );
        assertEquals( "a", termsStatsFacetResultEntryIterator.next().getTerm() );

    }

    @Test
    public void orderby_max()
    {
        createAndIndexContent( 1, new String[]{"10", "a"}, new String[]{"data/price", "data/term"} );
        createAndIndexContent( 2, new String[]{"200", "a"}, new String[]{"data/price", "data/term"} );
        createAndIndexContent( 3, new String[]{"300", "a"}, new String[]{"data/price", "data/term"} );
        createAndIndexContent( 4, new String[]{"0", "b"}, new String[]{"data/price", "data/term"} );
        createAndIndexContent( 5, new String[]{"-100", "c"}, new String[]{"data/price", "data/term"} );
        createAndIndexContent( 6, new String[]{"0", "c"}, new String[]{"data/price", "data/term"} );
        createAndIndexContent( 7, new String[]{"1000", "c"}, new String[]{"data/price", "data/term"} );
        flushIndex();

        ContentIndexQuery query = new ContentIndexQuery( "" );

        final String facetDefinition = "<facets>\n" +
            "    <terms-stats name=\"myTermsStatsFacet\">\n" +
            "        <index>data/term</index>\n" +
            "        <value-index>data/price</value-index>\n" +
            "        <orderby>max</orderby>\n" +
            "    </terms-stats >\n" +
            "</facets>";
        query.setFacets( facetDefinition );

        final ContentResultSet contentResultSet = contentIndexService.query( query );

        final FacetsResultSet facetsResultSet = contentResultSet.getFacetsResultSet();
        final FacetResultSet termsStatsFacet = facetsResultSet.iterator().next();
        TermsStatsFacetResultSet termFacetResultSet = (TermsStatsFacetResultSet) termsStatsFacet;
        final Iterator<TermsStatsFacetResultEntry> termsStatsFacetResultEntryIterator = termFacetResultSet.getResults().iterator();

        assertEquals( "c", termsStatsFacetResultEntryIterator.next().getTerm() );
        assertEquals( "a", termsStatsFacetResultEntryIterator.next().getTerm() );
        assertEquals( "b", termsStatsFacetResultEntryIterator.next().getTerm() );
    }


    @Test
    public void multifield()
    {
        createAndIndexContent( 1, new String[]{"10", "a"}, new String[]{"data/price", "data/term"} );
        createAndIndexContent( 2, new String[]{"200", "a"}, new String[]{"data/price", "data/term"} );
        createAndIndexContent( 3, new String[]{"300", "a", "b"}, new String[]{"data/price", "data/term", "data/term"} );
        createAndIndexContent( 4, new String[]{"0", "b", "a"}, new String[]{"data/price", "data/term", "data/term"} );
        createAndIndexContent( 5, new String[]{"-100", "c", "b", "a"}, new String[]{"data/price", "data/term", "data/term", "data/term"} );
        createAndIndexContent( 6, new String[]{"0", "c", "b"}, new String[]{"data/price", "data/term", "data/term"} );
        createAndIndexContent( 7, new String[]{"1000", "c", "b"}, new String[]{"data/price", "data/term", "data/term"} );
        createAndIndexContent( 8, new String[]{"2000", "c", "b"}, new String[]{"data/price", "data/term", "data/term"} );
        flushIndex();

        ContentIndexQuery query = new ContentIndexQuery( "" );

        final String facetDefinition = "<facets>\n" +
            "    <terms-stats name=\"myTermsStatsFacet\">\n" +
            "        <index>data/term</index>\n" +
            "        <value-index>data/price</value-index>\n" +
            "        <orderby>count</orderby>\n" +
            "    </terms-stats >\n" +
            "</facets>";
        query.setFacets( facetDefinition );

        final ContentResultSet contentResultSet = contentIndexService.query( query );

        final FacetsResultSet facetsResultSet = contentResultSet.getFacetsResultSet();
        final FacetResultSet termsStatsFacet = facetsResultSet.iterator().next();

        TermsStatsFacetResultSet termFacetResultSet = (TermsStatsFacetResultSet) termsStatsFacet;
        final Iterator<TermsStatsFacetResultEntry> termsStatsFacetResultEntryIterator = termFacetResultSet.getResults().iterator();

        assertEquals( "b", termsStatsFacetResultEntryIterator.next().getTerm() );
        assertEquals( "a", termsStatsFacetResultEntryIterator.next().getTerm() );
        assertEquals( "c", termsStatsFacetResultEntryIterator.next().getTerm() );

        String expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<content>\n" +
            "  <facets>\n" +
            "    <terms-stats name=\"myTermsStatsFacet\">\n" +
            "      <term total=\"3200.0\" count=\"6\" min=\"-100.0\" mean=\"533.3333333333334\" max=\"2000.0\">b</term>\n" +
            "      <term total=\"410.0\" count=\"5\" min=\"-100.0\" mean=\"82.0\" max=\"300.0\">a</term>\n" +
            "      <term total=\"2900.0\" count=\"4\" min=\"-100.0\" mean=\"725.0\" max=\"2000.0\">c</term>\n" +
            "    </terms-stats>\n" +
            "  </facets>\n" +
            "</content>";

        createAndCompareResultAsXml( contentResultSet, expectedXml );

    }


}
