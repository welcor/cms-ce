package com.enonic.cms.core.plugin.ext;

import org.junit.Test;

import com.enonic.cms.api.plugin.ext.FunctionLibrary;

import static org.junit.Assert.*;

public class FunctionLibraryExtensionsTest
    extends ExtensionPointTest<FunctionLibrary, FunctionLibraryExtensions>
{
    public FunctionLibraryExtensionsTest()
    {
        super( FunctionLibrary.class );
    }

    @Override
    protected FunctionLibraryExtensions createExtensionPoint()
    {
        return new FunctionLibraryExtensions();
    }

    private FunctionLibrary create( final String name )
    {
        final FunctionLibrary ext = new FunctionLibrary();
        ext.setName( name );
        return ext;
    }

    @Override
    protected FunctionLibrary createOne()
    {
        return create( "a" );
    }

    @Override
    protected FunctionLibrary createTwo()
    {
        return create( "b" );
    }

    @Test
    public void testGetByName()
    {
        assertNull( this.extensions.getByName( "a" ) );

        this.extensions.addExtension( this.ext1 );
        assertSame( this.ext1, this.extensions.getByName( "a" ) );
    }
}
