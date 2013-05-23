package com.enonic.cms.core.resource.xml;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enonic.cms.core.resource.ResourceReferencer;
import com.enonic.cms.core.resource.ResourceReferencerType;
import com.enonic.cms.core.structure.SiteKey;

final class SortBySiteBuilderWrapper
{

    private Collection<ResourceReferencer> usedByCollection = null;

    public SortBySiteBuilderWrapper( Collection<ResourceReferencer> usedByCollection )
    {
        this.usedByCollection = usedByCollection;
    }

    public Map<SiteKey, String> getSites()
    {
        HashMap<SiteKey, String> sites = new HashMap<SiteKey, String>();
        for ( ResourceReferencer ref : usedByCollection )
        {
            if ( ref.getSiteKey() != null && !sites.containsKey( ref.getSiteKey() ) )
            {
                sites.put( ref.getSiteKey(), ref.getSiteName() );
            }
        }
        return sites;
    }

    public List<ResourceReferencer> getContentObjectUsage( SiteKey siteKey )
    {
        List<ResourceReferencer> objects = getReferencer( siteKey, ResourceReferencerType.CONTENT_OBJECT_STYLE );
        objects.addAll( getReferencer( siteKey, ResourceReferencerType.CONTENT_OBJECT_BORDER ) );
        return objects;
    }

    public List<ResourceReferencer> getPageTemplateUsage( SiteKey siteKey )
    {
        List<ResourceReferencer> templates = getReferencer( siteKey, ResourceReferencerType.PAGE_TEMPLATE_STYLE );
        templates.addAll( getReferencer( siteKey, ResourceReferencerType.PAGE_TEMPLATE_CSS ) );
        return templates;
    }

    public List<ResourceReferencer> getContentTypeUsage()
    {
        return getReferencer( null, ResourceReferencerType.CONTENT_TYPE_CSS );
    }

    public List<ResourceReferencer> getSiteUsage()
    {
        return getReferencer( null, ResourceReferencerType.SITE_DEFAULT_CSS );
    }

    private List<ResourceReferencer> getReferencer( SiteKey siteKey, ResourceReferencerType type )
    {
        List<ResourceReferencer> objects = new ArrayList<ResourceReferencer>();
        for ( ResourceReferencer ref : usedByCollection )
        {
            if ( ref.getType().equals( type ) && ( siteKey == null || ref.getSiteKey().equals( siteKey ) ) )
            {
                objects.add( ref );
            }
        }
        return objects;
    }
}