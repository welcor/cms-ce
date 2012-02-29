package com.enonic.cms.core.plugin.hook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.eclipse.osgi.framework.adaptor.BundleClassLoader;
import org.eclipse.osgi.framework.adaptor.BundleData;
import org.eclipse.osgi.framework.adaptor.ClassLoaderDelegateHook;

final class ParentClassLoaderHook
    implements ClassLoaderDelegateHook
{
    private final ClassLoader parent;

    public ParentClassLoaderHook()
    {
        this.parent = getClass().getClassLoader();
    }

    public Class preFindClass( final String name, final BundleClassLoader classLoader, final BundleData data )
        throws ClassNotFoundException
    {
        return null;
    }

    public Class postFindClass( final String name, final BundleClassLoader classLoader, final BundleData data )
        throws ClassNotFoundException
    {
        return this.parent.loadClass( name );
    }

    public URL preFindResource( final String name, final BundleClassLoader classLoader, final BundleData data )
        throws FileNotFoundException
    {
        return null;
    }

    public URL postFindResource( final String name, final BundleClassLoader classLoader, final BundleData data )
        throws FileNotFoundException
    {
        return this.parent.getResource( name );
    }

    public Enumeration preFindResources( final String name, final BundleClassLoader classLoader, final BundleData data )
        throws FileNotFoundException
    {
        return null;
    }

    public Enumeration postFindResources( final String name, final BundleClassLoader classLoader, final BundleData data )
        throws FileNotFoundException
    {
        try {
            return this.parent.getResources( name );
        } catch (final IOException e) {
            throw new FileNotFoundException( e.getMessage() );
        }
    }

    public String preFindLibrary( final String name, final BundleClassLoader classLoader, final BundleData data )
        throws FileNotFoundException
    {
        return null;
    }

    public String postFindLibrary( final String name, final BundleClassLoader classLoader, final BundleData data )
    {
        return null;
    }
}
