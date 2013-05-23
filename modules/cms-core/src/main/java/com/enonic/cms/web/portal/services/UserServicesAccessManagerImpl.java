/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.web.portal.services;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.structure.SiteKey;
import com.enonic.cms.core.structure.SiteProperties;
import com.enonic.cms.core.structure.SitePropertiesListener;
import com.enonic.cms.core.structure.SitePropertiesService;
import com.enonic.cms.core.structure.SiteService;

@Component
public class UserServicesAccessManagerImpl
    implements UserServicesAccessManager, SitePropertiesListener
{

    private static enum AccessPermission
    {
        ALLOW,
        DENY,
    }

    private static final String HTTP_SERVICES_ALLOW_PROPERTY = "cms.site.httpServices.allow";

    private static final String HTTP_SERVICES_DENY_PROPERTY = "cms.site.httpServices.deny";

    private static final String ACCESS_RULE_ALL = "*";

    private static final AccessPermission DEFAULT_ACCESS_RULE = AccessPermission.DENY;

    private SitePropertiesService sitePropertiesService;

    private SiteService siteService;

    private ConcurrentMap<SiteKey, ConcurrentMap<String, AccessPermission>> sitesAccessRules;

    public UserServicesAccessManagerImpl()
    {
        sitesAccessRules = new ConcurrentHashMap<SiteKey, ConcurrentMap<String, AccessPermission>>();
    }

    @PostConstruct
    public void postConstruct()
    {
        sitePropertiesService.registerSitePropertiesListener( this );
    }

    @Override
    public boolean isOperationAllowed( SiteKey site, String service, String operation )
    {
        siteService.checkSiteExist( site );

        ConcurrentMap<String, AccessPermission> siteRules = getRulesForSite( site );
        AccessPermission access = applyAccessRules( service, operation, siteRules );

        return access == AccessPermission.ALLOW;
    }

    private AccessPermission applyAccessRules( String service, String operation, ConcurrentMap<String, AccessPermission> siteRules )
    {
        // search for specific rule: "service.operation"
        AccessPermission accessServiceOperation = siteRules.get( service + "." + operation );
        if ( accessServiceOperation != null )
        {
            return accessServiceOperation;
        }

        // search for generic rule: "service.*"
        AccessPermission accessService = siteRules.get( service + ".*" );
        if ( accessService != null )
        {
            siteRules.putIfAbsent( service + "." + operation, accessService );
            return accessService;
        }

        // no rule found -> return default and cache value
        AccessPermission defaultAccess = siteRules.get( ACCESS_RULE_ALL );
        siteRules.putIfAbsent( service + "." + operation, defaultAccess );
        return defaultAccess;
    }

    private ConcurrentMap<String, AccessPermission> getRulesForSite( SiteKey site )
    {
        ConcurrentMap<String, AccessPermission> rules = sitesAccessRules.get( site );
        if ( rules == null )
        {
            initSiteRules( site );
            rules = sitesAccessRules.get( site );
        }
        return rules;
    }

    @Override
    public void sitePropertiesLoaded( final SiteProperties siteProperties )
    {
        // nothing
    }

    @Override
    public void sitePropertiesReloaded( final SiteProperties siteProperties )
    {
        sitesAccessRules.remove( siteProperties.getSiteKey() );
        initSiteRules( siteProperties.getSiteKey() );
    }

    private void initSiteRules( SiteKey site )
    {
        ConcurrentMap<String, AccessPermission> siteRules = new ConcurrentHashMap<String, AccessPermission>();

        final SiteProperties siteProperties = sitePropertiesService.getSiteProperties( site );

        String allowRules = siteProperties.getProperty( HTTP_SERVICES_ALLOW_PROPERTY );
        String denyRules = siteProperties.getProperty( HTTP_SERVICES_DENY_PROPERTY );
        parseAndAddRules( allowRules, AccessPermission.ALLOW, siteRules, site );
        parseAndAddRules( denyRules, AccessPermission.DENY, siteRules, site );

        siteRules.putIfAbsent( ACCESS_RULE_ALL, DEFAULT_ACCESS_RULE );

        sitesAccessRules.putIfAbsent( site, siteRules );
    }

    private void parseAndAddRules( String accessRules, AccessPermission accessPermission, ConcurrentMap<String, AccessPermission> siteRules,
                                   SiteKey site )
    {
        accessRules = StringUtils.trimToEmpty( accessRules );
        String[] ruleItems = accessRules.split( "," );
        for ( String ruleItem : ruleItems )
        {
            ruleItem = ruleItem.trim();
            if ( ruleItem.isEmpty() )
            {
                continue;
            }
            if ( siteRules.containsKey( ruleItem ) )
            {
                throw new IllegalArgumentException( "Duplicated value for http service access rule '" + ruleItem + "' on site " + site );
            }
            siteRules.put( ruleItem, accessPermission );
        }
    }

    @Autowired
    public void setSitePropertiesService( SitePropertiesService sitePropertiesService )
    {
        this.sitePropertiesService = sitePropertiesService;
    }

    @Autowired
    public void setSiteService( SiteService siteService )
    {
        this.siteService = siteService;
    }

}

