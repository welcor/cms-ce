package com.enonic.cms.core.search.builder;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.cms.core.content.ContentLocation;
import com.enonic.cms.core.content.ContentLocations;
import com.enonic.cms.core.search.builder.indexdata.ContentIndexData;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/2/12
 * Time: 1:49 PM
 */
public class ContentIndexDataSectionFactory
    extends AbstractIndexDataFactory
{

    public void create( final ContentIndexData contentIndexData, final ContentLocations contentLocations )
    {

        if ( contentLocations == null || !contentLocations.hasLocations() )
        {
            return;
        }

        Set<Object> sectionKeysApproved = Sets.newHashSet();
        Set<Object> sectionKeysUnapproved = Sets.newHashSet();

        for ( final ContentLocation contentLocation : contentLocations.getAllLocations() )
        {
            if ( !contentLocation.isInSection() )
            {
                continue;
            }

            final Double menuKey = new Double( contentLocation.getMenuItemKey().toInt() );

            if ( contentLocation.isApproved() )
            {
                sectionKeysApproved.add( menuKey );
            }
            else
            {
                sectionKeysUnapproved.add( menuKey );
            }
        }

        contentIndexData.addContentData( CONTENTLOCATION_APPROVED_FIELDNAME, sectionKeysApproved );
        contentIndexData.addContentData( CONTENTLOCATION_UNAPPROVED_FIELDNAME, sectionKeysUnapproved );
    }
}

