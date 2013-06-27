/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.itest.search;

import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;

public class ContentIndexServiceImpl_queryTextTokensTest
    extends ContentIndexServiceTestBase
{

    @Test
    public void testEmailAddress()
    {
        contentIndexService.index( createContentDocumentWithTextField( 1, "doc1", "doc", "rmy@enonic.com" ));
        contentIndexService.index( createContentDocumentWithTextField( 2, "doc2", "doc2", "esu@enonic.com" ));
        flushIndex();

        assertContentResultSetEquals( new int[]{1},
                                      contentIndexService.query( new ContentIndexQuery( "data/textfield CONTAINS 'rmy' " ) ) );
        assertContentResultSetEquals( new int[]{1},
                                      contentIndexService.query( new ContentIndexQuery( "data/textfield = 'rmy@enonic.com' " ) ) );
        assertContentResultSetEquals( new int[]{1, 2},
                                      contentIndexService.query( new ContentIndexQuery( "data/textfield CONTAINS '@enonic' " ) ) );
        assertContentResultSetEquals( new int[]{1, 2},
                                      contentIndexService.query( new ContentIndexQuery( "data/* CONTAINS '@enonic.com' " ) ) );
        assertContentResultSetEquals( new int[]{1}, contentIndexService.query( new ContentIndexQuery( "data/* = 'rmy@enonic.com' " ) ) );
    }

    @Test
    public void testTextWithSpaces_named_fields()
    {
        contentIndexService.index( createContentDocumentWithTextField( 1, "doc1", "doc", "Dette er en tekst med mange ord med mellomrom" )
                                   );
        contentIndexService.index( createContentDocumentWithTextField( 2, "doc2", "doc2", "Dette en er med tekst ord mange mellomrom med" )
                                   );
        flushIndex();

        assertContentResultSetEquals( new int[]{1, 2},
                                      contentIndexService.query( new ContentIndexQuery( "data/textfield CONTAINS 'ord' " ) ) );
        assertContentResultSetEquals( new int[]{1},
                                      contentIndexService.query( new ContentIndexQuery( "data/textfield CONTAINS 'tekst med' " ) ) );
        assertContentResultSetEquals( new int[]{2},
                                      contentIndexService.query( new ContentIndexQuery( "data/textfield CONTAINS 'er med tekst' " ) ) );
        assertContentResultSetEquals( new int[]{1, 2},
                                      contentIndexService.query( new ContentIndexQuery( "data/textfield CONTAINS 'tek' " ) ) );
    }

    @Test
    public void testTextWithSpaces_all_fields()
    {
        contentIndexService.index( createContentDocumentWithTextField( 1, "doc1", "doc", "Dette er en tekst med mange ord med mellomrom" ));
        contentIndexService.index( createContentDocumentWithTextField( 2, "doc2", "doc2", "Dette en er med tekst ord mange med" ));
        flushIndex();

        assertContentResultSetEquals( new int[]{1, 2}, contentIndexService.query( new ContentIndexQuery( "data/* CONTAINS 'ord' " ) ) );
        assertContentResultSetEquals( new int[]{1}, contentIndexService.query( new ContentIndexQuery( "data/* CONTAINS 'tekst med' " ) ) );
        assertContentResultSetEquals( new int[]{1}, contentIndexService.query(
            new ContentIndexQuery( "data/* CONTAINS 'tekst med mange ord med mellomrom' " ) ) );
        assertContentResultSetEquals( new int[]{1, 2}, contentIndexService.query( new ContentIndexQuery( "data/* CONTAINS 'tek' " ) ) );
    }

    @Test
    public void testWordsWithHyphens()
    {
        contentIndexService.index( createContentDocumentWithTextField( 1, "doc1", "doc", "ord1-ord2" ));
        contentIndexService.index( createContentDocumentWithTextField( 2, "doc2", "doc2", "ord1" ));
        flushIndex();

        assertContentResultSetEquals( new int[]{1, 2},
                                      contentIndexService.query( new ContentIndexQuery( "data/textfield CONTAINS 'ord1' " ) ) );
        assertContentResultSetEquals( new int[]{1},
                                      contentIndexService.query( new ContentIndexQuery( "data/textfield CONTAINS 'ord1-ord2' " ) ) );
        assertContentResultSetEquals( new int[]{1}, contentIndexService.query( new ContentIndexQuery( "data/* CONTAINS 'ord1-ord2' " ) ) );
    }

    @Test
    public void testWordsWithCommas()
    {
        contentIndexService.index( createContentDocumentWithTextField( 1, "doc1", "doc", "ord1,ord2,ord3" ));
        contentIndexService.index( createContentDocumentWithTextField( 2, "doc2", "doc2", "ord1" ));
        flushIndex();

        assertContentResultSetEquals( new int[]{1, 2},
                                      contentIndexService.query( new ContentIndexQuery( "data/textfield CONTAINS 'ord1' " ) ) );
        assertContentResultSetEquals( new int[]{1},
                                      contentIndexService.query( new ContentIndexQuery( "data/textfield CONTAINS 'ord2' " ) ) );
        assertContentResultSetEquals( new int[]{1},
                                      contentIndexService.query( new ContentIndexQuery( "data/textfield CONTAINS 'ord3' " ) ) );
        assertContentResultSetEquals( new int[]{1},
                                      contentIndexService.query( new ContentIndexQuery( "data/textfield CONTAINS 'ord1,ord2' " ) ) );
        assertContentResultSetEquals( new int[]{1},
                                      contentIndexService.query( new ContentIndexQuery( "data/* CONTAINS 'ord1,ord2,ord3' " ) ) );

        assertContentResultSetEquals( new int[]{1, 2}, contentIndexService.query( new ContentIndexQuery( "data/* CONTAINS 'ord1' " ) ) );

    }


}
