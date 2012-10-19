package com.enonic.vertical.adminweb;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.*;

import com.enonic.cms.core.resource.ResourceKey;

public class ResourceHandlerServletTest
{

    private ResourceHandlerServlet servlet;

    @Before
    public void setUp()
        throws Exception
    {
        servlet = new ResourceHandlerServlet();
    }

    /*
     * Case:
     *      source:      "/libraries/resolvers"
     *      destination: "/sites/stuff"
     *  we move folder "resolvers"
     *      result: "/sites/stuff/resolvers"
     */
    @Test
    public void testResolvePathForNewFolderComputePath()
    {

        ResourceKey sourceFolderPath = ResourceKey.from( "/libraries/resolvers" );
        ResourceKey destinationFolderPath = ResourceKey.from( "/sites/stuff" );

        String newPath = servlet.resolvePathForNewFolder( sourceFolderPath, destinationFolderPath );
        assertEquals( "/sites/stuff/resolvers", newPath );
    }

    /*
     * Case:
     *      source:      "/libraries/resolvers"
     *      destination: "/"
     *  we move folder "resolvers"
     *      result: "/resolvers"
     */
    @Test
    public void testResolvePathForNewFolderRootFolder()
    {

        ResourceKey sourceFolderPath = ResourceKey.from( "/libraries/resolvers" );
        ResourceKey destinationFolderPath = ResourceKey.from( "/" );

        String newPath = servlet.resolvePathForNewFolder( sourceFolderPath, destinationFolderPath );
        assertEquals( "/resolvers", newPath );
    }

    @Test
    public void testResolvePathForNewFolder()
    {

        ResourceKey sourceFolderPath = ResourceKey.from( "/resolvers" );
        ResourceKey destinationFolderPath = ResourceKey.from( "/sites/stuff" );

        String newPath = servlet.resolvePathForNewFolder( sourceFolderPath, destinationFolderPath );
        assertEquals( "/sites/stuff/resolvers", newPath );
    }

}
