package com.enonic.cms.core.content;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.jdom.Document;

import com.enonic.cms.core.content.access.ContentAccessEntity;
import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.BigText;
import com.enonic.cms.core.content.index.config.IndexDefinition;
import com.enonic.cms.core.content.index.config.IndexDefinitionBuilder;
import com.enonic.cms.core.search.query.ContentDocument;
import com.enonic.cms.core.search.query.SimpleText;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.core.structure.menuitem.section.SectionContentEntity;

public class ContentDocumentFactory
{
    final IndexDefinitionBuilder indexDefBuilder = new IndexDefinitionBuilder();

    final BinaryDataExtractor binaryDataExtractor = new BinaryDataExtractor();

    public ContentDocument createContentDocument( final ContentEntity content, final boolean skipAttachment )
    {
        ContentDocument indexedDoc = insertStandardValues( content );
        insertUserDefinedIndexValues( content, indexedDoc );
        insertOrderedSections( content, indexedDoc );

        if ( !skipAttachment )
        {
            insertBinaryExtractedValues( content, indexedDoc );
        }

        return indexedDoc;
    }

    private void insertBinaryExtractedValues( final ContentEntity content, final ContentDocument indexedDoc )
    {
        final BigText bigText = binaryDataExtractor.extractBinaryData( content );

        if ( bigText != null )
        {
            indexedDoc.setBinaryExtractedText( bigText );
        }
    }

    private void insertOrderedSections( ContentEntity content, ContentDocument indexedDoc )
    {
        final Set<SectionContentEntity> sectionContents = content.getSectionContents();
        for ( SectionContentEntity sectionContent : sectionContents )
        {
            final MenuItemEntity menu = sectionContent.getMenuItem();
            if ( menu.isOrderedSection() )
            {
                final MenuItemKey sectionKey = menu.getKey();
                final int orderPosition = sectionContent.getOrder();
                indexedDoc.addOrderedSection( sectionKey, orderPosition );
            }
        }
    }


    private void insertUserDefinedIndexValues( ContentEntity content, ContentDocument indexedDoc )
    {
        Document doc = content.getMainVersion().getContentDataAsJDomDocument();
        for ( IndexDefinition def : this.indexDefBuilder.buildList( content.getContentType() ) )
        {
            for ( final String stringValue : def.evaluate( doc ) )
            {
                indexedDoc.addUserDefinedField( def.getName(), new SimpleText( stringValue ) );
            }
        }
    }

    private ContentDocument insertStandardValues( ContentEntity content )
    {

        final ContentKey contentKey = content.getKey();
        final ContentTypeEntity contentType = content.getContentType();
        final CategoryEntity category = content.getCategory();

        final ContentVersionEntity contentVersion = content.getMainVersion();
        final UserEntity owner = content.getOwner();
        final UserEntity modifier = contentVersion.getModifiedBy();

        Date createdDate = content.getCreatedAt();
        Date publishFromDate = content.getAvailableFrom();
        Date publishToDate = content.getAvailableTo();

        String title = contentVersion.getTitle();

        ContentDocument indexedDoc = new ContentDocument( contentKey );
        indexedDoc.setCategoryKey( category.getKey() );
        indexedDoc.setContentTypeKey( new ContentTypeKey( contentType.getKey() ) );
        indexedDoc.setContentTypeName( contentType.getName() );
        if ( createdDate != null )
        {
            indexedDoc.setCreated( createdDate );
        }
        indexedDoc.setModifierKey( modifier.getKey().toString() );
        indexedDoc.setModifierName( modifier.getName() );
        indexedDoc.setModifierQualifiedName( modifier.getQualifiedName().toString() );
        indexedDoc.setOwnerKey( owner.getKey().toString() );
        indexedDoc.setOwnerName( owner.getName() );
        indexedDoc.setOwnerQualifiedName( owner.getQualifiedName().toString() );
        if ( content.getAssignee() != null )
        {
            indexedDoc.setAssigneeKey( content.getAssignee().getKey().toString() );
            indexedDoc.setAssigneeName( content.getAssignee().getName() );
            indexedDoc.setAssigneeQualifiedName( content.getAssignee().getQualifiedName().toString() );
        }
        if ( content.getAssigner() != null )
        {
            indexedDoc.setAssignerKey( content.getAssigner().getKey().toString() );
            indexedDoc.setAssignerName( content.getAssigner().getName() );
            indexedDoc.setAssignerQualifiedName( content.getAssigner().getQualifiedName().toString() );
        }
        if ( content.getAssignmentDueDate() != null )
        {
            indexedDoc.setAssignmentDueDate( content.getAssignmentDueDate() );
        }

        if ( publishFromDate != null )
        {
            indexedDoc.setPublishFrom( publishFromDate );
        }
        if ( publishToDate != null )
        {
            indexedDoc.setPublishTo( publishToDate );
        }
        indexedDoc.setTimestamp( content.getTimestamp() );
        indexedDoc.setModified( contentVersion.getModifiedAt() );
        indexedDoc.setTitle( title );
        indexedDoc.setStatus( contentVersion.getStatus().getKey() );
        indexedDoc.setPriority( content.getPriority() );

        ContentLocationSpecification contentLocationSpecification = new ContentLocationSpecification();
        contentLocationSpecification.setIncludeInactiveLocationsInSection( true );
        final ContentLocations contentLocations = content.getLocations( contentLocationSpecification );

        indexedDoc.setContentLocations( contentLocations );

        final Collection<ContentAccessEntity> contentAccessRights = content.getContentAccessRights();
        indexedDoc.addContentAccessRights( contentAccessRights );
        indexedDoc.setCategory( content.getCategory() );

        return indexedDoc;

    }
}