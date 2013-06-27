/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.web.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.enonic.cms.core.product.LicenseChecker;
import com.enonic.cms.core.product.NopLicenseChecker;
import com.enonic.cms.core.product.ProductVersion;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.store.dao.SiteDao;
import com.enonic.cms.upgrade.UpgradeService;

/**
 * Controller for displaying the welcome page, the root page for an installation.
 */
@Controller
public final class WelcomeController
{
    private UpgradeService upgradeService;

    private SiteDao siteDao;

    private LicenseChecker licenseChecker;

    @Autowired
    public void setUpgradeService( final UpgradeService upgradeService )
    {
        this.upgradeService = upgradeService;
    }

    @Autowired
    public void setSiteDao( final SiteDao siteDao )
    {
        this.siteDao = siteDao;
    }

    @Autowired(required = false)
    public void setLicenseChecker( final LicenseChecker licenseChecker )
    {
        this.licenseChecker = licenseChecker;
    }

    private List<Site> createSiteList()
    {
        final List<Site> sites = new ArrayList<Site>();
        for ( final SiteEntity entity : this.siteDao.findAll() )
        {
            final String code = entity.getLanguage() == null ? "" : " (" + entity.getLanguage().getCode() + ")";
            sites.add( new Site( entity.getName() + code, entity.getKey().toInt() ) );
        }

        return sites;
    }

    public class Site
    {
        private String displayString;

        private int key;

        public Site( final String displayString, final int key )
        {
            this.displayString = displayString;
            this.key = key;
        }

        public String getDisplayString()
        {
            return displayString;
        }

        public int getKey()
        {
            return key;
        }
    }

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView welcomePage( final HttpServletRequest req )
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
            model.put( "sites", createSiteList() );
        }
        model.put( "upgradeNeeded", upgradeNeeded );
        model.put( "modelUpgradeNeeded", modelUpgradeNeeded );
        model.put( "softwareUpgradeNeeded", softwareUpgradeNeeded );
        model.put( "upgradeFrom", this.upgradeService.getCurrentModelNumber() );
        model.put( "upgradeTo", this.upgradeService.getTargetModelNumber() );

        if ( this.licenseChecker == null )
        {
            this.licenseChecker = new NopLicenseChecker();
        }

        model.put( "license", this.licenseChecker );
        return new ModelAndView( "welcomePage", model );
    }

    private String createBaseUrl( final HttpServletRequest req )
    {
        final String url = ServletUriComponentsBuilder.fromRequest( req ).build().toString();
        if ( url.endsWith( "/" ) )
        {
            return url.substring( 0, url.length() - 1 );
        }
        else
        {
            return url;
        }
    }
}
