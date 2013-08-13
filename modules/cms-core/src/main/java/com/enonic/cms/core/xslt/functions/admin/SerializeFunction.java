/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.xslt.functions.admin;

import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

import net.sf.saxon.dom.NodeOverNodeInfo;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.BooleanValue;
import net.sf.saxon.value.SequenceType;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.core.xslt.functions.AbstractXsltFunctionCall;

class SerializeFunction
    extends AbstractAdminFunction
{
    private final class Call
        extends AbstractXsltFunctionCall
    {
        @Override
        protected Item call( final XPathContext context, final SequenceIterator[] args )
            throws XPathException
        {
            final NodeInfo node = (NodeInfo) args[0].next();
            final boolean includeSelf = ( (BooleanValue) args[1].next() ).getBooleanValue();

            final org.w3c.dom.Node w3cNode = NodeOverNodeInfo.wrap( node );

            final Document doc = JDOMUtil.toDocument( w3cNode );
            recursiveRemoveNamespaces( doc.getRootElement() );

            final String output = includeSelf ? JDOMUtil.serialize( doc, 4, true ) : JDOMUtil.serializeChildren( doc, 4 );
            return createValue( output.trim() );
        }
    }

    @SuppressWarnings("unchecked")
    private Namespace[] findNamespaces( final Element element )
    {
        final List list = element.getAdditionalNamespaces();
        return (Namespace[]) list.toArray( new Namespace[list.size()] );
    }

    private void recursiveRemoveNamespaces( final Element element )
    {
        for ( final Namespace ns : findNamespaces( element ) )
        {
            element.removeNamespaceDeclaration( ns );
        }

        for ( final Object content : element.getContent() )
        {
            if ( content instanceof Element )
            {
                recursiveRemoveNamespaces( Element.class.cast( content ) );
            }
        }
    }

    public SerializeFunction()
    {
        super( "serialize" );
        setResultType( SequenceType.SINGLE_STRING );
        setArgumentTypes( SequenceType.SINGLE_NODE, SequenceType.SINGLE_BOOLEAN );
        setMinimumNumberOfArguments( 2 );
    }

    @Override
    protected AbstractXsltFunctionCall createCall()
    {
        return new Call();
    }
}