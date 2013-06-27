/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.plugin.hook;

import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.eclipse.osgi.baseadaptor.bundlefile.BundleEntry;
import org.eclipse.osgi.baseadaptor.bundlefile.BundleFile;

import com.google.common.base.Strings;

import com.enonic.cms.core.plugin.spring.SpringActivator;

final class ManifestTransformer
{
    public Manifest transform( final BundleFile file )
        throws IOException
    {
        final BundleEntry entry = file.getEntry( JarFile.MANIFEST_NAME );
        if ( entry == null )
        {
            throw new IOException( "Missing required metadata file." );
        }

        final Manifest mf = new Manifest();
        mf.read( entry.getInputStream() );

        transform( mf, file );
        return mf;
    }

    private void transform( final Manifest mf, final BundleFile file )
        throws IOException
    {
        final Attributes attr = mf.getMainAttributes();

        final String pluginId = findPluginId( attr );
        final String pluginName = findPluginName( attr, pluginId );
        final String pluginVersion = findPluginVersion( attr );

        if ( Strings.isNullOrEmpty( pluginId ) )
        {
            throw new IOException( "Required metadata not found. Plugin-Id or Bundle-SymbolicName is required." );
        }

        attr.putValue( "Bundle-SymbolicName", pluginId );
        attr.putValue( "Bundle-Name", pluginName );
        attr.putValue( "Bundle-Version", pluginVersion );
        attr.putValue( "Bundle-ManifestVersion", "2" );
        attr.putValue( "Bundle-Activator", SpringActivator.class.getName() );
        attr.putValue( "Bundle-ClassPath", findClassPath( file ) );

        // Remove old bundle metadata
        attr.remove( new Attributes.Name( "Import-Package" ) );
        attr.remove( new Attributes.Name( "Export-Package" ) );
        attr.remove( new Attributes.Name( "Require-Bundle" ) );
    }

    private String findClassPath( final BundleFile file )
    {
        final StringBuilder str = new StringBuilder( "." );
        final Enumeration e = file.getEntryPaths( "META-INF/lib/" );

        if ( e == null )
        {
            return str.toString();
        }

        while ( e.hasMoreElements() )
        {
            final String name = (String) e.nextElement();
            if ( name.endsWith( ".jar" ) )
            {
                str.append( ",/" ).append( name );
            }
        }

        return str.toString();
    }

    private String findPluginId( final Attributes attr )
    {
        String value = attr.getValue( "Plugin-Id" );

        if ( Strings.isNullOrEmpty( value ) )
        {
            value = attr.getValue( "Bundle-SymbolicName" );
        }

        return value;
    }

    private String findPluginName( final Attributes attr, final String defValue )
    {
        String value = attr.getValue( "Plugin-Name" );

        if ( Strings.isNullOrEmpty( value ) )
        {
            value = attr.getValue( "Bundle-Name" );
        }

        if ( Strings.isNullOrEmpty( value ) )
        {
            value = defValue;
        }

        return value;
    }

    private String findPluginVersion( final Attributes attr )
    {
        String value = attr.getValue( "Plugin-Version" );

        if ( Strings.isNullOrEmpty( value ) )
        {
            value = attr.getValue( "Bundle-Version" );
        }

        if ( Strings.isNullOrEmpty( value ) )
        {
            value = "0.0.0";
        }

        return value.replace( '-', '.' );
    }
}
