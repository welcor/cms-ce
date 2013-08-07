package com.enonic.cms.core.plugin.ext;

import java.util.List;

import org.junit.Test;

import com.enonic.cms.api.plugin.ext.http.HttpProcessor;

import static org.junit.Assert.*;

public abstract class HttpProcessorExtensionsTest<E extends HttpProcessor, P extends HttpProcessorExtensions<E>>
    extends ExtensionPointTest<E, P>
{
    public HttpProcessorExtensionsTest( final Class<E> type )
    {
        super( type );
    }

    protected abstract E createExt();

    private E create( final int priority, final String pattern )
    {
        final E ext = createExt();
        ext.setPriority( priority );
        ext.setUrlPattern( pattern );
        return ext;
    }

    @Override
    protected final E createOne()
    {
        return create( 1, "/path/a" );
    }

    @Override
    protected final E createTwo()
    {
        return create( 2, "/path/b" );
    }

    @Test
    public void testFindMatching()
    {
        final List<E> list1 = this.extensions.findMatching( "/path" );
        assertEquals( 0, list1.size() );

        this.extensions.addExtension( this.ext1 );
        this.extensions.addExtension( this.ext2 );

        final List<E> list2 = this.extensions.findMatching( "/path/a" );
        assertEquals( 1, list2.size() );
        assertSame( this.ext1, list2.get( 0 ) );

        final List<E> list3 = this.extensions.findMatching( "/path/c" );
        assertEquals( 0, list3.size() );
    }
}
