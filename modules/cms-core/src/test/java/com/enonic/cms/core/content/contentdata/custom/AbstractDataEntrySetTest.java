/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.content.contentdata.custom;


import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.contentdata.custom.contentkeybased.RelatedContentDataEntry;
import com.enonic.cms.core.content.contentdata.custom.relationdataentrylistbased.RelatedContentsDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contenttype.ContentHandlerName;
import com.enonic.cms.core.content.contenttype.ContentTypeConfig;
import com.enonic.cms.core.content.contenttype.CtyFormConfig;
import com.enonic.cms.core.content.contenttype.CtySetConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.RelatedContentDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.TextDataEntryConfig;

public class AbstractDataEntrySetTest
{
    private ContentTypeConfig contentTypeConfig;

    @Before
    public void before()
    {
        contentTypeConfig = new ContentTypeConfig( ContentHandlerName.CUSTOM, "MyRelatedTypes" );

        final CtyFormConfig ctyForm = new CtyFormConfig( contentTypeConfig );
        contentTypeConfig.setForm( ctyForm );

        final CtySetConfig ctyBlockArticle = ctyForm.addBlock( new CtySetConfig( ctyForm, "MyRelatedTypes", null ) );
        ctyBlockArticle.addInput( new TextDataEntryConfig( "title", true, "text", "contentdata/title" ) );
        ctyBlockArticle.addInput( new RelatedContentDataEntryConfig( "mySingleRelatedToBeUnmodified", false, "My related1",
                                                                     "contentdata/mySingleRelatedToBeUnmodified", false,
                                                                     Arrays.asList( "MyRelatedTypes" ) ) );
        ctyBlockArticle.addInput( new RelatedContentDataEntryConfig( "mySingleRelatedToBeModified", false, "My related2",
                                                                     "contentdata/mySingleRelatedToBeModified", false,
                                                                     Arrays.asList( "MyRelatedTypes" ) ) );
        ctyBlockArticle.addInput( new RelatedContentDataEntryConfig( "myMultipleRelatedToBeModified", false, "My related3",
                                                                     "contentdata/myMultipleRelatedToBeModified", true,
                                                                     Arrays.asList( "MyRelatedTypes" ) ) );

        final CtySetConfig myGroup = ctyForm.addBlock( new CtySetConfig( ctyForm, "MyGroup", "contentdata/mygroup" ) );
        myGroup.addInput( new RelatedContentDataEntryConfig( "myMultipleRelatedToBeModifiedGroup", false, "My related4",
                                                             "contentdata/myMultipleRelatedToBeModifiedGroup", true,
                                                             Arrays.asList( "MyRelatedTypes" ) ) );
    }

    @Test
    public void testRemoveReferencesToContent_ContentWithSingleRelated()
        throws Exception
    {
        final ContentKey related1 = new ContentKey( "1" );
        final ContentKey related2 = new ContentKey( "2" );

        final CustomContentData customContentData = createContentWithSingleRelated( "title", related1, related2 );

        Assert.assertFalse( customContentData.markReferencesToContentAsDeleted( new ContentKey( "3" ) ) );

        Assert.assertTrue( customContentData.markReferencesToContentAsDeleted( related1 ) );
        Assert.assertFalse( customContentData.markReferencesToContentAsDeleted( related1 ) );

        Assert.assertTrue( customContentData.markReferencesToContentAsDeleted( related2 ) );
        Assert.assertFalse( customContentData.markReferencesToContentAsDeleted( related2 ) );

        Assert.assertFalse( customContentData.markReferencesToContentAsDeleted( new ContentKey( "3" ) ) );
    }

    @Test
    public void testRemoveReferencesToContent_ContentWithMultipleRelated()
        throws Exception
    {
        final ContentKey related1 = new ContentKey( "1" );
        final ContentKey related2 = new ContentKey( "2" );

        final CustomContentData customContentData = createContentWithMultipleRelated( "title", related1, related2 );

        Assert.assertFalse( customContentData.markReferencesToContentAsDeleted( new ContentKey( "3" ) ) );

        Assert.assertTrue( customContentData.markReferencesToContentAsDeleted( related1 ) );
        Assert.assertFalse( customContentData.markReferencesToContentAsDeleted( related1 ) );

        Assert.assertTrue( customContentData.markReferencesToContentAsDeleted( related2 ) );
        Assert.assertFalse( customContentData.markReferencesToContentAsDeleted( related2 ) );

        Assert.assertFalse( customContentData.markReferencesToContentAsDeleted( new ContentKey( "3" ) ) );
    }

    @Test
    public void testRemoveReferencesToContent_ContentWithMultipleRelatedInGroup()
        throws Exception
    {
        final ContentKey related1 = new ContentKey( "1" );
        final ContentKey related2 = new ContentKey( "2" );

        final CustomContentData customContentData = createContentWithMultipleRelatedInGroup( "title", related1, related2 );

        Assert.assertFalse( customContentData.markReferencesToContentAsDeleted( new ContentKey( "3" ) ) );

        Assert.assertTrue( customContentData.markReferencesToContentAsDeleted( related1 ) );
        Assert.assertFalse( customContentData.markReferencesToContentAsDeleted( related1 ) );

        Assert.assertTrue( customContentData.markReferencesToContentAsDeleted( related2 ) );
        Assert.assertFalse( customContentData.markReferencesToContentAsDeleted( related2 ) );

        Assert.assertFalse( customContentData.markReferencesToContentAsDeleted( new ContentKey( "3" ) ) );
    }


    private CustomContentData createContentWithSingleRelated( String title, ContentKey related1, ContentKey related2 )
    {
        final CustomContentData contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentData.getInputConfig( "title" ), title ) );

        if ( related1 != null && related2 != null )
        {
            contentData.add( new RelatedContentDataEntry( contentData.getInputConfig( "mySingleRelatedToBeModified" ), related1 ) );
            contentData.add( new RelatedContentDataEntry( contentData.getInputConfig( "mySingleRelatedToBeUnmodified" ), related2 ) );
        }

        return contentData;
    }


    private CustomContentData createContentWithMultipleRelated( String title, ContentKey related1, ContentKey related2 )
    {
        final CustomContentData contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentData.getInputConfig( "title" ), title ) );

        if ( related1 != null && related2 != null )
        {
            final DataEntryConfig dataEntryConfig = contentData.getInputConfig( "myMultipleRelatedToBeModified" );
            final RelatedContentsDataEntry contentsDataEntry = new RelatedContentsDataEntry( dataEntryConfig );
            contentsDataEntry.add( new RelatedContentDataEntry( dataEntryConfig, related1 ) );
            contentsDataEntry.add( new RelatedContentDataEntry( dataEntryConfig, related2 ) );

            contentData.add( contentsDataEntry );
        }

        return contentData;
    }

    private CustomContentData createContentWithMultipleRelatedInGroup( String title, ContentKey related1, ContentKey related2 )
    {
        final CustomContentData contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentData.getInputConfig( "title" ), title ) );

        if ( related1 != null && related2 != null )
        {
            GroupDataEntry groupDataEntry = new GroupDataEntry( "MyGroup", "contentdata/mygroup", 1 );
            groupDataEntry.setConfig( contentData.getSetConfig( "MyGroup" ) );

            final DataEntryConfig dataEntryConfig = contentData.getInputConfig( "myMultipleRelatedToBeModifiedGroup" );
            final RelatedContentsDataEntry contentsDataEntry = new RelatedContentsDataEntry( dataEntryConfig );
            contentsDataEntry.add( new RelatedContentDataEntry( dataEntryConfig, related1 ) );
            contentsDataEntry.add( new RelatedContentDataEntry( dataEntryConfig, related2 ) );

            groupDataEntry.add( contentsDataEntry );

            contentData.add( groupDataEntry );
        }

        return contentData;
    }

}
