package com.enonic.cms.core.plugin.ext;

import org.springframework.stereotype.Component;

import com.enonic.cms.api.plugin.ext.TaskHandler;

@Component
public final class TaskHandlerExtensions
    extends ExtensionPoint<TaskHandler>
{
    public TaskHandlerExtensions()
    {
        super( TaskHandler.class );
    }

    public TaskHandler getByName( final String name )
    {
        for ( final TaskHandler ext : this )
        {
            if ( ext.getName().equals( name ) )
            {
                return ext;
            }
        }

        return null;
    }

    @Override
    protected String toHtml( final TaskHandler ext )
    {
        return composeHtml( ext, "name", ext.getName() );
    }

    @Override
    public int compare( final TaskHandler o1, final TaskHandler o2 )
    {
        return o1.getName().compareTo( o2.getName() );
    }
}
