/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.executor;

import org.jdom.Element;

final class DataSourceResultBuilder
{
    private final Element root;

    public DataSourceResultBuilder( final String name )
    {
        this.root = new Element( name );
    }

    public Element getRootElement()
    {
        return this.root;
    }

    public void addElement( final Element element )
    {
        this.root.addContent( element );
    }

    public void addElementToGroup( final String name, final Element element )
    {
        if ( name == null )
        {
            this.root.addContent( element );
            return;
        }

        final Element existing = root.getChild( name );
        if ( existing != null )
        {
            existing.addContent( element );
            return;
        }

        final Element wrapper = new Element( name );
        wrapper.addContent( element );
        this.root.addContent( wrapper );
    }
}
