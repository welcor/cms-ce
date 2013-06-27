/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.facet.builder;

import org.elasticsearch.search.facet.terms.TermsFacetBuilder;
import org.junit.Test;

import com.enonic.cms.core.search.facet.AbstractElasticsearchFacetTestBase;
import com.enonic.cms.core.search.facet.FacetQueryException;
import com.enonic.cms.core.search.facet.model.TermsFacetModel;

import static org.junit.Assert.*;

public class ElasticsearchTermsFacetBuilderTest
    extends AbstractElasticsearchFacetTestBase
{

    ElasticsearchTermsFacetBuilder termsFacetBuilder = new ElasticsearchTermsFacetBuilder();

    @Test(expected = FacetQueryException.class)
    public void testEmptyModel()
    {
        TermsFacetModel termFacetModel = new TermsFacetModel();
        termsFacetBuilder.build( termFacetModel );
    }

    @Test
    public void testFullModel()
        throws Exception
    {
        String expected = "{\"myFullModel\":" +
            "{\"terms\":" +
            "{\"fields\":[\"fields1\",\"fields2\"]," +
            "\"size\":10," +
            "\"exclude\":[\"exclude1\",\"exclude2\"]," +
            "\"regex\":\"myRegexp\"," +
            "\"regex_flags\":\"CASE_INSENSITIVE|DOTALL|\"," +
            "\"order\":\"count\"" +
            "}}}";

        TermsFacetModel termFacetModel = new TermsFacetModel();
        termFacetModel.setName( "myFullModel" );
        termFacetModel.setAllTerms( true );
        termFacetModel.setExclude( "exclude1, exclude2" );
        termFacetModel.setIndexes( "field" );
        termFacetModel.setIndexes( "fields1, fields2" );
        termFacetModel.setRegex( "myRegexp" );
        termFacetModel.setRegexFlags( "CASE_INSENSITIVE, DOTALL" );
        termFacetModel.setOrderby( "hits" );

        final TermsFacetBuilder build = termsFacetBuilder.build( termFacetModel );
        assertEquals( expected, getJson( build ) );
    }

    @Test
    public void testReverseOrdering()
        throws Exception
    {
        String expected = "{\"myFullModel\":{\"terms\":{\"fields\":[\"fields1\",\"fields2\"],\"size\":10,\"order\":\"reverse_count\"}}}";

        TermsFacetModel termFacetModel = new TermsFacetModel();
        termFacetModel.setName( "myFullModel" );
        termFacetModel.setIndexes( "fields1, fields2" );
        termFacetModel.setOrderby( "hits ASC" );

        final TermsFacetBuilder build = termsFacetBuilder.build( termFacetModel );
        assertEquals( expected, getJson( build ) );
    }

    @Test
    public void testDescOrdering()
        throws Exception
    {
        String expected = "{\"myFullModel\":{\"terms\":{\"fields\":[\"fields1\",\"fields2\"],\"size\":10,\"order\":\"count\"}}}";

        TermsFacetModel termFacetModel = new TermsFacetModel();
        termFacetModel.setName( "myFullModel" );
        termFacetModel.setIndexes( "fields1, fields2" );
        termFacetModel.setOrderby( "hits DESC" );

        final TermsFacetBuilder build = termsFacetBuilder.build( termFacetModel );
        assertEquals( expected, getJson( build ) );
    }


    @Test
    public void testFieldNameConverting()
        throws Exception
    {
        String expected = "{\"myModel\":{\"terms\":{\"fields\":[\"data_field\",\"data_person_field\"],\"size\":10}}}";

        TermsFacetModel termFacetModel = new TermsFacetModel();
        termFacetModel.setName( "myModel" );
        termFacetModel.setIndexes( "data/field, data.person.field" );

        final TermsFacetBuilder build = termsFacetBuilder.build( termFacetModel );
        assertEquals( expected, getJson( build ) );
    }


}
