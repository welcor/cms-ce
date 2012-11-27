package com.enonic.cms.itest.search;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.index.UserDefinedField;
import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.search.query.ContentDocument;
import com.enonic.cms.core.search.query.IndexQueryException;
import com.enonic.cms.core.search.query.SimpleText;
import com.enonic.cms.core.search.result.FacetResultSet;
import com.enonic.cms.core.search.result.FacetsResultSet;
import com.enonic.cms.core.search.result.TermsFacetResultSet;

import static org.junit.Assert.*;

public class ContentIndexServiceImpl_facetTermsFacetTest
    extends ContentIndexServiceFacetTestBase
{

    @Test
    public void query_with_no_facet()
    {
        setUpValuesWithFacetGoodies();

        ContentIndexQuery query = new ContentIndexQuery( "" );

        final ContentResultSet result = contentIndexService.query( query );

        final FacetsResultSet facetsResultSet = result.getFacetsResultSet();

        assertTrue( facetsResultSet == null );
    }

    @Test
    public void single_facet()
    {
        setUpValuesWithFacetGoodies();

        ContentIndexQuery query = new ContentIndexQuery( "" );
        final String facetName = "specietypes";
        final String facetDefinition = "<facets>\n" +
            "    <terms name=\"" + facetName + "\">\n" +
            "        <indices>data/person/type</indices>\n" +
            "    </terms>\n" +
            "</facets>";
        query.setFacets( facetDefinition );

        final ContentResultSet result = contentIndexService.query( query );

        final FacetsResultSet facetsResultSet = result.getFacetsResultSet();
        assertNotNull( facetsResultSet );
        assertTrue( facetsResultSet.iterator().hasNext() );

        final FacetResultSet termFacet = facetsResultSet.iterator().next();
        assertNotNull( termFacet );
        assertEquals( facetName, termFacet.getName() );
        assertTrue( termFacet instanceof TermsFacetResultSet );

        TermsFacetResultSet termFacetResultSet = (TermsFacetResultSet) termFacet;

        final Map<String, Integer> results = termFacetResultSet.getResults();
        assertEquals( 2L, (long) results.get( "robot" ) );
        assertEquals( 3L, (long) results.get( "human" ) );
        assertEquals( 1L, (long) results.get( "alien" ) );

        String expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<content>\n" +
            "  <facets>\n" +
            "    <terms name=\"specietypes\" total=\"6\" missing=\"0\" other=\"0\">\n" +
            "      <term hits=\"3\">human</term>\n" +
            "      <term hits=\"2\">robot</term>\n" +
            "      <term hits=\"1\">alien</term>\n" +
            "    </terms>\n" +
            "  </facets>\n" +
            "</content>";

        createAndCompareResultAsXml( result, expectedXml );

    }

    @Test
    public void multi_facets()
    {
        // Setup standard values
        setUpValuesWithFacetGoodies();

        ContentIndexQuery query = new ContentIndexQuery( "" );
        final String facetDefinition = "<facets>\n" +
            "    <terms name=\"facet1\">\n" +
            "        <indices>data/person/type</indices>\n" +
            "    </terms>\n" +
            "    <terms name=\"facet2\">\n" +
            "        <indices>data/person/drink</indices>\n" +
            "    </terms>\n" +
            "</facets>";
        query.setFacets( facetDefinition );

        final ContentResultSet result = contentIndexService.query( query );

        FacetsResultSet facetsResultSet = result.getFacetsResultSet();
        assertNotNull( facetsResultSet );

        assertTrue( facetsResultSet.iterator().hasNext() );
        FacetResultSet termFacet = facetsResultSet.iterator().next();
        assertNotNull( termFacet );
        assertTrue( termFacet instanceof TermsFacetResultSet );
        termFacet = facetsResultSet.iterator().next();
        assertNotNull( termFacet );
        assertTrue( termFacet instanceof TermsFacetResultSet );

        String expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<content>\n" +
            "  <facets>\n" +
            "    <terms name=\"facet1\" total=\"6\" missing=\"0\" other=\"0\">\n" +
            "      <term hits=\"3\">human</term>\n" +
            "      <term hits=\"2\">robot</term>\n" +
            "      <term hits=\"1\">alien</term>\n" +
            "    </terms>\n" +
            "    <terms name=\"facet2\" total=\"6\" missing=\"0\" other=\"0\">\n" +
            "      <term hits=\"3\">beer</term>\n" +
            "      <term hits=\"2\">oil</term>\n" +
            "      <term hits=\"1\">blood</term>\n" +
            "    </terms>\n" +
            "  </facets>\n" +
            "</content>";
        createAndCompareResultAsXml( result, expectedXml );
    }

    @Test
    public void size()
    {
        setUpValuesWithFacetGoodies();

        ContentIndexQuery query = new ContentIndexQuery( "" );
        final String facetName = "specietypes";
        final String facetDefinition = "<facets>\n" +
            "    <terms name=\"" + facetName + "\">\n" +
            "        <indices>data/person/type</indices>\n" +
            "        <count>3</count>\n" +
            "    </terms>\n" +
            "</facets>";
        query.setFacets( facetDefinition );

        final ContentResultSet result = contentIndexService.query( query );

        final FacetsResultSet facetsResultSet = result.getFacetsResultSet();
        assertNotNull( facetsResultSet );
        assertTrue( facetsResultSet.iterator().hasNext() );

        final FacetResultSet termFacet = facetsResultSet.iterator().next();
        assertNotNull( termFacet );
        assertEquals( facetName, termFacet.getName() );
        assertTrue( termFacet instanceof TermsFacetResultSet );

        TermsFacetResultSet termFacetResultSet = (TermsFacetResultSet) termFacet;

        final Map<String, Integer> results = termFacetResultSet.getResults();
        assertEquals( 3, results.keySet().size() );

        String expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<content>\n" +
            "  <facets>\n" +
            "    <terms name=\"specietypes\" total=\"6\" missing=\"0\" other=\"0\">\n" +
            "      <term hits=\"3\">human</term>\n" +
            "      <term hits=\"2\">robot</term>\n" +
            "      <term hits=\"1\">alien</term>\n" +
            "    </terms>\n" +
            "  </facets>\n" +
            "</content>\n";
        createAndCompareResultAsXml( result, expectedXml );
    }

    @Test
    public void fields()
    {
        setUpValuesWithFacetGoodies();

        ContentIndexQuery query = new ContentIndexQuery( "" );
        final String facetName = "specietypes";
        final String facetDefinition = "<facets>\n" +
            "    <terms name=\"" + facetName + "\">\n" +
            "        <indices>data/person/type,data/person/type,data/person/gender,data/person/drink</indices>\n" +
            "        <count>3</count>\n" +
            "        <orderby>count</orderby>\n" +
            "    </terms>\n" +
            "</facets>";
        query.setFacets( facetDefinition );

        final ContentResultSet result = contentIndexService.query( query );

        final FacetsResultSet facetsResultSet = result.getFacetsResultSet();
        assertNotNull( facetsResultSet );
        assertTrue( facetsResultSet.iterator().hasNext() );

        final FacetResultSet termFacet = facetsResultSet.iterator().next();
        assertNotNull( termFacet );
        assertEquals( facetName, termFacet.getName() );
        assertTrue( termFacet instanceof TermsFacetResultSet );

        TermsFacetResultSet termFacetResultSet = (TermsFacetResultSet) termFacet;

        final Map<String, Integer> results = termFacetResultSet.getResults();
        assertEquals( 4L, (long) results.get( "male" ) );
        assertEquals( 3L, (long) results.get( "human" ) );
        assertEquals( 3L, (long) results.get( "beer" ) );

        String expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<content>\n" +
            "  <facets>\n" +
            "    <terms name=\"specietypes\" total=\"18\" missing=\"0\" other=\"8\">\n" +
            "      <term hits=\"4\">male</term>\n" +
            "      <term hits=\"3\">human</term>\n" +
            "      <term hits=\"3\">beer</term>\n" +
            "    </terms>\n" +
            "  </facets>\n" +
            "</content>";
        createAndCompareResultAsXml( result, expectedXml );
    }


    @Test
    public void ordering_default()
    {
        setUpValuesWithFacetGoodies();
        flushIndex();

        ContentIndexQuery query = new ContentIndexQuery( "" );
        final String facetName = "specietypes";
        String facetDefinition = "<facets>\n" +
            "    <terms name=\"" + facetName + "\">\n" +
            "        <indices>data/person/type</indices>\n" +
            "    </terms>\n" +
            "</facets>";
        query.setFacets( facetDefinition );
        ContentResultSet result = contentIndexService.query( query );

        TermsFacetResultSet termFacetResultSet = getTermFacetResultSet( result );
        Iterator<String> resultIterator = getResultIterator( termFacetResultSet );

        // Default sorting count
        assertEquals( "human", resultIterator.next() );
        assertEquals( "robot", resultIterator.next() );
        assertEquals( "alien", resultIterator.next() );

        String expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<content>\n" +
            "  <facets>\n" +
            "    <terms name=\"specietypes\" total=\"6\" missing=\"0\" other=\"0\">\n" +
            "      <term hits=\"3\">human</term>\n" +
            "      <term hits=\"2\">robot</term>\n" +
            "      <term hits=\"1\">alien</term>\n" +
            "    </terms>\n" +
            "  </facets>\n" +
            "</content>";
        createAndCompareResultAsXml( result, expectedXml );
    }

    @Test
    public void ordering_reverse_count()
    {
        setUpValuesWithFacetGoodies();
        flushIndex();

        ContentIndexQuery query = new ContentIndexQuery( "" );
        final String facetName = "specietypes";
        String facetDefinition = "<facets>\n" +
            "    <terms name=\"" + facetName + "\">\n" +
            "        <indices>data/person/type</indices>\n" +
            "        <orderby>reverse_count</orderby>\n" +
            "    </terms>\n" +
            "</facets>";
        query.setFacets( facetDefinition );
        ContentResultSet result = contentIndexService.query( query );

        TermsFacetResultSet termFacetResultSet = getTermFacetResultSet( result );
        Iterator<String> resultIterator = getResultIterator( termFacetResultSet );

        // Default sorting count
        assertEquals( "alien", resultIterator.next() );
        assertEquals( "robot", resultIterator.next() );
        assertEquals( "human", resultIterator.next() );

        String expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<content>\n" +
            "  <facets>\n" +
            "    <terms name=\"specietypes\" total=\"6\" missing=\"0\" other=\"0\">\n" +
            "      <term hits=\"1\">alien</term>\n" +
            "      <term hits=\"2\">robot</term>\n" +
            "      <term hits=\"3\">human</term>\n" +
            "    </terms>\n" +
            "  </facets>\n" +
            "</content>";
        createAndCompareResultAsXml( result, expectedXml );
    }

    @Test
    public void ordering_term()
    {
        setUpValuesWithFacetGoodies();
        flushIndex();

        ContentIndexQuery query = new ContentIndexQuery( "" );
        final String facetName = "specietypes";
        String facetDefinition = "<facets>\n" +
            "    <terms name=\"" + facetName + "\">\n" +
            "        <indices>data/person/type</indices>\n" +
            "        <orderby>term</orderby>\n" +
            "    </terms>\n" +
            "</facets>";
        query.setFacets( facetDefinition );
        ContentResultSet result = contentIndexService.query( query );

        TermsFacetResultSet termFacetResultSet = getTermFacetResultSet( result );
        Iterator<String> resultIterator = getResultIterator( termFacetResultSet );

        // Default sorting count
        assertEquals( "alien", resultIterator.next() );
        assertEquals( "human", resultIterator.next() );
        assertEquals( "robot", resultIterator.next() );

        String expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<content>\n" +
            "  <facets>\n" +
            "    <terms name=\"specietypes\" total=\"6\" missing=\"0\" other=\"0\">\n" +
            "      <term hits=\"1\">alien</term>\n" +
            "      <term hits=\"3\">human</term>\n" +
            "      <term hits=\"2\">robot</term>\n" +
            "    </terms>\n" +
            "  </facets>\n" +
            "</content>\n" +
            "\n";
        createAndCompareResultAsXml( result, expectedXml );
    }

    @Test
    public void ordering_reverse_term()
    {
        setUpValuesWithFacetGoodies();
        flushIndex();

        ContentIndexQuery query = new ContentIndexQuery( "" );
        final String facetName = "specietypes";
        String facetDefinition = "<facets>\n" +
            "    <terms name=\"" + facetName + "\">\n" +
            "        <indices>data/person/type</indices>\n" +
            "        <orderby>reverse_term</orderby>\n" +
            "    </terms>\n" +
            "</facets>";
        query.setFacets( facetDefinition );
        ContentResultSet result = contentIndexService.query( query );

        TermsFacetResultSet termFacetResultSet = getTermFacetResultSet( result );
        Iterator<String> resultIterator = getResultIterator( termFacetResultSet );

        // Default sorting count
        assertEquals( "robot", resultIterator.next() );
        assertEquals( "human", resultIterator.next() );
        assertEquals( "alien", resultIterator.next() );

        String expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<content>\n" +
            "  <facets>\n" +
            "    <terms name=\"specietypes\" total=\"6\" missing=\"0\" other=\"0\">\n" +
            "      <term hits=\"2\">robot</term>\n" +
            "      <term hits=\"3\">human</term>\n" +
            "      <term hits=\"1\">alien</term>\n" +
            "    </terms>\n" +
            "  </facets>\n" +
            "</content>\n";
        createAndCompareResultAsXml( result, expectedXml );
    }

    @Test(expected = IndexQueryException.class)
    public void invalid_order_parameter()
    {
        setUpValuesWithFacetGoodies();
        flushIndex();

        ContentIndexQuery query = new ContentIndexQuery( "" );
        final String facetName = "specietypes";
        String facetDefinition = "<facets>\n" +
            "    <terms name=\"" + facetName + "\">\n" +
            "        <indices>data/person/type</indices>\n" +
            "        <orderby>sushi</orderby>\n" +
            "    </terms>\n" +
            "</facets>";
        query.setFacets( facetDefinition );
        contentIndexService.query( query );
    }


    @Test
    public void exclude_single_field()
    {
        setUpValuesWithFacetGoodies();
        flushIndex();

        ContentIndexQuery query = new ContentIndexQuery( "" );
        final String facetName = "specietypes";
        String facetDefinition = "<facets>\n" +
            "    <terms name=\"" + facetName + "\">\n" +
            "        <indices>data/person/type</indices>\n" +
            "        <exclude>robot</exclude>\n" +
            "    </terms>\n" +
            "</facets>";
        query.setFacets( facetDefinition );
        ContentResultSet result = contentIndexService.query( query );

        TermsFacetResultSet termFacetResultSet = getTermFacetResultSet( result );
        Iterator<String> resultIterator = getResultIterator( termFacetResultSet );

        // Default sorting count
        assertEquals( "human", resultIterator.next() );
        assertEquals( "alien", resultIterator.next() );

        assertFalse( resultIterator.hasNext() );
    }

    @Test
    public void exclude_multiple_fields()
    {
        setUpValuesWithFacetGoodies();
        flushIndex();

        ContentIndexQuery query = new ContentIndexQuery( "" );
        final String facetName = "specietypes";
        String facetDefinition = "<facets>\n" +
            "    <terms name=\"" + facetName + "\">\n" +
            "        <indices>data/person/type</indices>\n" +
            "        <exclude>robot,alien</exclude>\n" +
            "    </terms>\n" +
            "</facets>";
        query.setFacets( facetDefinition );
        ContentResultSet result = contentIndexService.query( query );

        TermsFacetResultSet termFacetResultSet = getTermFacetResultSet( result );
        Iterator<String> resultIterator = getResultIterator( termFacetResultSet );

        // Default sorting count
        assertEquals( "human", resultIterator.next() );
        assertFalse( resultIterator.hasNext() );
    }

    @Test
    public void regexp()
    {
        setUpValuesWithFacetGoodies();
        flushIndex();

        ContentIndexQuery query = new ContentIndexQuery( "" );
        final String facetName = "specietypes";
        final String facetDefinition = "<facets>\n" +
            "    <terms name=\"" + facetName + "\">\n" +
            "        <indices>data/person/food</indices>\n" +
            "        <regex>.*h.*</regex>\n" +
            "    </terms>\n" +
            "</facets>";
        query.setFacets( facetDefinition );

        final ContentResultSet result = contentIndexService.query( query );

        final FacetsResultSet facetsResultSet = result.getFacetsResultSet();
        assertNotNull( facetsResultSet );
        assertTrue( facetsResultSet.iterator().hasNext() );

        final FacetResultSet termFacet = facetsResultSet.iterator().next();
        assertNotNull( termFacet );
        assertEquals( facetName, termFacet.getName() );
        assertTrue( termFacet instanceof TermsFacetResultSet );

        TermsFacetResultSet termFacetResultSet = (TermsFacetResultSet) termFacet;

        final Map<String, Integer> results = termFacetResultSet.getResults();
        assertEquals( 1L, (long) results.get( "sushi" ) );
        assertEquals( 1L, (long) results.get( "shells" ) );
    }

    @Test
    public void regexp_with_flag()
    {
        setUpValuesWithFacetGoodies();
        flushIndex();

        ContentIndexQuery query = new ContentIndexQuery( "" );
        final String facetName = "specietypes";
        final String facetDefinition = "<facets>\n" +
            "    <terms name=\"" + facetName + "\">\n" +
            "        <indices>data/person/food</indices>\n" +
            "        <regex>S.*</regex>\n" +
            "        <regex-flags>CASE_INSENSITIVE,DOTALL</regex-flags>\n" +
            "    </terms>\n" +
            "</facets>";
        query.setFacets( facetDefinition );

        final ContentResultSet result = contentIndexService.query( query );

        final FacetsResultSet facetsResultSet = result.getFacetsResultSet();
        assertNotNull( facetsResultSet );
        assertTrue( facetsResultSet.iterator().hasNext() );

        final FacetResultSet termFacet = facetsResultSet.iterator().next();
        assertNotNull( termFacet );
        assertEquals( facetName, termFacet.getName() );
        assertTrue( termFacet instanceof TermsFacetResultSet );

        TermsFacetResultSet termFacetResultSet = (TermsFacetResultSet) termFacet;

        final Map<String, Integer> results = termFacetResultSet.getResults();
        assertEquals( 1L, (long) results.get( "sushi" ) );
        assertEquals( 1L, (long) results.get( "shells" ) );
    }


    private TermsFacetResultSet getTermFacetResultSet( final ContentResultSet result )
    {
        FacetsResultSet facetsResultSet = result.getFacetsResultSet();
        FacetResultSet termFacet = facetsResultSet.iterator().next();
        return (TermsFacetResultSet) termFacet;
    }

    private Iterator<String> getResultIterator( final TermsFacetResultSet termFacetResultSet )
    {
        Map<String, Integer> results = termFacetResultSet.getResults();
        Set<String> strings = results.keySet();
        return strings.iterator();
    }

    protected void setUpValuesWithFacetGoodies()
    {
        final GregorianCalendar date = new GregorianCalendar( 2008, Calendar.FEBRUARY, 28 );

        ContentDocument doc1 = new ContentDocument( new ContentKey( 1 ) );
        setMetadata( date, doc1 );
        doc1.setTitle( "Homer" );
        doc1.addUserDefinedField( "data/person/type", new SimpleText( "human" ) );
        doc1.addUserDefinedField( "data/person/gender", "male" );
        doc1.addUserDefinedField( "data/person/age", new SimpleText( "39" ) );
        doc1.addUserDefinedField( "data/person/food", new SimpleText( "pizza" ) );
        doc1.addUserDefinedField( "data/person/drink", "beer" );
        contentIndexService.index( doc1 );

        ContentDocument doc2 = new ContentDocument( new ContentKey( 2 ) );
        setMetadata( date, doc2 );
        doc2.setTitle( "r2d2" );
        doc2.addUserDefinedField( "data/person/type", new SimpleText( "robot" ) );
        doc2.addUserDefinedField( "data/person/gender", "unknown" );
        doc2.addUserDefinedField( "data/person/age", new SimpleText( "449" ) );
        doc2.addUserDefinedField( "data/person/food", new SimpleText( "oil" ) );
        doc2.addUserDefinedField( "data/person/drink", "oil" );
        contentIndexService.index( doc2 );

        ContentDocument doc3 = new ContentDocument( new ContentKey( 3 ) );
        setMetadata( date, doc3 );
        doc3.setTitle( "c3p0" );
        doc3.addUserDefinedField( "data/person/type", new SimpleText( "robot" ) );
        doc3.addUserDefinedField( "data/person/gender", "male" );
        doc3.addUserDefinedField( "data/person/age", new SimpleText( "150" ) );
        doc3.addUserDefinedField( "data/person/food", new SimpleText( "oil" ) );
        doc3.addUserDefinedField( "data/person/drink", "oil" );
        contentIndexService.index( doc3 );

        ContentDocument doc4 = new ContentDocument( new ContentKey( 4 ) );
        setMetadata( date, doc4 );
        doc4.setTitle( "Runar" );
        doc4.addUserDefinedField( "data/person/type", new SimpleText( "human" ) );
        doc4.addUserDefinedField( "data/person/gender", "male" );
        doc4.addUserDefinedField( "data/person/age", new SimpleText( "37" ) );
        doc4.addUserDefinedField( "data/person/food", new SimpleText( "sushi" ) );
        doc4.addUserDefinedField( "data/person/drink", "beer" );
        contentIndexService.index( doc4 );

        ContentDocument doc5 = new ContentDocument( new ContentKey( 5 ) );
        setMetadata( date, doc5 );
        doc5.setTitle( "Zorg" );
        doc5.addUserDefinedField( "data/person/type", new SimpleText( "alien" ) );
        doc5.addUserDefinedField( "data/person/gender", "male" );
        doc5.addUserDefinedField( "data/person/age", new SimpleText( "79" ) );
        doc5.addUserDefinedField( "data/person/food", new SimpleText( "humans" ) );
        doc5.addUserDefinedField( "data/person/drink", "beer" );
        contentIndexService.index( doc5 );

        ContentDocument doc6 = new ContentDocument( new ContentKey( 6 ) );
        setMetadata( date, doc6 );
        doc6.setTitle( "Vampira" );
        doc6.addUserDefinedField( "data/person/type", new SimpleText( "human" ) );
        doc6.addUserDefinedField( "data/person/gender", "female" );
        doc6.addUserDefinedField( "data/person/age", new SimpleText( "729" ) );
        doc6.addUserDefinedField( "data/person/food", new SimpleText( "Shells" ) );
        doc6.addUserDefinedField( "data/person/drink", "blood" );
        contentIndexService.index( doc6 );

        flushIndex();
    }

    protected ContentDocument createContentDocument( ContentKey contentKey, CategoryKey categoryKey, ContentTypeKey contentTypeKey,
                                                     String title, List<UserDefinedField> userDefinedFields )
    {
        ContentDocument doc = new ContentDocument( contentKey );
        doc.setCategoryKey( categoryKey );
        doc.setContentTypeKey( contentTypeKey );
        doc.setContentTypeName( "Article" );

        if ( title != null )
        {
            doc.setTitle( title );
        }

        for ( UserDefinedField userDefinedField : userDefinedFields )
        {
            doc.addUserDefinedField( userDefinedField );
        }

        doc.setStatus( 2 );
        doc.setPriority( 0 );
        return doc;
    }


}
