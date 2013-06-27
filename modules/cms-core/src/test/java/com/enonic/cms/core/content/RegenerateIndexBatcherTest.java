/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.core.content.contenttype.ContentTypeEntity;

import static org.mockito.Mockito.when;


public class RegenerateIndexBatcherTest
{
    private IndexService indexService;

    private ContentService contentService;

    private RegenerateIndexBatcher regenerateIndexBatcher;

    @Before
    public void before()
    {

        contentService = Mockito.mock( ContentService.class );

        indexService = Mockito.mock( IndexService.class );

        regenerateIndexBatcher = new RegenerateIndexBatcher( indexService, contentService );
    }

    @Test
    public void testRegenrateIndexUnequalToBatchSize()
        throws Exception
    {

        ContentTypeEntity cty1 = createContentTypeEntity( 1, "test" );
        List<ContentKey> contentKeysOfCty1 = createContentKeys( 11 );

        when( contentService.findContentKeysByContentType( cty1 ) ).thenReturn( contentKeysOfCty1 );

        indexService.reindex( createContentKeys( new int[]{1, 2, 3, 4} ) );
        indexService.reindex( createContentKeys( new int[]{5, 6, 7, 8} ) );
        indexService.reindex( createContentKeys( new int[]{9, 10, 11} ) );

        regenerateIndexBatcher.regenerateIndex( cty1, 4, null );

    }

    @Test
    public void testRegenrateIndexEqualToBatchSize()
        throws Exception
    {

        ContentTypeEntity cty1 = createContentTypeEntity( 1, "test" );
        List<ContentKey> contentKeysOfCty1 = createContentKeys( 8 );

        when( contentService.findContentKeysByContentType( cty1 ) ).thenReturn( contentKeysOfCty1 );
        indexService.reindex( createContentKeys( new int[]{1, 2, 3, 4} ) );
        indexService.reindex( createContentKeys( new int[]{5, 6, 7, 8} ) );

        regenerateIndexBatcher.regenerateIndex( cty1, 4, null );


    }

    @Test
    public void testRegenrateIndexSameTotalAsBatchSize()
        throws Exception
    {

        ContentTypeEntity cty1 = createContentTypeEntity( 1, "test" );
        List<ContentKey> contentKeysOfCty1 = createContentKeys( 4 );

        when( contentService.findContentKeysByContentType( cty1 ) ).thenReturn( contentKeysOfCty1 );
        indexService.reindex( createContentKeys( new int[]{1, 2, 3, 4} ) );

        regenerateIndexBatcher.regenerateIndex( cty1, 4, null );


    }

    @Test
    public void testRegenrateIndexWithContentKeysSmallerThanBatchSize()
        throws Exception
    {

        ContentTypeEntity cty1 = createContentTypeEntity( 1, "test" );
        List<ContentKey> contentKeysOfCty1 = createContentKeys( 2 );

        when( contentService.findContentKeysByContentType( cty1 ) ).thenReturn( contentKeysOfCty1 );
        indexService.reindex( createContentKeys( new int[]{1, 2} ) );

        regenerateIndexBatcher.regenerateIndex( cty1, 4, null );
    }

    @Test
    public void testRegenrateIndexWithNoneContentKeys()
    {

        ContentTypeEntity cty1 = createContentTypeEntity( 1, "test" );
        List<ContentKey> contentKeysOfCty1 = createContentKeys( 0 );

        when( contentService.findContentKeysByContentType( cty1 ) ).thenReturn( contentKeysOfCty1 );
        //indexService.regenerateIndex(createContentKeys(new int[] {1, 2}));

        regenerateIndexBatcher.regenerateIndex( cty1, 4, null );
    }

    private List<ContentKey> createContentKeys( int[] contentKeys )
    {

        List<ContentKey> keys = new ArrayList<ContentKey>();
        for ( int contentKey : contentKeys )
        {
            keys.add( new ContentKey( contentKey ) );
        }
        return keys;
    }

    private List<ContentKey> createContentKeys( int count )
    {

        List<ContentKey> keys = new ArrayList<ContentKey>();
        for ( int i = 0; i < count; i++ )
        {
            keys.add( new ContentKey( i + 1 ) );
        }
        return keys;
    }

    private ContentTypeEntity createContentTypeEntity( int key, String name )
    {

        ContentTypeEntity contentType = new ContentTypeEntity();
        contentType.setKey( key );
        contentType.setName( name );
        return contentType;
    }

}
