/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.resource.ResourceReferencer;
import com.enonic.cms.core.resource.ResourceReferencerType;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.page.template.PageTemplateEntity;
import com.enonic.cms.core.structure.portlet.PortletEntity;

@Repository("resourceUsageDao")
public final class ResourceUsageDaoImpl
    implements ResourceUsageDao
{
    @Autowired
    private PageTemplateDao pageTemplateDao;

    @Autowired
    private PortletDao contentObjectDao;

    @Autowired
    private SiteDao siteDao;

    @Autowired
    private ContentTypeEntityDao contentTypeDao;

    public HashMap<ResourceKey, Long> getUsageCountMap()
    {
        List<Object[]> allUsageCounts = getAllUsageCounts();
        HashMap<ResourceKey, Long> usageCountMap = new HashMap<ResourceKey, Long>();

        for ( Object[] obj : allUsageCounts )
        {
            ResourceKey resourceKey = (ResourceKey) obj[0];
            Long count = (Long) obj[1];

            if ( usageCountMap.containsKey( resourceKey ) )
            {
                count += usageCountMap.get( resourceKey );
            }

            usageCountMap.put( resourceKey, count );
        }
        return usageCountMap;
    }

    private List<Object[]> getAllUsageCounts()
    {
        List<Object[]> allUsageCounts = new ArrayList<Object[]>();

        allUsageCounts.addAll( contentObjectDao.getResourceUsageCountStyle() );
        allUsageCounts.addAll( contentObjectDao.getResourceUsageCountBorder() );
        allUsageCounts.addAll( contentTypeDao.getResourceUsageCountCSS() );
        allUsageCounts.addAll( pageTemplateDao.getResourceUsageCountStyle() );
        allUsageCounts.addAll( pageTemplateDao.getResourceUsageCountCSS() );
        allUsageCounts.addAll( getAllUsedBySites() );

        return allUsageCounts;
    }

    private Multimap<ResourceKey, ResourceReferencer> getUsedBySites( final ResourceKey resourceKey )
    {
        Multimap<ResourceKey, ResourceReferencer> usedBy = HashMultimap.create();

        final List<SiteEntity> sites = siteDao.findAll();

        for ( SiteEntity site : sites )
        {
            ResourceKey defaultCssKey = site.getDefaultCssKey();
            if ( defaultCssKey != null && defaultCssKey.equals( resourceKey ) )
            {
                usedBy.put( resourceKey, new ResourceReferencer( site, ResourceReferencerType.SITE_DEFAULT_CSS ) );
            }

            ResourceKey defaultLocalizationResource = site.getDefaultLocalizationResource();
            if ( defaultLocalizationResource != null && defaultLocalizationResource.equals( resourceKey ) )
            {
                usedBy.put( resourceKey, new ResourceReferencer( site, ResourceReferencerType.SITE_DEFAULT_LOCALIZATION_RESOURCE ) );
            }

            ResourceKey deviceClassResolver = site.getDeviceClassResolver();
            if ( deviceClassResolver != null && deviceClassResolver.equals( resourceKey ) )
            {
                usedBy.put( resourceKey, new ResourceReferencer( site, ResourceReferencerType.SITE_DEVICE_CLASS_RESOLVER ) );
            }

            ResourceKey localeResolver = site.getLocaleResolver();
            if ( localeResolver != null && localeResolver.equals( resourceKey ) )
            {
                usedBy.put( resourceKey, new ResourceReferencer( site, ResourceReferencerType.SITE_LOCALE_RESOLVER ) );
            }
        }

        return usedBy;
    }

    private List<Object[]> getAllUsedBySites()
    {
        List<Object[]> list = new ArrayList<Object[]>();

        final List<SiteEntity> sites = siteDao.findAll();

        for ( SiteEntity site : sites )
        {
            ResourceKey defaultCssKey = site.getDefaultCssKey();
            if ( defaultCssKey != null )
            {
                list.add( new Object[]{defaultCssKey, 1L} );
            }

            ResourceKey defaultLocalizationResource = site.getDefaultLocalizationResource();
            if ( defaultLocalizationResource != null )
            {
                list.add( new Object[]{defaultLocalizationResource, 1L} );
            }

            ResourceKey deviceClassResolver = site.getDeviceClassResolver();
            if ( deviceClassResolver != null )
            {
                list.add( new Object[]{deviceClassResolver, 1L} );
            }

            ResourceKey localeResolver = site.getLocaleResolver();
            if ( localeResolver != null )
            {
                list.add( new Object[]{localeResolver, 1L} );
            }
        }

        return list;
    }

    public Multimap<ResourceKey, ResourceReferencer> getUsedBy( ResourceKey resourceKey )
    {

        Multimap<ResourceKey, ResourceReferencer> usedBy = HashMultimap.create();

        for ( PortletEntity obj : contentObjectDao.findByStyle( resourceKey ) )
        {
            usedBy.put( resourceKey, new ResourceReferencer( obj, ResourceReferencerType.CONTENT_OBJECT_STYLE ) );
        }
        for ( PortletEntity obj : contentObjectDao.findByBorder( resourceKey ) )
        {
            usedBy.put( resourceKey, new ResourceReferencer( obj, ResourceReferencerType.CONTENT_OBJECT_BORDER ) );
        }
        for ( ContentTypeEntity obj : contentTypeDao.findByCSS( resourceKey ) )
        {
            usedBy.put( resourceKey, new ResourceReferencer( obj, ResourceReferencerType.CONTENT_TYPE_CSS ) );
        }
        for ( PageTemplateEntity obj : pageTemplateDao.findByStyle( resourceKey ) )
        {
            usedBy.put( resourceKey, new ResourceReferencer( obj, ResourceReferencerType.PAGE_TEMPLATE_STYLE ) );
        }
        for ( PageTemplateEntity obj : pageTemplateDao.findByCSS( resourceKey ) )
        {
            usedBy.put( resourceKey, new ResourceReferencer( obj, ResourceReferencerType.PAGE_TEMPLATE_CSS ) );
        }

        usedBy.putAll( getUsedBySites( resourceKey ) );

        return usedBy;
    }

    public void updateResourceReference( ResourceKey oldResourceKey, ResourceKey newResourceKey )
    {
        contentObjectDao.updateResourceStyleReference( oldResourceKey, newResourceKey );
        contentObjectDao.updateResourceBorderReference( oldResourceKey, newResourceKey );
        contentTypeDao.updateResourceCSSReference( oldResourceKey, newResourceKey );
        pageTemplateDao.updateResourceStyleReference( oldResourceKey, newResourceKey );
        pageTemplateDao.updateResourceCSSReference( oldResourceKey, newResourceKey );
        siteDao.updateResourceCSSReference( oldResourceKey, newResourceKey );
    }

    public void updateResourceReferencePrefix( String oldPrefix, String newPrefix )
    {
        contentObjectDao.updateResourceStyleReferencePrefix( oldPrefix, newPrefix );
        contentObjectDao.updateResourceBorderReferencePrefix( oldPrefix, newPrefix );
        contentTypeDao.updateResourceCSSReferencePrefix( oldPrefix, newPrefix );
        pageTemplateDao.updateResourceStyleReferencePrefix( oldPrefix, newPrefix );
        pageTemplateDao.updateResourceCSSReferencePrefix( oldPrefix, newPrefix );
        siteDao.updateResourceCSSReferencePrefix( oldPrefix, newPrefix );
    }
}
