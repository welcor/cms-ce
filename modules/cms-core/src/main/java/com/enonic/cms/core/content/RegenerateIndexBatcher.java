/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;

import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.search.IndexException;

public class RegenerateIndexBatcher
{

    private static final Logger LOG = LoggerFactory.getLogger( RegenerateIndexBatcher.class );

    private static final int DEFAULT_RETRIES = 3;

    private final IndexService indexService;

    private final ContentService contentService;

    private int maxRetries = DEFAULT_RETRIES;

    public RegenerateIndexBatcher( IndexService indexService, ContentService contentService )
    {
        this.indexService = indexService;
        this.contentService = contentService;
    }

    public void regenerateIndex( ContentTypeEntity contentType, int batchSize, List<String> logEntries )
    {

        if ( contentType == null )
        {
            throw new IllegalArgumentException( "Given contentType cannot be null" );
        }
        if ( batchSize <= 0 )
        {
            throw new IllegalArgumentException( "Given batchSize must be larger that zero" );
        }

        List<ContentKey> allContentKeys = contentService.findContentKeysByContentType( contentType );

        int currentIndex = 0;

        while ( currentIndex < allContentKeys.size() )
        {
            List<ContentKey> nextContentKeys = getNextContentKeys( allContentKeys, currentIndex, batchSize );

            if ( nextContentKeys != null && nextContentKeys.size() > 0 )
            {

                if ( logEntries != null )
                {
                    logEntries.add(
                        "Regenerating indexes, (batch: " + ( currentIndex + 1 ) + " -> " + ( currentIndex + nextContentKeys.size() ) +
                            " of total " + allContentKeys.size() + ") of content type '" + contentType.getName() + "'" );
                }

                LOG.info( "Regenerating indexes, (batch: " + ( currentIndex + 1 ) + " -> " + ( currentIndex + nextContentKeys.size() ) +
                              " of total " + allContentKeys.size() + ") of content type '" + contentType.getName() + "'" );

                long start = System.currentTimeMillis();

                int retry = 0;
                boolean indexSuccess = false;
                do
                {
                    try
                    {
                        indexService.reindex( nextContentKeys );
                        indexSuccess = true;
                    }
                    catch ( IndexException e )
                    {
                        retry++;
                        if ( retry > this.maxRetries )
                        {
                            throw e;
                        }
                        else
                        {
                            LOG.warn( "Unexpected error indexing batch with keys: " + Iterables.toString( nextContentKeys ), e );
                            LOG.warn( "Retrying (" + retry + ") ..." );
                        }
                    }
                }
                while ( !indexSuccess );
                //indexService.regenerateIndexBatched( nextContentKeys );

                long end = System.currentTimeMillis();

                LOG.info( "Last batch took: " + ( ( end - start ) / 1000 ) + " sec" );

                currentIndex = currentIndex + batchSize;
            }
        }

    }

    private List<ContentKey> getNextContentKeys( List<ContentKey> allContentKeys, int currentIndex, int batchSize )
    {

        if ( currentIndex + batchSize > allContentKeys.size() )
        {
            return allContentKeys.subList( currentIndex, allContentKeys.size() );
        }

        return allContentKeys.subList( currentIndex, currentIndex + batchSize );
    }

    public void setMaxRetries( final int maxRetries )
    {
        this.maxRetries = Math.max( maxRetries, 0 );
    }
}
