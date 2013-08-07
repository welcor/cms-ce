package com.enonic.cms.core.plugin.ext;

import org.springframework.stereotype.Component;

import com.enonic.cms.api.plugin.ext.FunctionLibrary;

@Component
public final class FunctionLibraries
    extends ExtensionPoint<FunctionLibrary>
{
    public FunctionLibraries()
    {
        super( FunctionLibrary.class );
    }

    public FunctionLibrary getByName( final String name )
    {
        for ( final FunctionLibrary ext : this )
        {
            if ( ext.getName().equals( name ) )
            {
                return ext;
            }
        }

        return null;
    }

    @Override
    protected String toHtml( final FunctionLibrary ext )
    {
        return composeHtml( ext, "name", ext.getName(), "target", ext.getTargetClass().getName() );
    }

    @Override
    public int compare( final FunctionLibrary o1, final FunctionLibrary o2 )
    {
        return o1.getName().compareTo( o2.getName() );
    }
}
