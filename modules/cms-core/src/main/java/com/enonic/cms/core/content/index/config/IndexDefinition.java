/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.config;

import java.util.Collections;
import java.util.List;

import org.jdom.Document;

public final class IndexDefinition
{
    private final String name;

    private final String xpath;

    public IndexDefinition( String name, String xpath )
    {
        this.xpath = xpath.trim();
        this.name = name.trim();
    }

    public String getName()
    {
        if ( this.name.startsWith( "data/" ) )
        {
            return this.name;
        }
        else
        {
            return "data/" + this.name;
        }
    }

    public String getXPath()
    {
        return this.xpath;
    }

    public int hashCode()
    {
        return this.name.hashCode();
    }

    public List<String> evaluate( Document doc )
    {
        List<String> result = IndexPathEvaluator.evaluateShared( this.xpath, doc );
        if ( result.isEmpty() )
        {
            return Collections.singletonList( "" );
        }
        else
        {
            return result;
        }
    }
}
