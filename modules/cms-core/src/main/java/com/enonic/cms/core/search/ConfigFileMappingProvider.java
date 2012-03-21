package com.enonic.cms.core.search;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.elasticsearch.ElasticSearchException;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/22/11
 * Time: 3:55 PM
 */
@Component("indexMappingProvider")
final class ConfigFileMappingProvider
    implements IndexMappingProvider
{

    public String getMapping( String indexName, IndexType indexType )
    {
        InputStream stream = ConfigFileMappingProvider.class.getResourceAsStream( createMappingFileName( indexName, indexType ) );

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

    private String createMappingFileName( String indexName, IndexType indexType )
    {
        return indexName + "_" + indexType.toString() + "_mapping.json";
    }
}
