/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.web.main;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.enonic.cms.core.product.LicenseChecker;
import com.enonic.cms.core.product.ProductVersion;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.store.dao.SiteDao;
import com.enonic.cms.upgrade.UpgradeService;

/**
 * Controller for displaying the welcome page, the root page for an installation, listing all sites, plugins, etcs, and linking to DAV,
 * Admin pages, and other information pages.
 */
@Controller
public final class WelcomeController
{
    private UpgradeService upgradeService;

    @Autowired
    private SiteDao siteDao;

    @Autowired(required = false)
    private LicenseChecker licenseChecker;

    @Autowired
    public void setUpgradeService( final UpgradeService upgradeService )
    {
        this.upgradeService = upgradeService;
    }

    private Map<String, Integer> createSiteMap()
    {
        final HashMap<String, Integer> siteMap = new HashMap<String, Integer>();
        for ( final SiteEntity entity : this.siteDao.findAll() )
        {
            siteMap.put( entity.getName(), entity.getKey().toInt() );
        }

        return siteMap;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    protected ModelAndView handle( final HttpServletRequest req )
    {
        final boolean modelUpgradeNeeded = this.upgradeService.needsUpgrade();
        final boolean softwareUpgradeNeeded = this.upgradeService.needsSoftwareUpgrade();
        final boolean upgradeNeeded = modelUpgradeNeeded || softwareUpgradeNeeded;

        HashMap<String, Object> model = new HashMap<String, Object>();
        model.put( "versionTitle", ProductVersion.getFullTitle() );
        model.put( "versionTitleVersion", ProductVersion.getFullTitleAndVersion() );
        model.put( "versionCopyright", ProductVersion.getCopyright() );
        model.put( "baseUrl", createBaseUrl( req ) );
        if ( !upgradeNeeded )
        {
            model.put( "sites", createSiteMap() );
        }
        model.put( "upgradeNeeded", upgradeNeeded );
        model.put( "modelUpgradeNeeded", modelUpgradeNeeded );
        model.put( "softwareUpgradeNeeded", softwareUpgradeNeeded );
        model.put( "upgradeFrom", this.upgradeService.getCurrentModelNumber() );
        model.put( "upgradeTo", this.upgradeService.getTargetModelNumber() );
        model.put( "license", this.licenseChecker );
        return new ModelAndView( "welcomePage", model );
    }

    private String createBaseUrl( final HttpServletRequest req )
    {
        final StringBuilder str = new StringBuilder();
        str.append( req.getScheme() ).append( "://" ).append( req.getServerName() );

        if ( req.getServerPort() != 80 )
        {
            str.append( ":" ).append( req.getServerPort() );
        }

        str.append( req.getContextPath() );
        if ( str.charAt( str.length() - 1 ) == '/' )
        {
            str.deleteCharAt( str.length() - 1 );
        }

        return str.toString();
    }
}
