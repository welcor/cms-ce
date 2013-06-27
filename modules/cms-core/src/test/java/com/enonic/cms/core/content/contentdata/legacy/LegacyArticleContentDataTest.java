/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.content.contentdata.legacy;

import org.jdom.Document;
import org.junit.Test;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.core.AbstractXmlCreatorTest;
import com.enonic.cms.core.content.ContentKey;

public class LegacyArticleContentDataTest
    extends AbstractXmlCreatorTest
{
    @Test
    public void testRemoveReferencesToContent()
        throws Exception
    {
        final String xml = getXml( getClass().getName().replace( '.', '/' ) + "-sample.xml" );

        final Document document = JDOMUtil.parseDocument( xml );
        final LegacyArticleContentData contentData = new LegacyArticleContentData( document );

        contentData.markReferencesToContentAsDeleted( new ContentKey( "81" ) );
        assertXPathEquals( "/contentdata/body/image[2]/@deleted",
                           contentData.getContentDataXml(), "true" );
        contentData.markReferencesToContentAsDeleted( new ContentKey( "108" ) );
        assertXPathEquals( "/contentdata/body/image[1]/@deleted",
                           contentData.getContentDataXml(), "true" );
        contentData.markReferencesToContentAsDeleted( new ContentKey( "93" ) );
        assertXPathEquals( "/contentdata/teaser/image/@deleted",
                           contentData.getContentDataXml(), "true" );
        contentData.markReferencesToContentAsDeleted( new ContentKey( "4" ) );
        assertXPathEquals( "/contentdata/files/file[1]/@deleted",
                           contentData.getContentDataXml(), "true" );
        contentData.markReferencesToContentAsDeleted( new ContentKey( "97" ) );
        assertXPathEquals( "/contentdata/files/file[2]/@deleted",
                           contentData.getContentDataXml(), "true" );
    }

}
