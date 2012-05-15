package com.enonic.cms.core.search;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.elasticsearch.index.get.GetField;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.cms.core.content.ContentIndexEntity;
import com.enonic.cms.core.content.ContentKey;

import static org.junit.Assert.*;

public class ElasticSearchIndexedFieldsTranslatorTest
{

    @Test
    public void testIndexedFieldsTranslation()
            throws IOException, JSONException
    {
        final JSONObject jsonDoc = readDocumentAsJson( "content_index.json" );
        final JSONObject sourceJson = jsonDoc.getJSONObject( "_source" );
        assertNotNull( "Missing source in document", sourceJson );

        final Map<String, GetField> fields = new HashMap<String, GetField>();
        addFields( fields, sourceJson );

        final ContentKey contentKey = new ContentKey( 42 );
        final ElasticSearchIndexedFieldsTranslator translator = new ElasticSearchIndexedFieldsTranslator();
        final List<ContentIndexEntity> indexValues = translator.generateContentIndexFieldSet( contentKey, fields );

        assertField( indexValues, "contenttype", "destination" );
        assertField( indexValues, "categorykey", "35" );
        assertField( indexValues, "title", "the blue lagoon, iceland" );
        assertField( indexValues, "modified", "2012-04-19t14:30:40" );
        assertField( indexValues, "timestamp", "2012-04-19t14:30:40" );
        assertField( indexValues, "data#heading", "the blue lagoon, iceland" );
        assertField( indexValues, "modifier#qualifiedname", "admin" );
        assertField( indexValues, "publishfrom", "2012-04-19t14:30:00" );
        assertField( indexValues, "contenttypekey", "1007" );
        assertField( indexValues, "priority", "0" );
        assertField( indexValues, "owner#qualifiedname", "admin" );
    }

    private void assertField( List<ContentIndexEntity> indexValues, String name, String expectedValue )
    {
        for ( ContentIndexEntity indexValue : indexValues )
        {
            if ( indexValue.getPath().equals( name ) )
            {
                assertEquals( "Index value does not match", expectedValue, indexValue.getValue() );
                return;
            }
        }
        assertFalse( "Index value not found: " + name, true );
    }

    private void addFields( Map<String, GetField> fields, JSONObject sourceJson )
            throws JSONException
    {
        final Iterator keyIterator = sourceJson.keys();
        while ( keyIterator.hasNext() )
        {
            final String key = (String) keyIterator.next();
            final Object value = sourceJson.get( key );
            if ( value instanceof JSONArray )
            {
                final JSONArray array = (JSONArray) value;
                final List<Object> values = new ArrayList<Object>();
                for ( int i = 0; i < array.length(); i++ )
                {
                    values.add( array.get( i ) );
                }
                fields.put( key, new GetField( key, values ) );
            }
            else
            {
                fields.put( key, new GetField( key, Lists.newArrayList( value ) ) );
            }
        }
    }

    private JSONObject readDocumentAsJson( final String fileName )
            throws IOException, JSONException
    {
        final InputStream stream = ElasticSearchIndexedFieldsTranslatorTest.class.getResourceAsStream( fileName );
        final StringWriter writer = new StringWriter();
        IOUtils.copy( stream, writer, "UTF-8" );
        final String content = writer.toString();
        return new JSONObject( content );
    }

}
