package com.enonic.cms.itest.search;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.BigText;
import com.enonic.cms.core.content.index.ContentDocument;
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
        indexDocument( 123, "ost", "ost", null );
        indexDocument( 124, "ost", "kake", null );
        indexDocument( 125, "kake", "ost", null );
        indexDocument( 126, "kake", "kake", null );
        flushIndex();

        assertContentResultSetEquals( new int[]{123}, contentIndexService.query(
            new ContentIndexQuery( "title CONTAINS 'ost' AND data/* CONTAINS 'ost'" ) ) );

        assertContentResultSetEquals( new int[]{123, 124, 125}, contentIndexService.query(
            new ContentIndexQuery( "title CONTAINS 'ost' OR data/* CONTAINS 'ost'" ) ) );

        assertContentResultSetEquals( new int[]{124, 125, 126}, contentIndexService.query(
            new ContentIndexQuery( "title CONTAINS 'kake' OR data/* CONTAINS 'kake'" ) ) );

        assertContentResultSetEquals( new int[]{}, contentIndexService.query(
            new ContentIndexQuery( "title CONTAINS 'fisk' OR data/* CONTAINS 'fisk'" ) ) );
    }

    @Test
    public void testOneWordSearchOnTitleAndFulltext()
    {
        indexDocument( 123, "ost", "ost" );
        indexDocument( 124, "ost", "kake" );
        indexDocument( 125, "kake", "ost" );
        indexDocument( 126, "kake", "kake" );

        flushIndex();

        printAllIndexContent();

        assertContentResultSetEquals( new int[]{123}, contentIndexService.query(
            new ContentIndexQuery( "title CONTAINS 'ost' AND fulltext CONTAINS 'ost'" ) ) );

        assertContentResultSetEquals( new int[]{123, 124, 125}, contentIndexService.query(
            new ContentIndexQuery( "title CONTAINS 'ost' OR fulltext CONTAINS 'ost'" ) ) );

        assertContentResultSetEquals( new int[]{124, 125, 126}, contentIndexService.query(
            new ContentIndexQuery( "title CONTAINS 'kake' OR fulltext CONTAINS 'kake'" ) ) );

        assertContentResultSetEquals( new int[]{}, contentIndexService.query(
            new ContentIndexQuery( "title CONTAINS 'fisk' OR fulltext CONTAINS 'fisk'" ) ) );

    }

    private void indexDocument( int contentKey, String title, String fulltext )
    {
        ContentDocument doc1 = new ContentDocument( new ContentKey( contentKey ) );
        doc1.setBinaryExtractedText( new BigText( fulltext ) );
        doc1.setTitle( title );
        doc1.setContentTypeKey( new ContentTypeKey( 1 ) );
        doc1.setContentTypeName( "myContentType" );
        contentIndexService.index( doc1 );
    }

    private void indexDocument( int contentKey, String title, String userData, String fulltext )
    {
        ContentDocument doc1 = new ContentDocument( new ContentKey( contentKey ) );
        if ( StringUtils.isNotBlank( fulltext ) )
        {
            doc1.setBinaryExtractedText( new BigText( fulltext ) );
        }
        doc1.setTitle( title );
        doc1.setContentTypeKey( new ContentTypeKey( 1 ) );
        doc1.setContentTypeName( "myContentType" );
        doc1.addUserDefinedField( "myDataField", userData );

        contentIndexService.index( doc1 );
    }

    @Test
    public void testOneWordSearchOnTitleAndUnknown()
    {
        contentIndexService.index( createContentDocument( 123, "ost", null, null ) );
        contentIndexService.index( createContentDocument( 124, "kake", null, null ) );
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


    @Test
    public void testOneWordSearchOnTitleAndDataAndFulltext()
    {
        indexDocument( 121, "ost", "ost", "ost" );
        indexDocument( 122, "kake", "ost", "ost" );
        indexDocument( 123, "ost", "kake", "ost" );
        indexDocument( 124, "ost", "ost", "kake" );
        indexDocument( 125, "kake", "kake", "ost" );
        indexDocument( 126, "kake", "ost", "kake" );
        indexDocument( 127, "ost", "kake", "kake" );
        indexDocument( 128, "kake", "kake", "kake" );

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
        indexDocument( 123, "ost", "ost", null );
        indexDocument( 124, "ost", "kake", null );
        indexDocument( 125, "kake", "ost", null );
        indexDocument( 126, "kake", "kake", null );

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


    @Test
    public void testOneWordSearchOnTitleAndFulltextAndUnknown()
    {
        indexDocument( 123, "ost", "ost" );
        indexDocument( 124, "ost", "kake" );
        indexDocument( 125, "kake", "ost" );
        indexDocument( 126, "kake", "kake" );
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


    @Test
    public void testOneWordSearchOnTitleAndDataAndFulltextAndUnknown()
    {
        indexDocument( 121, "ost", "ost", "ost" );
        indexDocument( 122, "kake", "ost", "ost" );
        indexDocument( 123, "ost", "kake", "ost" );
        indexDocument( 124, "ost", "ost", "kake" );
        indexDocument( 125, "kake", "kake", "ost" );
        indexDocument( 126, "kake", "ost", "kake" );
        indexDocument( 127, "ost", "kake", "kake" );
        indexDocument( 128, "kake", "kake", "kake" );
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
            createContentDocument( 101, "title", new String[][]{{"data/text", "fisk ost"}, {"data/text", "torsk tine"}} ) );
        contentIndexService.index(
            createContentDocument( 102, "title", new String[][]{{"data/text", "ku ost"}, {"data/text", "gryte tine"}} ) );
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

}
