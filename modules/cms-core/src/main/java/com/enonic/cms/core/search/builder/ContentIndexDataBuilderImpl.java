package com.enonic.cms.core.search.builder;

import java.util.List;
import java.util.Set;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.jdom.Document;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentLocation;
import com.enonic.cms.core.content.ContentLocationSpecification;
import com.enonic.cms.core.content.ContentLocations;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.binary.ContentBinaryDataEntity;
import com.enonic.cms.core.content.index.config.IndexDefinition;
import com.enonic.cms.core.content.index.config.IndexDefinitionBuilder;
import com.enonic.cms.core.search.ContentIndexDataBuilderSpecification;
import com.enonic.cms.core.search.index.ContentIndexData;
import com.enonic.cms.core.security.user.UserEntity;

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

    public ContentIndexData build( ContentEntity entity, ContentIndexDataBuilderSpecification indexDataBuilderSpecification )
        throws Exception
    {
        ContentIndexData contentIndexData = new ContentIndexData( entity.getKey(), buildMetadata( entity ) );

        if ( indexDataBuilderSpecification.doBuildCustomData() )
        {
            contentIndexData.setCustomdata( buildCustomData( entity ) );
        }

        if ( indexDataBuilderSpecification.doBuildAttachments() )
        {
            contentIndexData.setExtractedBinaryData( buildExtractedBinaryData( entity ) );
        }

        return contentIndexData;
    }

    private XContentBuilder buildExtractedBinaryData( ContentEntity entity )
        throws Exception
    {

        final ContentVersionEntity version = entity.getMainVersion();
        final Set<ContentBinaryDataEntity> binaryData = version.getContentBinaryData();

        if ( binaryData != null && !binaryData.isEmpty() )
        {

            final XContentBuilder result = XContentFactory.jsonBuilder();
            result.startObject();
            addField( "key", new Double( entity.getKey().toInt() ), result, false );
            addAttachmentValues( result, binaryData );
            result.endObject();
            return result;
        }

        return null;
    }

    private XContentBuilder buildMetadata( ContentEntity entity )
        throws Exception
    {
        final XContentBuilder result = XContentFactory.jsonBuilder();
        result.startObject();
        addField( "key", new Double( entity.getKey().toInt() ), result );

        addCategory( entity, result );
        addContentType( entity, result );
        addSections( entity, result );

        addStandardValues( result, entity );

        result.endObject();

        return result;
    }

    private XContentBuilder buildCustomData( ContentEntity entity )
        throws Exception
    {
        final List<IndexDefinition> indexDefinitions = this.indexDefBuilder.buildList( entity.getContentType() );

        if ( indexDefinitions.isEmpty() )
        {
            return null;
        }

        final XContentBuilder result = XContentFactory.jsonBuilder();
        result.startObject();
        addField( "key", new Double( entity.getKey().toInt() ), result );
        addUserDefinedValues( result, entity, indexDefinitions );
        result.endObject();

        return result;
    }

    private void addAttachmentValues( XContentBuilder result, Set<ContentBinaryDataEntity> contentBinaryData )
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


    private void addContentType( ContentEntity entity, XContentBuilder result )
        throws Exception
    {
        //addField( QueryFieldNameResolver.getContentTypeKeyFieldName(), new Double( entity.getContentType().getKey() ), result );
        //addField( QueryFieldNameResolver.getContentTypeNameFieldName(), entity.getContentType().getName(), result );
    }

    private void addCategory( ContentEntity entity, XContentBuilder result )
        throws Exception
    {
       // addField( QueryFieldNameResolver.getCategoryKeyFieldName(), new Double( entity.getCategory().getKey().toInt() ), result );
       // addField( QueryFieldNameResolver.getCategoryNameFieldName(), entity.getCategory().getName(), result );
    }

    private void addSections( ContentEntity content, XContentBuilder result )
        throws Exception
    {
        ContentLocationSpecification contentLocationSpecification = new ContentLocationSpecification();
        contentLocationSpecification.setIncludeInactiveLocationsInSection( true );
        final ContentLocations contentLocations = content.getLocations( contentLocationSpecification );

        if ( !contentLocations.hasLocations() )
        {
            return;
        }

        result.startArray( "contentlocations" );

        for ( final ContentLocation contentLocation : contentLocations.getAllLocations() )
        {
            result.startObject();
            addField( "home", Boolean.toString( contentLocation.isUserDefinedSectionHome() ), result );
            addField( "menuitemkey", contentLocation.getMenuItemKey().toString(), result );
            addField( "sitekey", contentLocation.getSiteKey().toString(), result );
            addField( "menukey", contentLocation.getSiteKey().toString(), result );
            result.endObject();
        }

        result.endArray();
    }

    private void addStandardValues( XContentBuilder result, ContentEntity entity )
        throws Exception
    {
        final ContentVersionEntity version = entity.getMainVersion();

        addField( "title", version.getTitle(), result );

        addUserValues( result, "owner", entity.getOwner() );
        addUserValues( result, "modifier", version.getModifiedBy() );
        addUserValues( result, "assignee", entity.getAssignee() );
        addUserValues( result, "assigner", entity.getAssigner() );

        addField( "publishFrom", entity.getAvailableFrom(), result );
        addField( "publishTo", entity.getAvailableTo(), result );
        addField( "timestamp", version.getModifiedAt(), result );
        addField( "status", version.getStatus().getKey(), result );
        addField( "priority", entity.getPriority(), result );
        addField( "assignmentDueDate", entity.getAssignmentDueDate(), result );

    }

    private void addUserValues( XContentBuilder result, String prefix, UserEntity entity )
        throws Exception
    {
        if ( entity == null )
        {
            return;
        }

        addField( prefix + "_key", entity.getKey().toString(), result );
        addField( prefix + "_name", entity.getName(), result );
        addField( prefix + "_qualifiedName", entity.getQualifiedName().toString(), result );

    }

    private void addUserDefinedValues( final XContentBuilder result, final ContentEntity entity,
                                       final List<IndexDefinition> indexDefinitions )
        throws Exception
    {
        final Document doc = entity.getMainVersion().getContentDataAsJDomDocument();

        for ( IndexDefinition def : indexDefinitions )
        {
            for ( final String stringValue : def.evaluate( doc ) )
            {
                addField( def.getName(), stringValue, result );
            }
        }

    }

}
