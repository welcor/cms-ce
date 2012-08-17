package com.enonic.cms.core.xslt.functions.portal;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.*;
import com.enonic.cms.core.xslt.functions.AbstractXsltFunctionCall;

final class GetInstanceKeyFunction
    extends AbstractPortalFunction
{
    private final class Call
        extends AbstractXsltFunctionCall
    {
        @Override
        protected Item call( final XPathContext context, final SequenceIterator[] args )
            throws XPathException
        {
            return createValue( getPortalFunctions().getInstanceKey() );
        }
    }

    public GetInstanceKeyFunction()
    {
        super( "getInstanceKey" );
        setResultType( SequenceType.SINGLE_STRING );
    }

    @Override
    protected AbstractXsltFunctionCall createCall()
    {
        return new Call();
    }
}
