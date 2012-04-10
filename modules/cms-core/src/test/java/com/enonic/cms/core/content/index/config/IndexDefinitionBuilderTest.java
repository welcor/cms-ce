/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.config;

import java.util.List;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class IndexDefinitionBuilderTest
{
    private IndexDefinitionBuilder builder;

    private Document inputDoc;

    private Document loadTestDocument()
        throws Exception
    {
        SAXBuilder builder = new SAXBuilder();
        return builder.build( getClass().getResourceAsStream( getClass().getSimpleName() + ".xml" ) );
    }

    @Before
    public void init()
        throws Exception
    {
        this.builder = new IndexDefinitionBuilder();
        this.inputDoc = loadTestDocument();
    }

    @Test
    public void testBuilder()
    {
        List<IndexDefinition> result = this.builder.buildList( this.inputDoc.getRootElement() );
        Assert.assertEquals( 12, result.size() );
        assertEquals( result.get( 0 ), "data/person/firstName", "contentdata/person/firstName", IndexFieldType.STRING );
        assertEquals( result.get( 1 ), "data/colorCount", "count(//favouriteColor)", IndexFieldType.STRING );
        assertEquals( result.get( 2 ), "data/colorList", "string-join(saxon:sort(//favouriteColor), ',')", IndexFieldType.STRING );
        assertEquals( result.get( 3 ), "data/a", "contentdata/a", IndexFieldType.STRING );
        assertEquals( result.get( 4 ), "data/b", "contentdata/b", IndexFieldType.STRING );
        assertEquals( result.get( 5 ), "data/c", "c", IndexFieldType.STRING );
        assertEquals( result.get( 6 ), "data/d", "contentdata/d", IndexFieldType.DATE );
        assertEquals( result.get( 7 ), "data/e", "contentdata/e", IndexFieldType.STRING );
        assertEquals( result.get( 8 ), "data/f", "contentdata/f", IndexFieldType.NUMBER );
        assertEquals( result.get( 9 ), "data/g", "contentdata/g", IndexFieldType.DATE );
        assertEquals( result.get( 10 ), "data/h", "contentdata/h", IndexFieldType.STRING );
        assertEquals( result.get( 11 ), "data/i", "i", IndexFieldType.NUMBER );
    }

    private void assertEquals( IndexDefinition def, String name, String xpath, IndexFieldType indexFieldType )
    {
        Assert.assertNotNull( def );
        Assert.assertEquals( name, def.getName() );
        Assert.assertEquals( xpath, def.getXPath() );
        Assert.assertEquals( indexFieldType, def.getIndexFieldType() );
    }
}
