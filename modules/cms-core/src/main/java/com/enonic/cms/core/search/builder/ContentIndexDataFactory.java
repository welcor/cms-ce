/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.search.builder;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.enonic.cms.core.content.access.ContentAccessEntity;
import com.enonic.cms.core.content.category.CategoryAccessEntity;
import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.index.BigText;
import com.enonic.cms.core.content.index.ContentDocument;
import com.enonic.cms.core.content.index.SimpleText;
import com.enonic.cms.core.content.index.UserDefinedField;
import com.enonic.cms.core.security.group.GroupKey;

public final class ContentIndexDataFactory
    extends IndexFieldNameConstants
{
    private final ContentIndexDataCustomDataFactory customDataFactory = new ContentIndexDataCustomDataFactory();

    private final ContentIndexDataSectionFactory sectionFactory = new ContentIndexDataSectionFactory();

    private final ContentIndexDataAccessRightsFactory accessRightsFactory = new ContentIndexDataAccessRightsFactory();

    public ContentIndexData create( ContentDocument content, boolean skipAttachments )
    {
        return doCreate( content, skipAttachments );
    }

    public ContentIndexData create( ContentDocument content )
    {
        return doCreate( content, true );
    }

    private ContentIndexData doCreate( final ContentDocument content, boolean skipAttachments )
    {
        ContentIndexData contentIndexData = new ContentIndexData( content.getContentKey() );

        addMetaData( contentIndexData, content );
        addCategory( contentIndexData, content );
        addContentType( contentIndexData, content );
        addSections( contentIndexData, content );
        addAccessRights( contentIndexData, content );
        addCustomData( contentIndexData, content );

        if ( !skipAttachments )
        {
            try
            {
                addExtractedBinaryData( contentIndexData, content );
            }
            catch ( Exception e )
            {
                throw new ContentIndexDataFactoryException( "Failed to build index-data for binaries", e );
            }
        }

        return contentIndexData;
    }

    private void addMetaData( ContentIndexData contentIndexData, ContentDocument content )
    {
        contentIndexData.addContentData( CONTENT_KEY_FIELDNAME, content.getContentKey().toInt() );
        contentIndexData.addContentData( TITLE_FIELDNAME, content.getTitle() );
        contentIndexData.addContentData( TIMESTAMP_FIELDNAME, content.getTimestamp() );
        contentIndexData.addContentData( PUBLISH_FROM_FIELDNAME, content.getPublishFrom() );
        contentIndexData.addContentData( PUBLISH_TO_FIELDNAME, content.getPublishTo() );
        contentIndexData.addContentData( TIMESTAMP_FIELDNAME, content.getTimestamp() );
        contentIndexData.addContentData( STATUS_FIELDNAME, content.getStatus() );
        contentIndexData.addContentData( PRIORITY_FIELDNAME, content.getPriority() );
        contentIndexData.addContentData( ASSIGNMENT_DUE_DATE_FIELDNAME, content.getAssignmentDueDate() );
        contentIndexData.addContentData( CONTENT_CREATED, content.getCreated() );
        contentIndexData.addContentData( CONTENT_MODIFIED, content.getModified() );

        addUsers( contentIndexData, content );
    }

    private void addUsers( ContentIndexData contentIndexData, ContentDocument content )
    {
        addUserValues( contentIndexData, OWNER_FIELDNAME, content.getOwnerKey(), content.getOwnerName(), content.getOwnerQualifiedName() );
        addUserValues( contentIndexData, MODIFIER_FIELDNAME, content.getModifierKey(), content.getModifierName(),
                       content.getModifierQualifiedName() );
        addUserValues( contentIndexData, ASSIGNEE_FIELDNAME, content.getAssigneeKey(), content.getAssigneeName(),
                       content.getAssigneeQualifiedName() );
        addUserValues( contentIndexData, ASSIGNER_FIELDNAME, content.getAssignerKey(), content.getAssignerName(),
                       content.getAssignerQualifiedName() );
    }

    private void addUserValues( ContentIndexData contentIndexData, String prefix, SimpleText key, SimpleText name,
                                SimpleText qualifiedName )
    {
        if ( key == null && name == null && qualifiedName == null )
        {
            return;
        }

        contentIndexData.addContentData( prefix + USER_KEY_POSTFIX, key != null ? key.getText() : null );
        contentIndexData.addContentData( prefix + USER_NAME_POSTFIX, name != null ? name.getText() : null );
        contentIndexData.addContentData( prefix + USER_QUALIFIED_NAME_POSTFIX, qualifiedName != null ? qualifiedName.getText() : null );
    }

    private void addCategory( ContentIndexData contentIndexData, ContentDocument content )
    {
        final CategoryKey categoryKey = content.getCategoryKey();
        if ( categoryKey == null )
        {
            return;
        }
        contentIndexData.addContentData( CATEGORY_KEY_FIELDNAME, categoryKey.toInt() );

        final SimpleText categoryName = content.getCategoryName();
        if ( categoryName == null || StringUtils.isNotBlank( categoryName.getText() ) )
        {
            return;
        }
        contentIndexData.addContentData( CATEGORY_NAME_FIELDNAME, categoryName.getText() );
    }

    private void addContentType( ContentIndexData contentIndexData, ContentDocument content )
    {
        contentIndexData.addContentData( CONTENTTYPE_KEY_FIELDNAME, content.getContentTypeKey().toInt() );
        contentIndexData.addContentData( CONTENTTYPE_NAME_FIELDNAME, content.getContentTypeName().getText() );
    }

    private void addSections( final ContentIndexData contentIndexData, final ContentDocument content )
    {
        sectionFactory.create( contentIndexData, content.getContentLocations() );
    }

    private void addAccessRights( final ContentIndexData contentIndexData, ContentDocument contentDocument )
    {
        final CategoryEntity category = contentDocument.getCategory();
        final Map<GroupKey, CategoryAccessEntity> categoryAccessRights =
            category == null ? Collections.<GroupKey, CategoryAccessEntity>emptyMap() : category.getAccessRights();

        final Collection<ContentAccessEntity> accessRights = contentDocument.getContentAccessRights();
        if ( !accessRights.isEmpty() )
        {
            accessRightsFactory.create( contentIndexData, accessRights, categoryAccessRights );
        }
    }

    private void addCustomData( final ContentIndexData contentIndexData, final ContentDocument contentDocument )
    {
        Collection<UserDefinedField> userDefinedFields = contentDocument.getUserDefinedFields();
        if ( !userDefinedFields.isEmpty() )
        {
            customDataFactory.create( contentIndexData, userDefinedFields );
        }
    }

    private void addExtractedBinaryData( final ContentIndexData contentIndexData, final ContentDocument content )
        throws Exception
    {
        BigText binaryData = content.getBinaryExtractedText();

        if ( binaryData != null && !binaryData.getText().isEmpty() )
        {
            contentIndexData.addBinaryData( "key", content.getContentKey().toInt() );
            contentIndexData.addBinaryData( ATTACHMENT_FIELDNAME, binaryData.getText() );
        }
    }


}
