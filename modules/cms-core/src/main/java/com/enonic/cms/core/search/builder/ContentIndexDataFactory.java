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
import com.enonic.cms.core.content.index.UserDefinedField;
import com.enonic.cms.core.search.query.ContentDocument;
import com.enonic.cms.core.search.query.SimpleText;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;

public class ContentIndexDataFactory
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
        return doCreate( content, false );
    }

    private ContentIndexData doCreate( final ContentDocument content, boolean skipAttachments )
    {
        ContentIndexData contentIndexData = new ContentIndexData( content.getContentKey() );

        addMetaData( contentIndexData, content );
        addCategory( contentIndexData, content );
        addContentType( contentIndexData, content );
        addSections( contentIndexData, content );
        addOrderedSections( contentIndexData, content );
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
        contentIndexData.addContentIndexDataElement( CONTENT_KEY_FIELDNAME, content.getContentKey().toInt() );
        contentIndexData.addContentIndexDataElement( TITLE_FIELDNAME, content.getTitle() );
        contentIndexData.addContentIndexDataElement( TIMESTAMP_FIELDNAME, content.getTimestamp() );
        contentIndexData.addContentIndexDataElement( PUBLISH_FROM_FIELDNAME, content.getPublishFrom() );
        contentIndexData.addContentIndexDataElement( PUBLISH_TO_FIELDNAME, content.getPublishTo() );
        contentIndexData.addContentIndexDataElement( TIMESTAMP_FIELDNAME, content.getTimestamp() );
        contentIndexData.addContentIndexDataElement( STATUS_FIELDNAME, content.getStatus() );
        contentIndexData.addContentIndexDataElement( PRIORITY_FIELDNAME, content.getPriority() );
        contentIndexData.addContentIndexDataElement( LANGUAGE_FIELDNAME, content.getLanguageCode() );
        contentIndexData.addContentIndexDataElement( ASSIGNMENT_DUE_DATE_FIELDNAME, content.getAssignmentDueDate() );
        contentIndexData.addContentIndexDataElement( CONTENT_CREATED, content.getCreated() );
        contentIndexData.addContentIndexDataElement( CONTENT_MODIFIED, content.getModified() );

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

        contentIndexData.addContentIndexDataElement( prefix + USER_KEY_POSTFIX, key != null ? key.getText() : null );
        contentIndexData.addContentIndexDataElement( prefix + USER_NAME_POSTFIX, name != null ? name.getText() : null );
        contentIndexData.addContentIndexDataElement( prefix + USER_QUALIFIED_NAME_POSTFIX,
                                                     qualifiedName != null ? qualifiedName.getText() : null );
    }

    private void addCategory( ContentIndexData contentIndexData, ContentDocument content )
    {
        final CategoryKey categoryKey = content.getCategoryKey();
        if ( categoryKey == null )
        {
            return;
        }
        contentIndexData.addContentIndexDataElement( CATEGORY_KEY_FIELDNAME, categoryKey.toInt() );

        final SimpleText categoryName = content.getCategoryName();
        if ( categoryName == null || StringUtils.isNotBlank( categoryName.getText() ) )
        {
            return;
        }
        contentIndexData.addContentIndexDataElement( CATEGORY_NAME_FIELDNAME, categoryName.getText() );
    }

    private void addContentType( ContentIndexData contentIndexData, ContentDocument content )
    {
        contentIndexData.addContentIndexDataElement( CONTENTTYPE_KEY_FIELDNAME, content.getContentTypeKey().toInt() );
        contentIndexData.addContentIndexDataElement( CONTENTTYPE_NAME_FIELDNAME, content.getContentTypeName().getText() );
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
        if ( !( accessRights.isEmpty() && categoryAccessRights.isEmpty() ) )
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
    {
        BigText binaryData = content.getBinaryExtractedText();

        if ( binaryData != null && !binaryData.getText().isEmpty() )
        {
            contentIndexData.addBinaryData( "key", content.getContentKey().toInt() );
            contentIndexData.addBinaryData( ATTACHMENT_FIELDNAME, binaryData.getText() );
        }
    }

    private void addOrderedSections( ContentIndexData contentIndexData, ContentDocument content )
    {
        final Map<MenuItemKey, Integer> orderedSections = content.getOrderedSections();
        for ( MenuItemKey sectionKey : orderedSections.keySet() )
        {
            final int position = orderedSections.get( sectionKey );
            contentIndexData.addContentIndexDataElement( CONTENT_SECTION_ORDER_PREFIX + sectionKey.toString(), position );
        }
    }

}
