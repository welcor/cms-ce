/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.search.builder;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.enonic.cms.core.content.access.ContentAccessEntity;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.index.BigText;
import com.enonic.cms.core.content.index.ContentDocument;
import com.enonic.cms.core.content.index.SimpleText;
import com.enonic.cms.core.content.index.UserDefinedField;
import com.enonic.cms.core.search.ContentIndexDataBuilderSpecification;
import com.enonic.cms.core.search.index.ContentIndexData;


public final class ContentIndexDataBuilderImpl
    extends AbstractIndexDataBuilder
    implements ContentIndexDataBuilder
{

    private final ContentIndexDataCustomDataBuilder customDataBuilder = new ContentIndexDataCustomDataBuilder();

    private final ContentIndexDataSectionBuilder sectionBuilder = new ContentIndexDataSectionBuilder();

    private final ContentIndexDataAccessRightsBuilder accessRightsBuilder = new ContentIndexDataAccessRightsBuilder();

    public ContentIndexData build( ContentDocument content, ContentIndexDataBuilderSpecification spec )
    {

        ContentIndexData contentIndexData = null;

        try
        {
            final XContentBuilder contentData = buildContentdata( content );

            contentIndexData = new ContentIndexData( content.getContentKey(), contentData );
        }
        catch ( Exception e )
        {
            throw new ContentIndexDataBuilderException( "Failed to build index-data for content", e );
        }

        if ( spec.doBuildAttachments() )
        {
            try
            {
                contentIndexData.setExtractedBinaryData( buildExtractedBinaryData( content ) );
            }
            catch ( Exception e )
            {
                throw new ContentIndexDataBuilderException( "Failed to build index-data for binaries", e );
            }
        }

        return contentIndexData;
    }

    private XContentBuilder buildContentdata( ContentDocument content )
        throws Exception
    {
        final XContentBuilder result = XContentFactory.jsonBuilder();
        result.startObject();

        addField( "key", new Double( content.getContentKey().toInt() ), result );

        addStandardValues( result, content );

        addCategory( content, result );
        addContentType( content, result );
        addSections( content, result );

        addCustomData( content, result );

        addAccessRights( content, result );

        result.endObject();

        return result;
    }

    private void addAccessRights( ContentDocument contentDocument, XContentBuilder result )
        throws Exception
    {
        Collection<ContentAccessEntity> accessRights = contentDocument.getContentAccessRights();
        if ( !accessRights.isEmpty() )
        {
            accessRightsBuilder.build( result, accessRights );
        }
    }

    private void addSections( ContentDocument content, XContentBuilder result )
        throws Exception
    {
        sectionBuilder.build( content.getContentLocations(), result );
    }

    private void addCustomData( ContentDocument contentDocument, XContentBuilder result )
        throws Exception
    {
        Collection<UserDefinedField> userDefinedFields = contentDocument.getUserDefinedFields();
        if ( !userDefinedFields.isEmpty() )
        {
            customDataBuilder.build( result, userDefinedFields );
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
            addBinaryExtractedData( result, binaryData );
            result.endObject();
            return result;
        }

        return null;
    }

    private void addBinaryExtractedData( XContentBuilder result, BigText contentBinaryData )
        throws Exception
    {

        //TODO: To be implemented when extractor is decided

        /*

        result.startArray( "attachments" );

        for ( ContentBinaryDataEntity data : contentBinaryData )
        {
            AttachmentData attachmentData = attachmentDataExtractor.extractAttachmentValues( data.getBinaryData() );

            if ( attachmentData == null )
            {
                continue;
            }

            result.startObject();

            addField( "attachmentkey", new Double( data.getKey().toInt() ), result, false );
            addField( "author", attachmentData.getAuthor(), result, false );
            addField( "title", attachmentData.getTitle(), result, false );
            addField( "mimetype", attachmentData.getMimetype(), result, false );
            addField( "text", attachmentData.getAttachmentText(), result, false );
            addField( "createddate", attachmentData.getCreationDate(), result, false );
            addField( "filename", attachmentData.getFileName(), result, false );

            result.endObject();
        }

        result.endArray();

        */
    }

    private void addContentType( ContentDocument content, XContentBuilder result )
        throws Exception
    {
        addField( IndexFieldNameResolver.getContentTypeKeyFieldName(), new Double( content.getContentTypeKey().toInt() ), result );
        addField( IndexFieldNameResolver.getContentTypeNameFieldName(), content.getContentTypeName().getText(), result );
    }

    private void addCategory( ContentDocument content, XContentBuilder result )
        throws Exception
    {
        final CategoryKey categoryKey = content.getCategoryKey();

        if ( categoryKey == null )
        {
            // TODO: Is this allowed or illegalArgument?
            return;
        }

        addField( IndexFieldNameResolver.getCategoryKeyFieldName(), categoryKey.toInt(), result );

        final SimpleText categoryName = content.getCategoryName();

        if ( categoryName == null || StringUtils.isNotBlank( categoryName.getText() ) )
        {
            // TODO: Is this allowed or illegalArgument?
            return;
        }

        addField( IndexFieldNameResolver.getCategoryNameFieldName(), categoryName.getText(), result );
    }

    private void addStandardValues( XContentBuilder result, ContentDocument content )
        throws Exception
    {
        addField( TITLE_FIELDNAME, content.getTitle().getText(), result );

        addUserValues( result, OWNER_FIELDNAME, content.getOwnerKey(), content.getOwnerName(), content.getOwnerQualifiedName() );
        addUserValues( result, MODIFIER_FIELDNAME, content.getModifierKey(), content.getModifierName(),
                       content.getModifierQualifiedName() );
        addUserValues( result, ASSIGNEE_FIELDNAME, content.getAssigneeKey(), content.getAssigneeName(),
                       content.getAssigneeQualifiedName() );
        addUserValues( result, ASSIGNER_FIELDNAME, content.getAssignerKey(), content.getAssignerName(),
                       content.getAssignerQualifiedName() );

        addField( PUBLISH_FROM_FIELDNAME, content.getPublishFrom(), result );
        addField( PUBLISH_TO_FIELDNAME, content.getPublishTo(), result );
        addField( TIMESTAMP_FIELDNAME, content.getTimestamp(), result );
        addField( STATUS_FIELDNAME, content.getStatus(), result );
        addField( PRIORITY_FIELDNAME, content.getPriority(), result );
        addField( ASSIGNMENT_DUE_DATE_FIELDNAME, content.getAssignmentDueDate(), result );

        addField( CONTENT_CREATED, content.getCreated(), result );
        addField( CONTENT_MODIFIED, content.getModified(), result );
    }


    private void addUserValues( XContentBuilder result, String prefix, SimpleText key, SimpleText name, SimpleText qualifiedName )
        throws Exception
    {
        if ( key == null && name == null && qualifiedName == null )
        {
            return;
        }

        addField( prefix + USER_KEY_POSTFIX, key != null ? key.getText() : null, result );
        addField( prefix + USER_NAME_POSTFIX, name != null ? name.getText() : null, result );
        addField( prefix + USER_QUALIFIED_NAME_POSTFIX, qualifiedName != null ? qualifiedName.getText() : null, result );

    }

}
