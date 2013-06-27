/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.content;

import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.cms.core.AbstractEqualsTest;


public class ContentMapEqualsTest
    extends AbstractEqualsTest
{
    private static final ContentKey CONTENT_KEY_1 = new ContentKey( 1 );

    private static final ContentKey CONTENT_KEY_2 = new ContentKey( 2 );

    private static final ContentKey CONTENT_KEY_3 = new ContentKey( 3 );

    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    @Override
    public Object getObjectX()
    {
        return createContentMap( CONTENT_KEY_1, CONTENT_KEY_2, CONTENT_KEY_3 );
    }

    @Override
    public Object[] getObjectsThatNotEqualsX()
    {
        return new Object[]{createContentMap( CONTENT_KEY_3, CONTENT_KEY_2, CONTENT_KEY_1 )};
    }

    @Override
    public Object getObjectThatEqualsXButNotTheSame()
    {
        return createContentMap( CONTENT_KEY_1, CONTENT_KEY_2, CONTENT_KEY_3 );
    }

    @Override
    public Object getObjectThatEqualsXButNotTheSame2()
    {
        return createContentMap( CONTENT_KEY_1, CONTENT_KEY_2, CONTENT_KEY_3 );
    }

    private static ContentMap createContentMap( ContentKey... keys )
    {
        ContentMap contentMap = new ContentMap( Lists.newArrayList( keys ) );
        for ( ContentKey contentKey : keys )
        {
            contentMap.add( createContent( contentKey ) );
        }
        return contentMap;
    }

    private static ContentEntity createContent( final ContentKey contentKey )
    {
        ContentEntity c = new ContentEntity();
        c.setKey( contentKey );
        return c;
    }
}
