package com.enonic.cms.core.search.builder;

import java.util.Collection;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.enonic.cms.core.content.ContentLocation;
import com.enonic.cms.core.content.ContentLocations;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.index.BigText;
import com.enonic.cms.core.content.index.ContentDocument;
import com.enonic.cms.core.content.index.SimpleText;
import com.enonic.cms.core.content.index.UserDefinedField;
import com.enonic.cms.core.content.index.config.IndexDefinitionBuilder;
import com.enonic.cms.core.search.ContentIndexDataBuilderSpecification;
import com.enonic.cms.core.search.index.ContentIndexData;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/22/11
 * Time: 1:50 PM
 */
public final class ContentIndexDataBuilderImpl
    extends AbstractIndexDataBuilder
    implements ContentIndexDataBuilder
{

    private final IndexDefinitionBuilder indexDefBuilder;

    public ContentIndexDataBuilderImpl()
    {
        this.indexDefBuilder = new IndexDefinitionBuilder();
    }

    public ContentIndexData build( ContentDocument content, ContentIndexDataBuilderSpecification spec )
    {

        ContentIndexData contentIndexData = null;
        try
        {
            contentIndexData = new ContentIndexData( content.getContentKey(), buildMetadata( content ) );
        }
        catch ( Exception e )
        {
            throw new ContentIndexDataBuilderException( "Faild to build index-data for content", e );
        }

        if ( spec.doBuildCustomData() )
        {
            try
            {
                contentIndexData.setCustomdata( buildCustomData( content ) );
            }
            catch ( Exception e )
            {
                throw new ContentIndexDataBuilderException( "Failed to build index-data for content-data", e );
            }
        }

        if ( spec.doBuildAttachments() )
        {
            try
            {
                contentIndexData.setExtractedBinaryData( buildExtractedBinaryData( content ) );
            }
            catch ( Exception e )
            {
                throw new ContentIndexDataBuilderException( "Faild to build index-data for binaries", e );
            }
        }

        return contentIndexData;
    }

    private XContentBuilder buildMetadata( ContentDocument content )
        throws Exception
    {
        final XContentBuilder result = XContentFactory.jsonBuilder();
        result.startObject();
        addField( "key", new Double( content.getContentKey().toInt() ), result );

        addCategory( content, result );
        addContentType( content, result );
        //addSections(content, result);

        addStandardValues( result, content );

        result.endObject();

        return result;
    }

    private XContentBuilder buildCustomData( ContentDocument content )
        throws Exception
    {
        Collection<UserDefinedField> userDefinedFields = content.getUserDefinedFields();

        if ( userDefinedFields.isEmpty() )
        {
            return null;
        }

        final XContentBuilder result = XContentFactory.jsonBuilder();
        result.startObject();
        addField( "key", new Double( content.getContentKey().toInt() ), result );
        addUserDefinedValues( result, userDefinedFields );
        result.endObject();

        return result;
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

        addField( IndexFieldNameResolver.getCategoryKeyFieldName(), new Double( categoryKey.toInt() ), result );

        final SimpleText categoryName = content.getCategoryName();

        if ( categoryName == null )
        {
            return;
        }

        addField( IndexFieldNameResolver.getCategoryNameFieldName(), categoryName.getText(), result );
    }

    private void addSections( ContentDocument content, XContentBuilder result )
        throws Exception
    {
//        final ContentLocations contentLocations = content.getContentLocations();
//
//        if ( !contentLocations.hasLocations() )
//        {
//            return;
//        }
//
//        result.startArray( "contentlocations" );
//
//        for ( final ContentLocation contentLocation : contentLocations.getAllLocations() )
//        {
//            result.startObject();
//            addField( "home", Boolean.toString( contentLocation.isUserDefinedSectionHome() ), result );
//            addField( "menuitemkey", contentLocation.getMenuItemKey().toString(), result );
//            addField( "sitekey", contentLocation.getSiteKey().toString(), result );
//            addField( "menukey", contentLocation.getSiteKey().toString(), result );
//            result.endObject();
//        }
//
//        result.endArray();
    }

    private void addStandardValues( XContentBuilder result, ContentDocument content )
        throws Exception
    {
        addField( "title", content.getTitle().getText(), result );

        addUserValues( result, "owner", content.getOwnerKey(), content.getOwnerName(), content.getOwnerQualifiedName() );
        addUserValues( result, "modifier", content.getModifierKey(), content.getModifierName(), content.getModifierQualifiedName() );
        addUserValues( result, "assignee", content.getAssigneeKey(), content.getAssigneeName(), content.getAssigneeQualifiedName() );
        addUserValues( result, "assigner", content.getAssignerKey(), content.getAssignerName(), content.getAssignerQualifiedName() );

        addField( "publishFrom", content.getPublishFrom(), result );
        addField( "publishTo", content.getPriority(), result );
        addField( "timestamp", content.getTimestamp(), result );
        addField( "status", content.getStatus(), result );
        addField( "priority", content.getPriority(), result );
        addField( "assignmentDueDate", content.getAssignmentDueDate(), result );
    }

    private void addUserValues( XContentBuilder result, String prefix, SimpleText key, SimpleText name, SimpleText qualigiedName )
        throws Exception
    {
        if ( key == null && name == null && qualigiedName == null )
        {
            return;
        }

        addField( prefix + "_key", key != null ? key.getText() : null, result );
        addField( prefix + "_name", name != null ? name.getText() : null, result );
        addField( prefix + "_qualifiedName", qualigiedName != null ? qualigiedName.getText() : null, result );

    }

    private void addUserDefinedValues( final XContentBuilder result, final Collection<UserDefinedField> userDefinedFields )
        throws Exception
    {
        for ( UserDefinedField field : userDefinedFields )
        {
            addField( field.getName(), field.getValue().getText(), result );
        }
    }

}
