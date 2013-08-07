package com.enonic.cms.core.plugin.ext;

import java.util.Properties;

import org.junit.Test;

import com.enonic.cms.api.plugin.ext.userstore.RemoteUserStore;
import com.enonic.cms.api.plugin.ext.userstore.RemoteUserStoreFactory;

import static org.junit.Assert.*;

public class RemoteUserStoreFactoryExtensionsTest
    extends ExtensionPointTest<RemoteUserStoreFactory, RemoteUserStoreFactoryExtensions>
{
    public RemoteUserStoreFactoryExtensionsTest()
    {
        super( RemoteUserStoreFactory.class );
    }

    @Override
    protected RemoteUserStoreFactoryExtensions createExtensionPoint()
    {
        return new RemoteUserStoreFactoryExtensions();
    }

    private RemoteUserStoreFactory create( final String type, final String... aliases )
    {
        return new RemoteUserStoreFactory( type, aliases )
        {
            @Override
            public RemoteUserStore create( final Properties props )
            {
                return null;
            }
        };
    }

    @Override
    protected RemoteUserStoreFactory createOne()
    {
        return create( "a", "one" );
    }

    @Override
    protected RemoteUserStoreFactory createTwo()
    {
        return create( "b" );
    }

    @Test
    public void testGetByType()
    {
        assertNull( this.extensions.getByType( "a" ) );

        this.extensions.addExtension( this.ext1 );
        assertSame( this.ext1, this.extensions.getByType( "a" ) );

        this.extensions.addExtension( this.ext2 );
        assertSame( this.ext2, this.extensions.getByType( "b" ) );
        assertSame( this.ext1, this.extensions.getByType( "one" ) );
    }
}
