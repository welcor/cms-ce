/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import org.jdom.Document;
import org.jdom.Element;

/**
 * This xml creator only creates a "dummy" objectclasses xml for used in old admin interface.
 */
public final class ObjectClassesXmlCreator
{
    public Document createDocument( String oid )
    {
        Document doc = new Document();
        Element root = new Element( "objectclasses" );
        root.addContent( createElement( oid ) );
        doc.addContent( root );
        return doc;
    }

    private Element createElement( String oid )
    {
        Element root = new Element( "objectclass" );
        root.addContent( new Element( "oid" ).setText( oid ) );
        return root;
    }
}
