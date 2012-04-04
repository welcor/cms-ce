package com.enonic.cms.itest.search;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.elasticsearch.ElasticSearchException;

import com.enonic.cms.core.search.IndexMappingProvider;

public class TestIndexMappingProvider
    implements IndexMappingProvider
{

    @Override
    public String getMapping( final String indexName, final String indexType )
    {
        InputStream stream = TestIndexMappingProvider.class.getResourceAsStream( createMappingFileName( indexName, indexType ) );

        if ( stream == null )
        {
            throw new ElasticSearchException( "Mapping-file not found: " + createMappingFileName( indexName, indexType ) );
        }

        StringWriter writer = new StringWriter();
        try
        {

            IOUtils.copy( stream, writer, "UTF-8" );
            final String mapping = writer.toString();

            //System.out.println( "Mapping file loaded: " + mapping );

            return mapping;
        }
        catch ( IOException e )
        {
            throw new ElasticSearchException( "Failed to get mapping-file as stream", e );
        }
    }

    private String createMappingFileName( String indexName, String indexType )
    {
        return indexName + "_" + indexType.toString() + "_mapping.json";
    }

}
