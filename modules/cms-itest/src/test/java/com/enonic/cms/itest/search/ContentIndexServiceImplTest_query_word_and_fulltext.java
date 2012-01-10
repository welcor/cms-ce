package com.enonic.cms.itest.search;

import org.junit.Ignore;
import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 1/3/12
 * Time: 1:02 PM
 */
public class ContentIndexServiceImplTest_query_word_and_fulltext
    extends ContentIndexServiceTestBase
{
    @Test
    public void testOneWordSearchOnTitleAndData()
    {
        service.index( createContentDocument( 123, "ost", "ost", null ), false );
        service.index( createContentDocument( 124, "ost", "kake", null ), false );
        service.index( createContentDocument( 125, "kake", "ost", null ), false );
        service.index( createContentDocument( 126, "kake", "kake", null ), false );
        letTheIndexFinishItsWork();
        //printAllIndexContent();

        assertContentResultSetEquals( new int[]{123},
                                      service.query( new ContentIndexQuery( "title CONTAINS 'ost' AND data/* CONTAINS 'ost'", 10 ) ) );

        assertContentResultSetEquals( new int[]{123, 124, 125},
                                      service.query( new ContentIndexQuery( "title CONTAINS 'ost' OR data/* CONTAINS 'ost'", 10 ) ) );

        assertContentResultSetEquals( new int[]{124, 125, 126},
                                      service.query( new ContentIndexQuery( "title CONTAINS 'kake' OR data/* CONTAINS 'kake'", 10 ) ) );

        assertContentResultSetEquals( new int[]{},
                                      service.query( new ContentIndexQuery( "title CONTAINS 'fisk' OR data/* CONTAINS 'fisk'", 10 ) ) );
    }

    @Test
    public void testOneWordSearchOnTitleAndFulltext()
    {
        service.index( createContentDocument( 123, "ost", null, "ost" ), false );
        service.index( createContentDocument( 124, "ost", null, "kake" ), false );
        service.index( createContentDocument( 125, "kake", null, "ost" ), false );
        service.index( createContentDocument( 126, "kake", null, "kake" ), false );
        letTheIndexFinishItsWork();

        assertContentResultSetEquals( new int[]{123},
                                      service.query( new ContentIndexQuery( "title CONTAINS 'ost' AND fulltext CONTAINS 'ost'", 10 ) ) );

        assertContentResultSetEquals( new int[]{123, 124, 125},
                                      service.query( new ContentIndexQuery( "title CONTAINS 'ost' OR fulltext CONTAINS 'ost'", 10 ) ) );

        assertContentResultSetEquals( new int[]{124, 125, 126},
                                      service.query( new ContentIndexQuery( "title CONTAINS 'kake' OR fulltext CONTAINS 'kake'", 10 ) ) );

        assertContentResultSetEquals( new int[]{},
                                      service.query( new ContentIndexQuery( "title CONTAINS 'fisk' OR fulltext CONTAINS 'fisk'", 10 ) ) );
    }


    @Test
    public void testOneWordSearchOnTitleAndUnknown()
    {
        service.index( createContentDocument( 123, "ost", null, null ), false );
        service.index( createContentDocument( 124, "kake", null, null ), false );
        letTheIndexFinishItsWork();

        assertContentResultSetEquals( new int[]{},
                                      service.query( new ContentIndexQuery( "title CONTAINS 'ost' AND unknown CONTAINS 'ost'", 10 ) ) );

        assertContentResultSetEquals( new int[]{123},
                                      service.query( new ContentIndexQuery( "title CONTAINS 'ost' OR unknown CONTAINS 'ost'", 10 ) ) );

        assertContentResultSetEquals( new int[]{124},
                                      service.query( new ContentIndexQuery( "unknown CONTAINS 'kake' OR title CONTAINS 'kake'", 10 ) ) );

        assertContentResultSetEquals( new int[]{},
                                      service.query( new ContentIndexQuery( "title CONTAINS 'fisk' OR unknown CONTAINS 'fisk'", 10 ) ) );
    }

    @Ignore // Decide how to handle fulltext
    @Test
    public void testOneWordSearchOnTitleAndDataAndFulltext()
    {
        service.index( createContentDocument( 121, "ost", "ost", "ost" ), false );
        service.index( createContentDocument( 122, "kake", "ost", "ost" ), false );
        service.index( createContentDocument( 123, "ost", "kake", "ost" ), false );
        service.index( createContentDocument( 124, "ost", "ost", "kake" ), false );
        service.index( createContentDocument( 125, "kake", "kake", "ost" ), false );
        service.index( createContentDocument( 126, "kake", "ost", "kake" ), false );
        service.index( createContentDocument( 127, "ost", "kake", "kake" ), false );
        service.index( createContentDocument( 128, "kake", "kake", "kake" ), false );
        letTheIndexFinishItsWork();

        assertContentResultSetEquals( new int[]{121}, service.query(
            new ContentIndexQuery( "title CONTAINS 'ost' AND data/* CONTAINS 'ost' AND fulltext CONTAINS 'ost'", 10 ) ) );

        assertContentResultSetEquals( new int[]{121, 122, 123, 124, 125, 126, 127}, service.query(
            new ContentIndexQuery( "title CONTAINS 'ost' OR data/* CONTAINS 'ost' OR fulltext CONTAINS 'ost'", 10 ) ) );

        assertContentResultSetEquals( new int[]{121, 122, 123, 124, 127}, service.query(
            new ContentIndexQuery( "title CONTAINS 'ost' OR (data/* CONTAINS 'ost' AND fulltext CONTAINS 'ost')", 10 ) ) );

        assertContentResultSetEquals( new int[]{121, 122, 123, 124, 126}, service.query(
            new ContentIndexQuery( "data/* CONTAINS 'ost' OR (title CONTAINS 'ost' AND fulltext CONTAINS 'ost')", 10 ) ) );

        assertContentResultSetEquals( new int[]{121, 122, 123, 124, 125}, service.query(
            new ContentIndexQuery( "fulltext CONTAINS 'ost' OR (title CONTAINS 'ost' AND data/* CONTAINS 'ost')", 10 ) ) );

        assertContentResultSetEquals( new int[]{121, 123, 124}, service.query(
            new ContentIndexQuery( "title CONTAINS 'ost' AND (data/* CONTAINS 'ost' OR fulltext CONTAINS 'ost')", 10 ) ) );

        assertContentResultSetEquals( new int[]{121, 122, 124}, service.query(
            new ContentIndexQuery( "data/* CONTAINS 'ost' AND (title CONTAINS 'ost' OR fulltext CONTAINS 'ost')", 10 ) ) );

        assertContentResultSetEquals( new int[]{121, 122, 123}, service.query(
            new ContentIndexQuery( "fulltext CONTAINS 'ost' AND (title CONTAINS 'ost' OR data/* CONTAINS 'ost')", 10 ) ) );
    }

    @Test
    public void testOneWordSearchOnTitleAndDataAndUnknown()
    {
        service.index( createContentDocument( 123, "ost", "ost", null ), false );
        service.index( createContentDocument( 124, "ost", "kake", null ), false );
        service.index( createContentDocument( 125, "kake", "ost", null ), false );
        service.index( createContentDocument( 126, "kake", "kake", null ), false );
        letTheIndexFinishItsWork();

        assertContentResultSetEquals( new int[]{123}, service.query(
            new ContentIndexQuery( "(title CONTAINS 'ost' AND data/* CONTAINS 'ost') OR unknown CONTAINS 'fisk'", 10 ) ) );

        assertContentResultSetEquals( new int[]{123, 124, 125}, service.query(
            new ContentIndexQuery( "title CONTAINS 'ost' OR data/* CONTAINS 'ost' OR unknown CONTAINS 'fisk'", 10 ) ) );

        assertContentResultSetEquals( new int[]{}, service.query(
            new ContentIndexQuery( "(title CONTAINS 'ost' OR data/* CONTAINS 'ost') AND unknown CONTAINS 'fisk'", 10 ) ) );

        assertContentResultSetEquals( new int[]{124, 125, 126}, service.query(
            new ContentIndexQuery( "title CONTAINS 'kake' OR data/* CONTAINS 'kake' OR unknown CONTAINS 'fisk'", 10 ) ) );

        assertContentResultSetEquals( new int[]{}, service.query(
            new ContentIndexQuery( "title CONTAINS 'fisk' OR data/* CONTAINS 'fisk' OR unknown CONTAINS 'fisk'", 10 ) ) );
    }


    @Test
    public void testOneWordSearchOnTitleAndFulltextAndUnknown()
    {
        service.index( createContentDocument( 123, "ost", null, "ost" ), false );
        service.index( createContentDocument( 124, "ost", null, "kake" ), false );
        service.index( createContentDocument( 125, "kake", null, "ost" ), false );
        service.index( createContentDocument( 126, "kake", null, "kake" ), false );
        letTheIndexFinishItsWork();

        assertContentResultSetEquals( new int[]{123}, service.query(
            new ContentIndexQuery( "(title CONTAINS 'ost' AND fulltext CONTAINS 'ost') OR unknown CONTAINS 'fisk'", 10 ) ) );

        assertContentResultSetEquals( new int[]{123, 124, 125}, service.query(
            new ContentIndexQuery( "title CONTAINS 'ost' OR fulltext CONTAINS 'ost' OR unknown CONTAINS 'fisk'", 10 ) ) );

        assertContentResultSetEquals( new int[]{}, service.query(
            new ContentIndexQuery( "title CONTAINS 'ost' AND fulltext CONTAINS 'ost' AND unknown CONTAINS 'fisk'", 10 ) ) );

        assertContentResultSetEquals( new int[]{124, 125, 126}, service.query(
            new ContentIndexQuery( "title CONTAINS 'kake' OR fulltext CONTAINS 'kake' OR unknown CONTAINS 'fisk'", 10 ) ) );

        assertContentResultSetEquals( new int[]{}, service.query(
            new ContentIndexQuery( "title CONTAINS 'fisk' OR fulltext CONTAINS 'fisk' OR unknown CONTAINS 'fisk'", 10 ) ) );
    }


    @Ignore // Decide how to handle fulltext
    @Test
    public void testOneWordSearchOnTitleAndDataAndFulltextAndUnknown()
    {
        service.index( createContentDocument( 121, "ost", "ost", "ost" ), false );
        service.index( createContentDocument( 122, "kake", "ost", "ost" ), false );
        service.index( createContentDocument( 123, "ost", "kake", "ost" ), false );
        service.index( createContentDocument( 124, "ost", "ost", "kake" ), false );
        service.index( createContentDocument( 125, "kake", "kake", "ost" ), false );
        service.index( createContentDocument( 126, "kake", "ost", "kake" ), false );
        service.index( createContentDocument( 127, "ost", "kake", "kake" ), false );
        service.index( createContentDocument( 128, "kake", "kake", "kake" ), false );
        letTheIndexFinishItsWork();

        assertContentResultSetEquals( new int[]{121}, service.query( new ContentIndexQuery(
            "(title CONTAINS 'ost' AND data/* CONTAINS 'ost' AND fulltext CONTAINS 'ost') OR unknown CONTAINS 'fisk'", 10 ) ) );

        assertContentResultSetEquals( new int[]{}, service.query( new ContentIndexQuery(
            "(title CONTAINS 'ost' AND data/* CONTAINS 'ost' AND fulltext CONTAINS 'ost') AND unknown CONTAINS 'fisk'", 10 ) ) );

        assertContentResultSetEquals( new int[]{121, 122, 123, 124, 125, 126, 127}, service.query(
            new ContentIndexQuery( "(title CONTAINS 'ost' OR data/* CONTAINS 'ost' OR fulltext CONTAINS 'ost') OR unknown CONTAINS 'fisk'",
                                   10 ) ) );

        assertContentResultSetEquals( new int[]{}, service.query(
            new ContentIndexQuery( "(title CONTAINS 'ost' OR data/* CONTAINS 'ost' OR fulltext CONTAINS 'ost') AND unknown CONTAINS 'fisk'",
                                   10 ) ) );

        assertContentResultSetEquals( new int[]{121, 122, 123, 124, 127}, service.query( new ContentIndexQuery(
            "(title CONTAINS 'ost' OR (data/* CONTAINS 'ost' AND fulltext CONTAINS 'ost')) OR unknown CONTAINS 'fisk'", 10 ) ) );

        assertContentResultSetEquals( new int[]{}, service.query( new ContentIndexQuery(
            "(title CONTAINS 'ost' OR (data/* CONTAINS 'ost' AND fulltext CONTAINS 'ost')) AND unknown CONTAINS 'fisk'", 10 ) ) );

        assertContentResultSetEquals( new int[]{121, 122, 123, 124, 126}, service.query( new ContentIndexQuery(
            "(data/* CONTAINS 'ost' OR (title CONTAINS 'ost' AND fulltext CONTAINS 'ost')) OR unknown CONTAINS 'fisk'", 10 ) ) );

        assertContentResultSetEquals( new int[]{}, service.query( new ContentIndexQuery(
            "(data/* CONTAINS 'ost' OR (title CONTAINS 'ost' AND fulltext CONTAINS 'ost')) AND unknown CONTAINS 'fisk'", 10 ) ) );

        assertContentResultSetEquals( new int[]{121, 122, 123, 124, 125}, service.query( new ContentIndexQuery(
            "(fulltext CONTAINS 'ost' OR (title CONTAINS 'ost' AND data/* CONTAINS 'ost')) OR unknown CONTAINS 'fisk'", 10 ) ) );

        assertContentResultSetEquals( new int[]{}, service.query( new ContentIndexQuery(
            "(fulltext CONTAINS 'ost' OR (title CONTAINS 'ost' AND data/* CONTAINS 'ost')) AND unknown CONTAINS 'fisk'", 10 ) ) );

        assertContentResultSetEquals( new int[]{121, 123, 124}, service.query( new ContentIndexQuery(
            "(title CONTAINS 'ost' AND (data/* CONTAINS 'ost' OR fulltext CONTAINS 'ost')) OR unknown CONTAINS 'fisk'", 10 ) ) );

        assertContentResultSetEquals( new int[]{}, service.query( new ContentIndexQuery(
            "(title CONTAINS 'ost' AND (data/* CONTAINS 'ost' OR fulltext CONTAINS 'ost')) AND unknown CONTAINS 'fisk'", 10 ) ) );

        assertContentResultSetEquals( new int[]{121, 122, 124}, service.query( new ContentIndexQuery(
            "(data/* CONTAINS 'ost' AND (title CONTAINS 'ost' OR fulltext CONTAINS 'ost')) OR unknown CONTAINS 'fisk'", 10 ) ) );

        assertContentResultSetEquals( new int[]{}, service.query( new ContentIndexQuery(
            "(data/* CONTAINS 'ost' AND (title CONTAINS 'ost' OR fulltext CONTAINS 'ost')) AND unknown CONTAINS 'fisk'", 10 ) ) );

        assertContentResultSetEquals( new int[]{121, 122, 123}, service.query( new ContentIndexQuery(
            "(fulltext CONTAINS 'ost' AND (title CONTAINS 'ost' OR data/* CONTAINS 'ost')) OR unknown CONTAINS 'fisk'", 10 ) ) );

        assertContentResultSetEquals( new int[]{}, service.query( new ContentIndexQuery(
            "(fulltext CONTAINS 'ost' AND (title CONTAINS 'ost' OR data/* CONTAINS 'ost')) AND unknown CONTAINS 'fisk'", 10 ) ) );
    }

}
