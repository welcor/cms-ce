/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.itest.search;

import org.junit.Test;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.BigText;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.search.query.ContentDocument;

import static org.junit.Assert.*;

public class ContentIndexServiceImpl_queryAttachmentsTest
    extends ContentIndexServiceTestBase
{


    @Test
    public void testIndexAndQueryBinaryExtractedText()
    {
        ContentDocument contentDocument = new ContentDocument( new ContentKey( 1 ) );
        contentDocument.setCategoryKey( new CategoryKey( 9 ) );
        contentDocument.setContentTypeKey( new ContentTypeKey( 32 ) );
        contentDocument.setContentTypeName( "Article" );
        contentDocument.setTitle( "title" );
        contentDocument.setStatus( 2 );
        contentDocument.setPriority( 0 );
        contentDocument.setBinaryExtractedText( new BigText( "This is a BigText" ) );

        contentIndexService.index( contentDocument );

        flushIndex();

        assertContentResultSetEquals( new int[]{1}, contentIndexService.query(
            new ContentIndexQuery( "categorykey = 9 and attachment/* = 'bigtext'" ) ) );

        final ContentResultSet resultSet = contentIndexService.query( new ContentIndexQuery( "categorykey = 9 and data/* = 'bigtext'" ) );
        assertTrue( resultSet.getKeys().isEmpty() );

    }


}
