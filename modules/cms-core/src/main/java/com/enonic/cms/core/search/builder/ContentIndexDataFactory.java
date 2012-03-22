/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.search.builder;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.content.access.ContentAccessEntity;
import com.enonic.cms.core.content.category.CategoryAccessEntity;
import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.index.BigText;
import com.enonic.cms.core.content.index.ContentDocument;
import com.enonic.cms.core.content.index.SimpleText;
import com.enonic.cms.core.content.index.UserDefinedField;
import com.enonic.cms.core.search.ContentIndexDataBuilderSpecification;
import com.enonic.cms.core.search.index.ContentIndexData;
import com.enonic.cms.core.security.group.GroupKey;

public final class ContentIndexDataFactory
    extends AbstractIndexDataFactory
{
    private final ContentIndexDataCustomDataFactory customDataBuilder = new ContentIndexDataCustomDataFactory();

    private final ContentIndexDataSectionFactory sectionBuilder = new ContentIndexDataSectionFactory();

    private final ContentIndexDataAccessRightsFactory accessRightsBuilder = new ContentIndexDataAccessRightsFactory();

    public ContentIndexData create( ContentDocument content, ContentIndexDataBuilderSpecification spec )
    {
        ContentIndexData contentIndexData;

        try
        {
            final XContentBuilder contentData = createContentData( content );

            contentIndexData = new ContentIndexData( content.getContentKey(), contentData );
        }
        catch ( Exception e )
        {
            throw new ContentIndexDataFactoryException( "Failed to build index-data for contentdata", e );
        }

        if ( spec.doBuildAttachments() )
        {
            try
            {
                contentIndexData.setBinaryData( buildExtractedBinaryData( content ) );
            }
            catch ( Exception e )
            {
                throw new ContentIndexDataFactoryException( "Failed to build index-data for binaries", e );
            }
        }

        return contentIndexData;
    }

    private XContentBuilder createContentData( ContentDocument content )
        throws Exception
    {
        final XContentBuilder builder = XContentFactory.jsonBuilder();

        builder.startObject();
        addField( "key", new Double( content.getContentKey().toInt() ), builder );
        addMetadata( builder, content );
        addCategory( content, builder );
        addContentType( content, builder );
        addSections( content, builder );
        addCustomData( content, builder );
        addAccessRights( content, builder );
        builder.endObject();

        return builder;
    }

    private void addAccessRights( ContentDocument contentDocument, XContentBuilder builder )
        throws Exception
    {
        final CategoryEntity category = contentDocument.getCategory();
        final Map<GroupKey, CategoryAccessEntity> categoryAccessRights =
            category == null ? Collections.<GroupKey, CategoryAccessEntity>emptyMap() : category.getAccessRights();

        final Collection<ContentAccessEntity> accessRights = contentDocument.getContentAccessRights();
        if ( !accessRights.isEmpty() )
        {
            accessRightsBuilder.build( builder, accessRights, categoryAccessRights );
        }
    }

    private void addSections( ContentDocument content, XContentBuilder builder )
        throws Exception
    {
        sectionBuilder.build( content.getContentLocations(), builder );
    }

    private void addCustomData( ContentDocument contentDocument, XContentBuilder builder )
        throws Exception
    {
        Collection<UserDefinedField> userDefinedFields = contentDocument.getUserDefinedFields();
        if ( !userDefinedFields.isEmpty() )
        {
            customDataBuilder.build( builder, userDefinedFields );
        }

    }

    private XContentBuilder buildExtractedBinaryData( ContentDocument content )
        throws Exception
    {
        BigText binaryData = content.getBinaryExtractedText();

        if ( binaryData != null && !binaryData.getText().isEmpty() )
        {
            final XContentBuilder result = XContentFactory.jsonBuilder();
            result.startObject();
            addField( "key", new Double( content.getContentKey().toInt() ), result, false );
            addField( "attachment", binaryData.getText(), result );
            result.endObject();
            return result;
        }

        return null;
    }


    private void addContentType( ContentDocument content, XContentBuilder builder )
        throws Exception
    {
        addField( IndexFieldNameResolver.getContentTypeKeyFieldName(), new Double( content.getContentTypeKey().toInt() ), builder );
        addField( IndexFieldNameResolver.getContentTypeNameFieldName(), content.getContentTypeName().getText(), builder );
    }

    private void addCategory( ContentDocument content, XContentBuilder builder )
        throws Exception
    {
        final CategoryKey categoryKey = content.getCategoryKey();

        if ( categoryKey == null )
        {
            return;
        }

        addField( IndexFieldNameResolver.getCategoryKeyFieldName(), categoryKey.toInt(), builder );

        final SimpleText categoryName = content.getCategoryName();

        if ( categoryName == null || StringUtils.isNotBlank( categoryName.getText() ) )
        {
            return;
        }

        addField( IndexFieldNameResolver.getCategoryNameFieldName(), categoryName.getText(), builder );
    }

    private void addMetadata( XContentBuilder builder, ContentDocument content )
        throws Exception
    {
        addField( TITLE_FIELDNAME, content.getTitle().getText(), builder );

        addUserValues( builder, OWNER_FIELDNAME, content.getOwnerKey(), content.getOwnerName(), content.getOwnerQualifiedName() );
        addUserValues( builder, MODIFIER_FIELDNAME, content.getModifierKey(), content.getModifierName(),
                       content.getModifierQualifiedName() );
        addUserValues( builder, ASSIGNEE_FIELDNAME, content.getAssigneeKey(), content.getAssigneeName(),
                       content.getAssigneeQualifiedName() );
        addUserValues( builder, ASSIGNER_FIELDNAME, content.getAssignerKey(), content.getAssignerName(),
                       content.getAssignerQualifiedName() );

        addField( PUBLISH_FROM_FIELDNAME, content.getPublishFrom(), builder );
        addField( PUBLISH_TO_FIELDNAME, content.getPublishTo(), builder );
        addField( TIMESTAMP_FIELDNAME, content.getTimestamp(), builder );
        addField( STATUS_FIELDNAME, content.getStatus(), builder );
        addField( PRIORITY_FIELDNAME, content.getPriority(), builder );
        addField( ASSIGNMENT_DUE_DATE_FIELDNAME, content.getAssignmentDueDate(), builder );

        addField( CONTENT_CREATED, content.getCreated(), builder );
        addField( CONTENT_MODIFIED, content.getModified(), builder );
    }


    private void addUserValues( XContentBuilder builder, String prefix, SimpleText key, SimpleText name, SimpleText qualifiedName )
        throws Exception
    {
        if ( key == null && name == null && qualifiedName == null )
        {
            return;
        }

        addField( prefix + USER_KEY_POSTFIX, key != null ? key.getText() : null, builder );
        addField( prefix + USER_NAME_POSTFIX, name != null ? name.getText() : null, builder );
        addField( prefix + USER_QUALIFIED_NAME_POSTFIX, qualifiedName != null ? qualifiedName.getText() : null, builder );

    }

}
