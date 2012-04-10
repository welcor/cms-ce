/*
 * Copyright 2000-2011 Enonic AS
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

    private final IndexFieldType indexFieldType;

    public IndexDefinition( String name, String xpath )
    {
        this.xpath = xpath.trim();
        this.name = name.trim();
        this.indexFieldType = IndexFieldType.STRING;
    }

    public IndexDefinition( String name, String xpath, IndexFieldType indexFieldType )
    {
        this.xpath = xpath.trim();
        this.name = name.trim();
        this.indexFieldType = indexFieldType;
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

    public IndexFieldType getIndexFieldType()
    {
        return indexFieldType;
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
