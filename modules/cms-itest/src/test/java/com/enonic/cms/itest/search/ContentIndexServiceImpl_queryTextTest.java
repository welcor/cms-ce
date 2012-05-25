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
public class ContentIndexServiceImpl_queryTextTest
    extends ContentIndexServiceTestBase
{
    @Test
    public void testOneWordSearchOnTitleAndData()
    {
        contentIndexService.index( createContentDocument( 123, "ost", "ost", null ), false );
        contentIndexService.index( createContentDocument( 124, "ost", "kake", null ), false );
        contentIndexService.index( createContentDocument( 125, "kake", "ost", null ), false );
        contentIndexService.index( createContentDocument( 126, "kake", "kake", null ), false );
        flushIndex();
        //printAllIndexContent();

        assertContentResultSetEquals( new int[]{123}, contentIndexService.query(
            new ContentIndexQuery( "title CONTAINS 'ost' AND data/* CONTAINS 'ost'" ) ) );

        assertContentResultSetEquals( new int[]{123, 124, 125}, contentIndexService.query(
            new ContentIndexQuery( "title CONTAINS 'ost' OR data/* CONTAINS 'ost'" ) ) );

        assertContentResultSetEquals( new int[]{124, 125, 126}, contentIndexService.query(
            new ContentIndexQuery( "title CONTAINS 'kake' OR data/* CONTAINS 'kake'" ) ) );

        assertContentResultSetEquals( new int[]{}, contentIndexService.query(
            new ContentIndexQuery( "title CONTAINS 'fisk' OR data/* CONTAINS 'fisk'" ) ) );
    }

    @Ignore //Fix
    @Test
    public void testOneWordSearchOnTitleAndFulltext()
    {
        contentIndexService.index( createContentDocument( 123, "ost", null, "ost" ), false );
        contentIndexService.index( createContentDocument( 124, "ost", null, "kake" ), false );
        contentIndexService.index( createContentDocument( 125, "kake", null, "ost" ), false );
        contentIndexService.index( createContentDocument( 126, "kake", null, "kake" ), false );
        flushIndex();



        assertContentResultSetEquals( new int[]{123}, contentIndexService.query(
            new ContentIndexQuery( "title CONTAINS 'ost' AND fulltext CONTAINS 'ost'" ) ) );

        assertContentResultSetEquals( new int[]{123, 124, 125}, contentIndexService.query(
            new ContentIndexQuery( "title CONTAINS 'ost' OR fulltext CONTAINS 'ost'" ) ) );

        assertContentResultSetEquals( new int[]{124, 125, 126}, contentIndexService.query(
            new ContentIndexQuery( "title CONTAINS 'kake' OR fulltext CONTAINS 'kake'" ) ) );

        assertContentResultSetEquals( new int[]{}, contentIndexService.query(
            new ContentIndexQuery( "title CONTAINS 'fisk' OR fulltext CONTAINS 'fisk'" ) ) );
    }

    @Test
    public void testOneWordSearchOnTitleAndUnknown()
    {
        contentIndexService.index( createContentDocument( 123, "ost", null, null ), false );
        contentIndexService.index( createContentDocument( 124, "kake", null, null ), false );
        flushIndex();

        assertContentResultSetEquals( new int[]{}, contentIndexService.query(
            new ContentIndexQuery( "title CONTAINS 'ost' AND unknown CONTAINS 'ost'" ) ) );

        assertContentResultSetEquals( new int[]{123}, contentIndexService.query(
            new ContentIndexQuery( "title CONTAINS 'ost' OR unknown CONTAINS 'ost'" ) ) );

        assertContentResultSetEquals( new int[]{124}, contentIndexService.query(
            new ContentIndexQuery( "unknown CONTAINS 'kake' OR title CONTAINS 'kake'" ) ) );

        assertContentResultSetEquals( new int[]{}, contentIndexService.query(
            new ContentIndexQuery( "title CONTAINS 'fisk' OR unknown CONTAINS 'fisk'" ) ) );
    }


    @Ignore //Fix
    @Test
    public void testOneWordSearchOnTitleAndDataAndFulltext()
    {
        contentIndexService.index( createContentDocument( 121, "ost", "ost", "ost" ), false );
        contentIndexService.index( createContentDocument( 122, "kake", "ost", "ost" ), false );
        contentIndexService.index( createContentDocument( 123, "ost", "kake", "ost" ), false );
        contentIndexService.index( createContentDocument( 124, "ost", "ost", "kake" ), false );
        contentIndexService.index( createContentDocument( 125, "kake", "kake", "ost" ), false );
        contentIndexService.index( createContentDocument( 126, "kake", "ost", "kake" ), false );
        contentIndexService.index( createContentDocument( 127, "ost", "kake", "kake" ), false );
        contentIndexService.index( createContentDocument( 128, "kake", "kake", "kake" ), false );
        flushIndex();

        assertContentResultSetEquals( new int[]{121}, contentIndexService.query(
            new ContentIndexQuery( "title CONTAINS 'ost' AND data/* CONTAINS 'ost' AND fulltext CONTAINS 'ost'" ) ) );

        assertContentResultSetEquals( new int[]{121, 122, 123, 124, 125, 126, 127}, contentIndexService.query(
            new ContentIndexQuery( "title CONTAINS 'ost' OR data/* CONTAINS 'ost' OR fulltext CONTAINS 'ost'" ) ) );

        assertContentResultSetEquals( new int[]{121, 122, 123, 124, 127}, contentIndexService.query(
            new ContentIndexQuery( "title CONTAINS 'ost' OR (data/* CONTAINS 'ost' AND fulltext CONTAINS 'ost')" ) ) );

        assertContentResultSetEquals( new int[]{121, 122, 123, 124, 126}, contentIndexService.query(
            new ContentIndexQuery( "data/* CONTAINS 'ost' OR (title CONTAINS 'ost' AND fulltext CONTAINS 'ost')" ) ) );

        assertContentResultSetEquals( new int[]{121, 122, 123, 124, 125}, contentIndexService.query(
            new ContentIndexQuery( "fulltext CONTAINS 'ost' OR (title CONTAINS 'ost' AND data/* CONTAINS 'ost')" ) ) );

        assertContentResultSetEquals( new int[]{121, 123, 124}, contentIndexService.query(
            new ContentIndexQuery( "title CONTAINS 'ost' AND (data/* CONTAINS 'ost' OR fulltext CONTAINS 'ost')" ) ) );

        assertContentResultSetEquals( new int[]{121, 122, 124}, contentIndexService.query(
            new ContentIndexQuery( "data/* CONTAINS 'ost' AND (title CONTAINS 'ost' OR fulltext CONTAINS 'ost')" ) ) );

        assertContentResultSetEquals( new int[]{121, 122, 123}, contentIndexService.query(
            new ContentIndexQuery( "fulltext CONTAINS 'ost' AND (title CONTAINS 'ost' OR data/* CONTAINS 'ost')" ) ) );
    }

    @Test
    public void testOneWordSearchOnTitleAndDataAndUnknown()
    {
        contentIndexService.index( createContentDocument( 123, "ost", "ost", null ), false );
        contentIndexService.index( createContentDocument( 124, "ost", "kake", null ), false );
        contentIndexService.index( createContentDocument( 125, "kake", "ost", null ), false );
        contentIndexService.index( createContentDocument( 126, "kake", "kake", null ), false );
        flushIndex();

        assertContentResultSetEquals( new int[]{123}, contentIndexService.query(
            new ContentIndexQuery( "(title CONTAINS 'ost' AND data/* CONTAINS 'ost') OR unknown CONTAINS 'fisk'" ) ) );

        assertContentResultSetEquals( new int[]{123, 124, 125}, contentIndexService.query(
            new ContentIndexQuery( "title CONTAINS 'ost' OR data/* CONTAINS 'ost' OR unknown CONTAINS 'fisk'" ) ) );

        assertContentResultSetEquals( new int[]{}, contentIndexService.query(
            new ContentIndexQuery( "(title CONTAINS 'ost' OR data/* CONTAINS 'ost') AND unknown CONTAINS 'fisk'" ) ) );

        assertContentResultSetEquals( new int[]{124, 125, 126}, contentIndexService.query(
            new ContentIndexQuery( "title CONTAINS 'kake' OR data/* CONTAINS 'kake' OR unknown CONTAINS 'fisk'" ) ) );

        assertContentResultSetEquals( new int[]{}, contentIndexService.query(
            new ContentIndexQuery( "title CONTAINS 'fisk' OR data/* CONTAINS 'fisk' OR unknown CONTAINS 'fisk'" ) ) );
    }


    @Ignore //Fix
    @Test
    public void testOneWordSearchOnTitleAndFulltextAndUnknown()
    {
        contentIndexService.index( createContentDocument( 123, "ost", null, "ost" ), false );
        contentIndexService.index( createContentDocument( 124, "ost", null, "kake" ), false );
        contentIndexService.index( createContentDocument( 125, "kake", null, "ost" ), false );
        contentIndexService.index( createContentDocument( 126, "kake", null, "kake" ), false );
        flushIndex();

        assertContentResultSetEquals( new int[]{123}, contentIndexService.query(
            new ContentIndexQuery( "(title CONTAINS 'ost' AND fulltext CONTAINS 'ost') OR unknown CONTAINS 'fisk'" ) ) );

        assertContentResultSetEquals( new int[]{123, 124, 125}, contentIndexService.query(
            new ContentIndexQuery( "title CONTAINS 'ost' OR fulltext CONTAINS 'ost' OR unknown CONTAINS 'fisk'" ) ) );

        assertContentResultSetEquals( new int[]{}, contentIndexService.query(
            new ContentIndexQuery( "title CONTAINS 'ost' AND fulltext CONTAINS 'ost' AND unknown CONTAINS 'fisk'" ) ) );

        assertContentResultSetEquals( new int[]{124, 125, 126}, contentIndexService.query(
            new ContentIndexQuery( "title CONTAINS 'kake' OR fulltext CONTAINS 'kake' OR unknown CONTAINS 'fisk'" ) ) );

        assertContentResultSetEquals( new int[]{}, contentIndexService.query(
            new ContentIndexQuery( "title CONTAINS 'fisk' OR fulltext CONTAINS 'fisk' OR unknown CONTAINS 'fisk'" ) ) );
    }


    @Ignore //Fix
    @Test
    public void testOneWordSearchOnTitleAndDataAndFulltextAndUnknown()
    {
        contentIndexService.index( createContentDocument( 121, "ost", "ost", "ost" ), false );
        contentIndexService.index( createContentDocument( 122, "kake", "ost", "ost" ), false );
        contentIndexService.index( createContentDocument( 123, "ost", "kake", "ost" ), false );
        contentIndexService.index( createContentDocument( 124, "ost", "ost", "kake" ), false );
        contentIndexService.index( createContentDocument( 125, "kake", "kake", "ost" ), false );
        contentIndexService.index( createContentDocument( 126, "kake", "ost", "kake" ), false );
        contentIndexService.index( createContentDocument( 127, "ost", "kake", "kake" ), false );
        contentIndexService.index( createContentDocument( 128, "kake", "kake", "kake" ), false );
        flushIndex();

        assertContentResultSetEquals( new int[]{121}, contentIndexService.query( new ContentIndexQuery(
            "(title CONTAINS 'ost' AND data/* CONTAINS 'ost' AND fulltext CONTAINS 'ost') OR unknown CONTAINS 'fisk'" ) ) );

        assertContentResultSetEquals( new int[]{}, contentIndexService.query( new ContentIndexQuery(
            "(title CONTAINS 'ost' AND data/* CONTAINS 'ost' AND fulltext CONTAINS 'ost') AND unknown CONTAINS 'fisk'" ) ) );

        assertContentResultSetEquals( new int[]{121, 122, 123, 124, 125, 126, 127}, contentIndexService.query( new ContentIndexQuery(
            "(title CONTAINS 'ost' OR data/* CONTAINS 'ost' OR fulltext CONTAINS 'ost') OR unknown CONTAINS 'fisk'" ) ) );

        assertContentResultSetEquals( new int[]{}, contentIndexService.query( new ContentIndexQuery(
            "(title CONTAINS 'ost' OR data/* CONTAINS 'ost' OR fulltext CONTAINS 'ost') AND unknown CONTAINS 'fisk'" ) ) );

        assertContentResultSetEquals( new int[]{121, 122, 123, 124, 127}, contentIndexService.query( new ContentIndexQuery(
            "(title CONTAINS 'ost' OR (data/* CONTAINS 'ost' AND fulltext CONTAINS 'ost')) OR unknown CONTAINS 'fisk'" ) ) );

        assertContentResultSetEquals( new int[]{}, contentIndexService.query( new ContentIndexQuery(
            "(title CONTAINS 'ost' OR (data/* CONTAINS 'ost' AND fulltext CONTAINS 'ost')) AND unknown CONTAINS 'fisk'" ) ) );

        assertContentResultSetEquals( new int[]{121, 122, 123, 124, 126}, contentIndexService.query( new ContentIndexQuery(
            "(data/* CONTAINS 'ost' OR (title CONTAINS 'ost' AND fulltext CONTAINS 'ost')) OR unknown CONTAINS 'fisk'" ) ) );

        assertContentResultSetEquals( new int[]{}, contentIndexService.query( new ContentIndexQuery(
            "(data/* CONTAINS 'ost' OR (title CONTAINS 'ost' AND fulltext CONTAINS 'ost')) AND unknown CONTAINS 'fisk'" ) ) );

        assertContentResultSetEquals( new int[]{121, 122, 123, 124, 125}, contentIndexService.query( new ContentIndexQuery(
            "(fulltext CONTAINS 'ost' OR (title CONTAINS 'ost' AND data/* CONTAINS 'ost')) OR unknown CONTAINS 'fisk'" ) ) );

        assertContentResultSetEquals( new int[]{}, contentIndexService.query( new ContentIndexQuery(
            "(fulltext CONTAINS 'ost' OR (title CONTAINS 'ost' AND data/* CONTAINS 'ost')) AND unknown CONTAINS 'fisk'" ) ) );

        assertContentResultSetEquals( new int[]{121, 123, 124}, contentIndexService.query( new ContentIndexQuery(
            "(title CONTAINS 'ost' AND (data/* CONTAINS 'ost' OR fulltext CONTAINS 'ost')) OR unknown CONTAINS 'fisk'" ) ) );

        assertContentResultSetEquals( new int[]{}, contentIndexService.query( new ContentIndexQuery(
            "(title CONTAINS 'ost' AND (data/* CONTAINS 'ost' OR fulltext CONTAINS 'ost')) AND unknown CONTAINS 'fisk'" ) ) );

        assertContentResultSetEquals( new int[]{121, 122, 124}, contentIndexService.query( new ContentIndexQuery(
            "(data/* CONTAINS 'ost' AND (title CONTAINS 'ost' OR fulltext CONTAINS 'ost')) OR unknown CONTAINS 'fisk'" ) ) );

        assertContentResultSetEquals( new int[]{}, contentIndexService.query( new ContentIndexQuery(
            "(data/* CONTAINS 'ost' AND (title CONTAINS 'ost' OR fulltext CONTAINS 'ost')) AND unknown CONTAINS 'fisk'" ) ) );

        assertContentResultSetEquals( new int[]{121, 122, 123}, contentIndexService.query( new ContentIndexQuery(
            "(fulltext CONTAINS 'ost' AND (title CONTAINS 'ost' OR data/* CONTAINS 'ost')) OR unknown CONTAINS 'fisk'" ) ) );

        assertContentResultSetEquals( new int[]{}, contentIndexService.query( new ContentIndexQuery(
            "(fulltext CONTAINS 'ost' AND (title CONTAINS 'ost' OR data/* CONTAINS 'ost')) AND unknown CONTAINS 'fisk'" ) ) );
    }

    @Test
    public void testSplittedNormalIndexWithAnd()
    {
        contentIndexService.index(
            createContentDocument( 101, "title", new String[][]{{"data/text", "fisk ost"}, {"data/text", "torsk tine"}} ), false );
        contentIndexService.index(
            createContentDocument( 102, "title", new String[][]{{"data/text", "ku ost"}, {"data/text", "gryte tine"}} ), false );
        flushIndex();

        assertContentResultSetEquals( new int[]{101}, contentIndexService.query(
            new ContentIndexQuery( "data/text CONTAINS 'fisk' AND data/text CONTAINS 'torsk'", "" ) ) );
    }

    @Test
    public void testSplittedNormalIndexWithOr()
    {
        contentIndexService.index(
            createContentDocument( 101, "title", new String[][]{{"data/text", "fisk ost"}, {"data/text", "torsk tine"}} ), false );
        contentIndexService.index(
            createContentDocument( 102, "title", new String[][]{{"data/text", "ku ost"}, {"data/text", "gryte tine"}} ), false );
        flushIndex();

        assertContentResultSetEquals( new int[]{101}, contentIndexService.query(
            new ContentIndexQuery( "data/text CONTAINS 'fisk' OR data/text CONTAINS 'torsk'", "" ) ) );
    }


    @Ignore //Fix
    @Test
    public void testMultipleSameLikeExactWords()
        throws Exception
    {
        contentIndexService.index( createContentDocument( 101, "title",
                                                          new String[][]{{"data/heading", "enonic"}, {"data/preface", "enonic"},
                                                              {"data/text", "enonic"}} ), false );

        assertContentResultSetEquals( new int[]{101}, contentIndexService.query(
            new ContentIndexQuery( "data/heading LIKE '%ENONIC%' or data/preface LIKE '%ENONIC%' or data/text LIKE '%ENONIC%'", "" ) ) );
    }


}
