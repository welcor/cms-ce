package com.enonic.cms.itest.search;

import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 1/4/12
 * Time: 10:33 AM
 */
public class ContentIndexServiceImpl_queryEinartingTest
    extends ContentIndexServiceTestBase
{

    @Test
    public void testEinarTing1()
    {
        contentIndexService.index( createContentDocument( 100, "title", new String[][]{{"data/preface", "denne skal ikke gi treff"},
            {"data/description", "dette skal ikke gi treff"}} ), false );

        contentIndexService.index( createContentDocument( 101, "title", new String[][]{{"data/preface", "dette er en ingress ja"},
            {"data/description", "dette er en beskrivelse ja"}} ), false );

        flushIndex();

        assertContentResultSetEquals( new int[]{101}, contentIndexService.query(
            new ContentIndexQuery( "data/preface CONTAINS 'ingress' AND data/description CONTAINS 'beskrivelse'" ) ) );

    }

    @Test
    public void testEinarTing2()
    {
        contentIndexService.index( createContentDocument( 100, "title", new String[][]{{"data/preface", "denne skal ikke gi treff"},
            {"data/description", "dette skal ikke gi treff"}} ), false );

        contentIndexService.index( createContentDocument( 101, "title", new String[][]{{"data/preface", "dette er en ingress ja"},
            {"data/description", "dette er en beskrivelse ja"}} ), false );

        flushIndex();

        assertContentResultSetEquals( new int[]{101}, contentIndexService.query(
            new ContentIndexQuery( "data/* CONTAINS 'ingress' AND data/* CONTAINS 'beskrivelse'" ) ) );

    }

    @Test
    public void testEinarTing3()
    {
        contentIndexService.index( createContentDocument( 100, "title", new String[][]{{"data/preface", "denne skal ikke gi treff"},
            {"data/description", "dette skal ikke gi treff"}} ), false );

        contentIndexService.index( createContentDocument( 101, "title", new String[][]{{"data/preface", "dette er en ingress ja"},
            {"data/description", "dette er en beskrivelse ja"}} ), false );

        flushIndex();

        assertContentResultSetEquals( new int[]{101}, contentIndexService.query(
            new ContentIndexQuery( "data/* CONTAINS 'ingress' OR data/* CONTAINS 'beskrivelse'" ) ) );

    }


}
