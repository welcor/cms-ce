/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.web.portal.resource;

import java.util.HashMap;

import org.junit.Test;

import com.enonic.cms.core.Path;
import com.enonic.cms.core.structure.SiteKey;
import com.enonic.cms.core.structure.SitePath;

import static org.junit.Assert.*;

public class ResourceKeyResolverTest
{
    @Test
    public void testResolveWithPublicHomeSetTildeAtBegining()
    {
        ResourceKeyResolver resolver = new ResourceKeyResolver( "/_public/Drumming Africa" );

        assertEquals( "/_public/Drumming Africa/images/logo.gif", resolver.resolveResourceKey(
            new SitePath( new SiteKey( 1 ), new Path( "/~/images/logo.gif" ), new HashMap<String, String[]>() ) ).toString() );
    }

    @Test
    public void testResolveWithPublicHomeSetTildeInTheMiddle()
    {
        ResourceKeyResolver resolver = new ResourceKeyResolver( "/_public/Drumming Africa" );

        assertEquals( "/_public/Drumming Africa/images/logo.gif", resolver.resolveResourceKey(
            new SitePath( new SiteKey( 1 ), new Path( "/_public/shared/styles/~/images/logo.gif" ),
                          new HashMap<String, String[]>() ) ).toString() );
    }


}
