/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.builder;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.cms.core.content.ContentLocation;
import com.enonic.cms.core.content.ContentLocations;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/2/12
 * Time: 1:49 PM
 */
public class ContentIndexDataSectionFactory
    extends IndexFieldNameConstants
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

            final String menuKey = contentLocation.getMenuItemKey().toString();

            if ( contentLocation.isApproved() )
            {
                sectionKeysApproved.add( menuKey );
            }
            else
            {
                sectionKeysUnapproved.add( menuKey );
            }
        }

        contentIndexData.addContentIndexDataElement( CONTENTLOCATION_APPROVED_FIELDNAME, sectionKeysApproved );
        contentIndexData.addContentIndexDataElement( CONTENTLOCATION_UNAPPROVED_FIELDNAME, sectionKeysUnapproved );
    }
}

