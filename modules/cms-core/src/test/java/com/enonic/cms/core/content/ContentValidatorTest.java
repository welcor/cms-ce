/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.content;


import java.io.IOException;

import org.jdom.JDOMException;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.contentkeybased.RelatedContentDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contenttype.ContentHandlerEntity;
import com.enonic.cms.core.content.contenttype.ContentHandlerName;
import com.enonic.cms.core.content.contenttype.ContentTypeConfig;
import com.enonic.cms.core.content.contenttype.ContentTypeConfigBuilder;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.store.dao.ContentDao;


import static junit.framework.Assert.assertTrue;

public class ContentValidatorTest
{
    private ContentDao contentDao = Mockito.mock( ContentDao.class );

    @Test
    public void given_contentdata_with_related_content_that_does_not_exist_when_validate_then_relatedContentDataEntry_is_marked_as_deleted()
        throws JDOMException, IOException
    {
        // setup
        ContentTypeConfigBuilder ctyBuilder = new ContentTypeConfigBuilder( "Person", "name" );
        ctyBuilder.startBlock( "Person" );
        ctyBuilder.addInput( "name", "text", "contentdata/name", "Name", true );
        ctyBuilder.addInput( "relatedPerson", "relatedcontent", "contentdata/relatedPerson", "Related person", false );
        ctyBuilder.endBlock();

        ContentTypeConfig config = createContentType( ctyBuilder, createCustomContentHandler() ).getContentTypeConfig();

        CustomContentData contentData = new CustomContentData( config );
        contentData.add( new TextDataEntry( config.getInputConfig( "name" ), "Name" ) );
        contentData.add( new RelatedContentDataEntry( config.getInputConfig( "relatedPerson" ), new ContentKey( 999 ) ) );

        ContentVersionEntity contentVersion = createContentVersion( contentData );

        // exercise
        new ContentValidator( contentDao ).validate( contentVersion );

        // verify
        RelatedContentDataEntry relatedContentDataEntry = (RelatedContentDataEntry) contentData.getEntry( "relatedPerson" );
        assertTrue( relatedContentDataEntry.isMarkedAsDeleted() );
    }

    @Test
    public void given_contentdata_with_related_content_that_is_deleted_when_validate_then_relatedContentDataEntry_is_marked_as_deleted()
        throws JDOMException, IOException
    {
        // setup
        ContentKey deletedContent = new ContentKey( 999 );

        Mockito.when( contentDao.findByKey( deletedContent ) ).thenReturn( createDeletedContent( deletedContent ) );

        ContentTypeConfigBuilder ctyBuilder = new ContentTypeConfigBuilder( "Person", "name" );
        ctyBuilder.startBlock( "Person" );
        ctyBuilder.addInput( "name", "text", "contentdata/name", "Name", true );
        ctyBuilder.addInput( "relatedPerson", "relatedcontent", "contentdata/relatedPerson", "Related person", false );
        ctyBuilder.endBlock();

        ContentTypeConfig config = createContentType( ctyBuilder, createCustomContentHandler() ).getContentTypeConfig();

        CustomContentData contentData = new CustomContentData( config );
        contentData.add( new TextDataEntry( config.getInputConfig( "name" ), "Name" ) );
        contentData.add( new RelatedContentDataEntry( config.getInputConfig( "relatedPerson" ), deletedContent ) );

        ContentVersionEntity contentVersion = createContentVersion( contentData );

        // exercise
        new ContentValidator( contentDao ).validate( contentVersion );

        // verify
        RelatedContentDataEntry relatedContentDataEntry = (RelatedContentDataEntry) contentData.getEntry( "relatedPerson" );
        assertTrue( relatedContentDataEntry.isMarkedAsDeleted() );
    }

    private ContentEntity createDeletedContent( final ContentKey deletedContent )
    {
        ContentEntity content = new ContentEntity();
        content.setKey( deletedContent );
        content.setDeleted( true );
        return content;
    }

    private ContentVersionEntity createContentVersion( final CustomContentData contentData )
    {
        ContentVersionEntity contentVersion = new ContentVersionEntity();
        contentVersion.setContentData( contentData );
        return contentVersion;
    }

    private ContentTypeEntity createContentType( final ContentTypeConfigBuilder ctyBuilder, final ContentHandlerEntity contentHandler )
    {
        ContentTypeEntity contentType = new ContentTypeEntity();
        contentType.setContentHandler( contentHandler );
        contentType.setData( XMLDocumentFactory.create( ctyBuilder.toString() ).getAsJDOMDocument() );
        return contentType;
    }

    private ContentHandlerEntity createCustomContentHandler()
    {
        ContentHandlerEntity contentHandler = new ContentHandlerEntity();
        contentHandler.setName( "Custom content handler" );
        contentHandler.setClassName( ContentHandlerName.CUSTOM.getHandlerClassShortName() );
        return contentHandler;
    }
}
