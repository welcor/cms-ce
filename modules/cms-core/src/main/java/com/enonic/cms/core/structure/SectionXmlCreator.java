/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.structure;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentLocation;
import com.enonic.cms.core.content.ContentLocationSpecification;
import com.enonic.cms.core.content.ContentLocations;
import com.enonic.cms.core.content.ContentXMLCreator;
import com.enonic.cms.core.content.access.ContentAccessResolver;
import com.enonic.cms.core.content.category.CategoryAccessResolver;
import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.store.dao.SiteDao;

public class SectionXmlCreator
{
    private SiteDao siteDao;

    private ContentXMLCreator contentXMLCreator = new ContentXMLCreator();

    public SectionXmlCreator( SiteDao siteDao, CategoryAccessResolver categoryAccessResolver, ContentAccessResolver contentAccessResolver )
    {
        this.siteDao = siteDao;
        contentXMLCreator.setIncludeAccessRightsInfo( false );
        contentXMLCreator.setIncludeRelatedContentsInfo( false );
        contentXMLCreator.setIncludeSectionActivationInfo( true );
        contentXMLCreator.setIncludeRepositoryPathInfo( true );
        contentXMLCreator.setIncludeUserRightsInfo( true, categoryAccessResolver, contentAccessResolver );
        contentXMLCreator.setIncludeVersionsInfoForAdmin( false );
        contentXMLCreator.setIncludeOwnerAndModifierData( false );
        contentXMLCreator.setIncludeDraftInfo( true );
        contentXMLCreator.setIncludeContentData( false );
        contentXMLCreator.setIncludeCategoryData( false );
    }

    public XMLDocument createSectionsDocument( UserEntity runningUser, ContentResultSet contentResultSet, final int maxCount )
    {
        final Element sectionsEl = new Element( "sections" );
        final List<Section> sectionNameList = getUniqueSectionsSorted( contentResultSet );

        sectionsEl.setAttribute( "count", String.valueOf( sectionNameList.size() ) );
        sectionsEl.setAttribute( "contenttotalcount", String.valueOf( contentResultSet.getTotalCount() ) );

        int totalCount = 0;

        for ( final Section section : sectionNameList )
        {
            final List<ContentEntity> contentList = section.getContentList();

            if ( totalCount <= maxCount )
            {
                final Element sectionEl = new Element( "section" );

                sectionEl.setAttribute( "sitekey", section.getSiteKey().toString() );
                sectionEl.setAttribute( "sitename", section.getSiteName() );
                sectionEl.setAttribute( "menuitemkey", section.getMenuItemKey().toString() );
                sectionEl.setAttribute( "name", section.getMenuItemName() );
                sectionEl.setAttribute( "path", section.getMenuItemPath() );

                for ( final ContentEntity content : contentList )
                {
                    final Element contentEl = contentXMLCreator.createSingleContentVersionElement( runningUser, content.getMainVersion() );
                    sectionEl.addContent( contentEl );
                }

                sectionEl.setAttribute( "sectioncount", String.valueOf( contentList.size() ) );

                sectionsEl.addContent( sectionEl );
            }

            totalCount += contentList.size();
        }

        sectionsEl.setAttribute( "contentcount", String.valueOf( contentResultSet.getLength() ) );
        sectionsEl.setAttribute( "contentinsectioncount", String.valueOf( totalCount ) );

        return XMLDocumentFactory.create( new Document( sectionsEl ) );
    }

    private List<Section> getUniqueSectionsSorted( ContentResultSet contentResultSet )
    {
        final Map<Section, Section> uniqueSectionNames = new HashMap<Section, Section>();

        for ( final ContentEntity content : contentResultSet.getContents() )
        {
            final ContentLocationSpecification contentLocationSpecification = new ContentLocationSpecification();
            contentLocationSpecification.setIncludeInactiveLocationsInSection( true );

            final ContentLocations contentLocations = content.getLocations( contentLocationSpecification );
            final Iterable<ContentLocation> allLocations = contentLocations.getAllLocations();

            for ( final ContentLocation contentLocation : allLocations )
            {
                // filter items that are not the section
                if ( !contentLocation.isInSection() )
                {
                    continue;
                }

                // filter activated='false' here instead of doing this in XSL
                if ( contentLocation.isApproved() )
                {
                    continue;
                }

                final Section section =
                    new Section( contentLocation.getSiteKey(), contentLocation.getMenuItemKey(), contentLocation.getMenuItemName(),
                                 contentLocation.getMenuItemPathAsString(), null );

                final Section existingSection = uniqueSectionNames.get( section );

                if ( existingSection == null )
                {
                    uniqueSectionNames.put( section, section );

                    final SiteEntity siteEntity = siteDao.findByKey( contentLocation.getSiteKey().toInt() );
                    section.setSiteName( siteEntity.getName() );

                    section.addContent( content );
                }
                else
                {
                    // filter here instead of doing this in XSL ( descendant::contentlocation[@activated = 'false' and @menuitemkey = ../../../../@menuitemkey])
                    if ( existingSection.getMenuItemKey().equals( contentLocation.getMenuItemKey() ) )
                    {
                        existingSection.addContent( content );
                    }
                }
            }
        }

        final Section[] sectionNameArray = new Section[uniqueSectionNames.size()];
        uniqueSectionNames.keySet().toArray( sectionNameArray );
        final List<Section> sectionNameList = Arrays.asList( sectionNameArray );

        Collections.sort( sectionNameList, new CaseInsensitiveSectionComparator() );
        return sectionNameList;
    }

}
