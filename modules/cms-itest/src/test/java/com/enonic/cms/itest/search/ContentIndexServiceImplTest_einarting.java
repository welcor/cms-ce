package com.enonic.cms.itest.search;

import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 1/4/12
 * Time: 10:33 AM
 */
public class ContentIndexServiceImplTest_einarting
    extends ContentIndexServiceTestBase
{

    @Test
    public void testEinarTing1()
    {
        service.index( createContentDocument( 100, "title", new String[][]{{"data/preface", "denne skal ikke gi treff"},
            {"data/description", "dette skal ikke gi treff"}} ), false );

        service.index( createContentDocument( 101, "title", new String[][]{{"data/preface", "dette er en ingress ja"},
            {"data/description", "dette er en beskrivelse ja"}} ), false );

        letTheIndexFinishItsWork();

        assertContentResultSetEquals( new int[]{101}, service.query(
            new ContentIndexQuery( "data/preface CONTAINS 'ingress' AND data/description CONTAINS 'beskrivelse'", 10) ) );

    }

    @Test
    public void testEinarTing2()
    {
        service.index( createContentDocument( 100, "title", new String[][]{{"data/preface", "denne skal ikke gi treff"},
            {"data/description", "dette skal ikke gi treff"}} ), false );

        service.index( createContentDocument( 101, "title", new String[][]{{"data/preface", "dette er en ingress ja"},
            {"data/description", "dette er en beskrivelse ja"}} ), false );

        letTheIndexFinishItsWork();

        assertContentResultSetEquals( new int[]{101}, service.query(
            new ContentIndexQuery( "data/* CONTAINS 'ingress' AND data/* CONTAINS 'beskrivelse'", 10 ) ) );

    }

    @Test
    public void testEinarTing3()
    {
        service.index( createContentDocument( 100, "title", new String[][]{{"data/preface", "denne skal ikke gi treff"},
            {"data/description", "dette skal ikke gi treff"}} ), false );

        service.index( createContentDocument( 101, "title", new String[][]{{"data/preface", "dette er en ingress ja"},
            {"data/description", "dette er en beskrivelse ja"}} ), false );

        letTheIndexFinishItsWork();

        assertContentResultSetEquals( new int[]{101}, service.query(
            new ContentIndexQuery( "data/* CONTAINS 'ingress' OR data/* CONTAINS 'beskrivelse'", 10 ) ) );

    }


}
