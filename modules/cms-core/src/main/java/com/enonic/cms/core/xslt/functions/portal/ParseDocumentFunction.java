package com.enonic.cms.core.xslt.functions.portal;

import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.tree.iter.SingleNodeIterator;
import net.sf.saxon.value.SequenceType;

import com.enonic.esl.xml.XMLTool;

import com.enonic.cms.core.xslt.functions.AbstractXsltFunctionCall;

public class ParseDocumentFunction
    extends AbstractPortalFunction
{
    private final class Call
        extends AbstractXsltFunctionCall
    {
        @Override
        public SequenceIterator<? extends Item> call( final SequenceIterator<? extends Item>[] args, final XPathContext context )
            throws XPathException
        {
            final String inputDocumentAsString = toSingleString( args[0] );
            final Document parsedDocument = XMLTool.domparse( inputDocumentAsString );
            final NodeInfo nodeInfo = context.getController().prepareInputTree( new DOMSource( parsedDocument ) );

            return SingleNodeIterator.makeIterator( nodeInfo );
        }
    }

    public ParseDocumentFunction()
    {
        super( "parseDocument" );
        setMinimumNumberOfArguments( 1 );
        setMaximumNumberOfArguments( 1 );
        setArgumentTypes( SequenceType.SINGLE_STRING );
        setResultType( SequenceType.SINGLE_NODE );
    }


    @Override
    public ExtensionFunctionCall makeCallExpression()
    {
        return new Call();
    }
}
