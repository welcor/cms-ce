package com.enonic.cms.itest.search;

import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;

public class ContentIndexServiceImpl_queryTextTokensTest
    extends ContentIndexServiceTestBase
{

    @Test
    public void testEmailAddress()
    {
        contentIndexService.index( createContentDocumentWithTextField( 1, "doc1", "doc", "rmy@enonic.com" ), false );
        contentIndexService.index( createContentDocumentWithTextField( 2, "doc2", "doc2", "esu@enonic.com" ), false );
        flushIndex();

        printAllIndexContent();
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
        contentIndexService.index( createContentDocumentWithTextField( 1, "doc1", "doc", "Dette er en tekst med mange ord med mellomrom" ),
                                   false );
        contentIndexService.index( createContentDocumentWithTextField( 2, "doc2", "doc2", "Dette en er med tekst ord mange mellomrom med" ),
                                   false );
        flushIndex();

        printAllIndexContent();
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
        contentIndexService.index( createContentDocumentWithTextField( 1, "doc1", "doc", "Dette er en tekst med mange ord med mellomrom" ),
                                   false );
        contentIndexService.index( createContentDocumentWithTextField( 2, "doc2", "doc2", "Dette en er med tekst ord mange med" ), false );
        flushIndex();

        printAllIndexContent();
        assertContentResultSetEquals( new int[]{1, 2}, contentIndexService.query( new ContentIndexQuery( "data/* CONTAINS 'ord' " ) ) );
        assertContentResultSetEquals( new int[]{1}, contentIndexService.query( new ContentIndexQuery( "data/* CONTAINS 'tekst med' " ) ) );
        assertContentResultSetEquals( new int[]{1}, contentIndexService.query(
            new ContentIndexQuery( "data/* CONTAINS 'tekst med mange ord med mellomrom' " ) ) );
        assertContentResultSetEquals( new int[]{1, 2}, contentIndexService.query( new ContentIndexQuery( "data/* CONTAINS 'tek' " ) ) );
    }

    @Test
    public void testWordsWithHyphens()
    {
        contentIndexService.index( createContentDocumentWithTextField( 1, "doc1", "doc", "ord1-ord2" ), false );
        contentIndexService.index( createContentDocumentWithTextField( 2, "doc2", "doc2", "ord1" ), false );
        flushIndex();

        printAllIndexContent();
        assertContentResultSetEquals( new int[]{1, 2},
                                      contentIndexService.query( new ContentIndexQuery( "data/textfield CONTAINS 'ord1' " ) ) );
        assertContentResultSetEquals( new int[]{1},
                                      contentIndexService.query( new ContentIndexQuery( "data/textfield CONTAINS 'ord1-ord2' " ) ) );
        assertContentResultSetEquals( new int[]{1}, contentIndexService.query( new ContentIndexQuery( "data/* CONTAINS 'ord1-ord2' " ) ) );
    }

    @Test
    public void testWordsWithCommas()
    {
        contentIndexService.index( createContentDocumentWithTextField( 1, "doc1", "doc", "ord1,ord2,ord3" ), false );
        contentIndexService.index( createContentDocumentWithTextField( 2, "doc2", "doc2", "ord1" ), false );
        flushIndex();

        printAllIndexContent();
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
