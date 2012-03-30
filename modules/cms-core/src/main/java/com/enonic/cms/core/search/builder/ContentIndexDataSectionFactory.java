package com.enonic.cms.core.search.builder;

import java.util.Set;

import org.elasticsearch.common.xcontent.XContentBuilder;

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
    extends AbstractIndexDataFactory
{

    public void build( final ContentLocations contentLocations, final XContentBuilder result )
        throws Exception
    {

        if ( contentLocations == null || !contentLocations.hasLocations() )
        {
            return;
        }

        Set<Integer> sectionKeysApproved = Sets.newTreeSet();
        Set<Integer> sectionKeysUnapproved = Sets.newTreeSet();

        for ( final ContentLocation contentLocation : contentLocations.getAllLocations() )
        {
            if ( !contentLocation.isInSection() )
            {
                continue;
            }

            final int menuKey = contentLocation.getMenuItemKey().toInt();

            if ( contentLocation.isApproved() )
            {
                sectionKeysApproved.add( menuKey );
            }
            else
            {
                sectionKeysUnapproved.add( menuKey );
            }
        }

        addNumericSet( CONTENTLOCATION_APPROVED_FIELDNAME, sectionKeysApproved, result, false );
        addNumericSet( CONTENTLOCATION_UNAPPROVED_FIELDNAME, sectionKeysUnapproved, result, false );
    }
}

