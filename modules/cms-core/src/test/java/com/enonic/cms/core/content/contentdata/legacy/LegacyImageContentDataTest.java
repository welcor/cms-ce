package com.enonic.cms.core.content.contentdata.legacy;

import org.jdom.Document;
import org.junit.Test;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.core.AbstractXmlCreatorTest;
import com.enonic.cms.core.content.ContentKey;

public class LegacyImageContentDataTest
    extends AbstractXmlCreatorTest
{

    @Test
    public void testRemoveReferencesToContent()
        throws Exception
    {
        final String xml = getXml( getClass().getName().replace( '.', '/' ) + "-sample.xml" );

        final Document document = JDOMUtil.parseDocument( xml );
        final LegacyImageContentData contentData = new LegacyImageContentData( document );

        contentData.markReferencesToContentAsDeleted( new ContentKey( "118" ) );
        assertXPathEquals( "/contentdata/file/@deleted", contentData.getContentDataXml(), "true" );

    }

}
