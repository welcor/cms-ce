/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import com.enonic.cms.core.content.contentdata.ContentData;
import com.enonic.cms.core.content.contentdata.InvalidContentDataException;
import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.DataEntry;
import com.enonic.cms.core.content.contentdata.custom.contentkeybased.RelatedContentDataEntry;
import com.enonic.cms.core.content.contentdata.custom.relationdataentrylistbased.RelatedContentsDataEntry;
import com.enonic.cms.core.content.contenttype.dataentryconfig.RelatedContentDataEntryConfig;
import com.enonic.cms.store.dao.ContentDao;

public class ContentValidator
{
    private ContentDao contentDao;

    private boolean contentDataChanged = false;

    public ContentValidator( ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    public void validate( ContentVersionEntity contentVersion )
    {
        validate( contentVersion.getContentData() );
        if ( contentDataChanged )
        {
            contentVersion.setXmlDataFromContentData();
        }
    }

    public void validate( ContentData contentData )
    {
        if ( contentData == null )
        {
            throw new IllegalArgumentException( "Given contentdata cannot be null" );
        }

        if ( contentData instanceof CustomContentData )
        {
            CustomContentData customContentData = (CustomContentData) contentData;
            customContentData.validate();

            for ( DataEntry dataEntry : customContentData.getEntries() )
            {
                if ( dataEntry instanceof RelatedContentDataEntry )
                {
                    validateRelatedContent( (RelatedContentDataEntry) dataEntry );
                }
                else if ( dataEntry instanceof RelatedContentsDataEntry )
                {
                    validateRelatedContents( (RelatedContentsDataEntry) dataEntry );
                }

            }

        }
    }

    private void validateRelatedContents( RelatedContentsDataEntry relatedContents )
    {
        for ( RelatedContentDataEntry dataEntry : relatedContents.getEntries() )
        {
            validateRelatedContent( dataEntry );
        }
    }

    private void validateRelatedContent( RelatedContentDataEntry relatedContentDataEntry )
    {
        if ( relatedContentDataEntry.isMarkedAsDeleted() )
        {
            return; // do not validate deleted content
        }

        final ContentKey relatedContentKey = relatedContentDataEntry.getContentKey();
        if ( relatedContentKey == null )
        {
            return; // A null content key is a valid content key
        }
        final ContentEntity relatedContent = contentDao.findByKey( relatedContentKey );
        if ( relatedContent == null || relatedContent.isDeleted() )
        {
            relatedContentDataEntry.markAsDeleted();
            contentDataChanged = true;
            return;
        }

        final RelatedContentDataEntryConfig relatedConfig = (RelatedContentDataEntryConfig) relatedContentDataEntry.getConfig();

        if ( !relatedConfig.isContentTypeNameSupported( relatedContent.getContentType().getName() ) )
        {
            throw new InvalidContentDataException(
                "Related data's config type is not equals to the configuration. Illegal relation to content: " + relatedContentKey );
        }
    }
}
