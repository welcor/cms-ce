package com.enonic.cms.core.plugin.ext;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.api.plugin.ext.TaskHandler;

import static org.junit.Assert.*;

public class TaskHandlerExtensionsTest
    extends ExtensionPointTest<TaskHandler, TaskHandlerExtensions>
{
    public TaskHandlerExtensionsTest()
    {
        super( TaskHandler.class );
    }

    @Override
    protected TaskHandlerExtensions createExtensionPoint()
    {
        return new TaskHandlerExtensions();
    }

    private TaskHandler create( final String name )
    {
        final TaskHandler ext = Mockito.mock( TaskHandler.class );
        ext.setName( name );
        return ext;
    }

    @Override
    protected TaskHandler createOne()
    {
        return create( "a" );
    }

    @Override
    protected TaskHandler createTwo()
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
