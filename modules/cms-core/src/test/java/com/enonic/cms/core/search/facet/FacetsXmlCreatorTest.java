package com.enonic.cms.core.search.facet;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

import org.elasticsearch.common.Strings;
import org.junit.Test;

import com.enonic.cms.core.search.facet.model.FacetsModel;
import com.enonic.cms.core.search.facet.model.RangeFacetModel;
import com.enonic.cms.core.search.facet.model.TermsFacetModel;

import static org.junit.Assert.*;

public class FacetsXmlCreatorTest
    implements ValidationEventHandler
{

    @Test
    public void testCreateTermsFacetXml_simple()
        throws Exception
    {
        FacetsModel facets = new FacetsModel();

        final TermsFacetModel facet = new TermsFacetModel();
        facet.setField( "termsFacetField" );
        facet.setName( "myFacetName" );
        facet.setSize( 10 );
        facets.addFacet( facet );
    }

    @Test
    public void testCreateTermsFacetXml_multi_field()
        throws Exception
    {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<facets>\n" +
            "    <terms>\n" +
            "        <name>myFacetName</name>\n" +
            "        <size>10</size>\n" +
            "        <fields>field1,field2,field3</fields>\n" +
            "    </terms>\n" +
            "</facets>\n";

        FacetsModel facets = new FacetsModel();

        final TermsFacetModel facet = new TermsFacetModel();
        facet.setFields( "field1,field2,field3" );
        facet.setName( "myFacetName" );
        facet.setSize( 10 );
        facets.addFacet( facet );

        final String xml = createXml( facets );

        assertEquals( Strings.trimTrailingWhitespace(expected), Strings.trimTrailingWhitespace(xml) );
    }

    @Test
    public void testCreateTermsFacetXml_all_fields()
        throws Exception
    {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<facets>\n" +
            "    <terms>\n" +
            "        <name>myFacetName</name>\n" +
            "        <size>10</size>\n" +
            "        <all_terms>true</all_terms>\n" +
            "        <exclude>exclude1,exclude2,exclude3</exclude>\n" +
            "        <field>field</field>\n" +
            "        <fields>fields1, fields2, fields3</fields>\n" +
            "        <order>orderby</order>\n" +
            "        <regex>regexp</regex>\n" +
            "        <regex_flags>DOTALL</regex_flags>\n" +
            "    </terms>\n" +
            "</facets>";

        FacetsModel facets = new FacetsModel();

        final TermsFacetModel facet = new TermsFacetModel();
        facet.setField( "field" );
        facet.setFields( "fields1, fields2, fields3" );
        facet.setExclude( "exclude1,exclude2,exclude3" );
        facet.setName( "myFacetName" );
        facet.setAllTerms( true );
        facet.setOrder( "orderby" );
        facet.setSize( 10 );
        facet.setRegex( "regexp" );
        facet.setRegexFlags( "DOTALL" );
        facets.addFacet( facet );

        final String xml = createXml( facets );

        assertEquals( Strings.trimTrailingWhitespace(expected), Strings.trimTrailingWhitespace(xml) );
    }


    @Test
    public void testRangeFacetXml()
    {
        FacetsModel facets = new FacetsModel();

        final RangeFacetModel rangeFacet = new RangeFacetModel();
        rangeFacet.addRange( null, 49 );
        rangeFacet.addRange( 50, 100 );
        rangeFacet.addRange( 101, 200 );
        rangeFacet.addRange( 201, null );
        rangeFacet.setField( "rangeField" );
        facets.addFacet( rangeFacet );

        final RangeFacetModel rangeFacet2 = new RangeFacetModel();
        rangeFacet2.addRange( null, 49 );
        rangeFacet2.addRange( 50, 100 );
        rangeFacet2.addRange( 101, 200 );
        rangeFacet2.addRange( 201, null );
        rangeFacet2.setKeyField( "rangeKeyField" );
        rangeFacet2.setValueField( "rangeValueField" );
        facets.addFacet( rangeFacet2 );
    }


    private String createXml( final FacetsModel facets )
        throws JAXBException
    {
        JAXBContext context = JAXBContext.newInstance( FacetsModel.class );
        Marshaller m = context.createMarshaller();
        m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
        final StringWriter stringWriter = new StringWriter();
        m.marshal( facets, stringWriter );
        return stringWriter.toString();
    }


    @Override
    public boolean handleEvent( final ValidationEvent validationEvent )
    {

        System.out.println( "Validation Event: " + validationEvent.toString() );
        return true;
    }
}
