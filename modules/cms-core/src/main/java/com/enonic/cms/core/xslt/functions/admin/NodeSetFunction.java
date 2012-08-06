package com.enonic.cms.core.xslt.functions.admin;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceType;

import com.enonic.cms.core.xslt.functions.AbstractXsltFunctionCall;

final class NodeSetFunction
    extends AbstractAdminFunction
{
    private final class Call
        extends AbstractXsltFunctionCall
    {
        @Override
        public SequenceIterator<? extends Item> call( final SequenceIterator<? extends Item>[] args, final XPathContext context )
            throws XPathException
        {
            return args[0];
        }
    }

    public NodeSetFunction()
    {
        super( "node-set" );
        setArgumentTypes( SequenceType.NODE_SEQUENCE );
        setResultType( SequenceType.NODE_SEQUENCE );
    }

    @Override
    public boolean trustResultType()
    {
        return true;
    }

    @Override
    public ExtensionFunctionCall makeCallExpression()
    {
        return new Call();
    }
}
