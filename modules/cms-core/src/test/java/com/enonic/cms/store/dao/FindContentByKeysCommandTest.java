package com.enonic.cms.store.dao;


import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.google.common.collect.Lists;

import com.enonic.cms.framework.cache.CacheFacade;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;

import static org.junit.Assert.*;

public class FindContentByKeysCommandTest
{
    private static ContentKey CONTENT_KEY_1 = new ContentKey( 1 );

    private static ContentKey CONTENT_KEY_2 = new ContentKey( 2 );

    private static ContentKey CONTENT_KEY_3 = new ContentKey( 3 );

    private static ContentKey CONTENT_KEY_4 = new ContentKey( 4 );

    private CacheFacade entityCache;

    private HibernateTemplate hibernateTemplate;

    private FindContentByKeysQuerier findContentByKeysQuerier;

    @Before
    public void before()
    {
        entityCache = Mockito.mock( CacheFacade.class );
        hibernateTemplate = Mockito.mock( HibernateTemplate.class );
        findContentByKeysQuerier = Mockito.mock( FindContentByKeysQuerier.class );
    }

    @Test
    public void given_content_keys_and_all_content_exists_in_cache_when_execute_then_contents_are_returned_in_same_order_as_keys_where_given()
    {
        // setup
        Mockito.when( entityCache.get( Mockito.anyString(), Mockito.anyString() ) ).thenReturn( new Object() );
        Mockito.when( hibernateTemplate.get( ContentEntity.class, CONTENT_KEY_1 ) ).thenReturn( createContent( CONTENT_KEY_1 ) );
        Mockito.when( hibernateTemplate.get( ContentEntity.class, CONTENT_KEY_2 ) ).thenReturn( createContent( CONTENT_KEY_2 ) );
        Mockito.when( hibernateTemplate.get( ContentEntity.class, CONTENT_KEY_3 ) ).thenReturn( createContent( CONTENT_KEY_3 ) );

        // exercise
        FindContentByKeysCommand command = new FindContentByKeysCommand( entityCache, hibernateTemplate, findContentByKeysQuerier );
        List<ContentEntity> result = command.execute( Lists.newArrayList( CONTENT_KEY_1, CONTENT_KEY_2, CONTENT_KEY_3 ) );

        // verify
        List<ContentEntity> expected =
            Lists.newArrayList( createContent( CONTENT_KEY_1 ), createContent( CONTENT_KEY_2 ), createContent( CONTENT_KEY_3 ) );
        assertEquals( expected, result );
    }

    @Test
    public void given_content_keys_and_some_content_exists_in_cache_when_execute_then_contents_are_returned_in_same_order_as_keys_where_given()
    {
        // setup: content 1 and 3 are to be found in cache
        Mockito.when( entityCache.get( ContentEntity.class.getName(), ContentEntity.class.getName() + "#" + CONTENT_KEY_1 ) ).thenReturn(
            new Object() );
        Mockito.when( entityCache.get( ContentEntity.class.getName(), ContentEntity.class.getName() + "#" + CONTENT_KEY_3 ) ).thenReturn(
            new Object() );

        Mockito.when( hibernateTemplate.get( ContentEntity.class, CONTENT_KEY_1 ) ).thenReturn( createContent( CONTENT_KEY_1 ) );
        Mockito.when( hibernateTemplate.get( ContentEntity.class, CONTENT_KEY_3 ) ).thenReturn( createContent( CONTENT_KEY_3 ) );
        Mockito.when( findContentByKeysQuerier.queryContent( Lists.newArrayList( CONTENT_KEY_2, CONTENT_KEY_4 ) ) ).thenReturn(
            Lists.newArrayList( createContent( CONTENT_KEY_2 ), createContent( CONTENT_KEY_4 ) ) );

        // exercise
        FindContentByKeysCommand command = new FindContentByKeysCommand( entityCache, hibernateTemplate, findContentByKeysQuerier );
        List<ContentEntity> result = command.execute( Lists.newArrayList( CONTENT_KEY_1, CONTENT_KEY_2, CONTENT_KEY_3, CONTENT_KEY_4 ) );

        // verify
        List<ContentEntity> expected =
            Lists.newArrayList( createContent( CONTENT_KEY_1 ), createContent( CONTENT_KEY_2 ), createContent( CONTENT_KEY_3 ),
                                createContent( CONTENT_KEY_4 ) );
        assertEquals( expected, result );
    }

    private ContentEntity createContent( ContentKey key )
    {
        ContentEntity content = new ContentEntity();
        content.setKey( key );
        return content;
    }
}
