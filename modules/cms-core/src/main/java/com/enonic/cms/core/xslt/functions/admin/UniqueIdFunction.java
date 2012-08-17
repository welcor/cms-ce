package com.enonic.cms.core.xslt.functions.admin;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceType;

import com.enonic.cms.core.xslt.functions.AbstractXsltFunctionCall;

class UniqueIdFunction
    extends AbstractAdminFunction
{
    private final class Call
        extends AbstractXsltFunctionCall
    {
        @Override
        protected Item call( final XPathContext context, final SequenceIterator[] args )
            throws XPathException
        {
            final String id = generator.generateUniqueId();
            return createValue( id );
        }
    }

    private final UniqueIdGenerator generator;

    public UniqueIdFunction( final UniqueIdGenerator generator )
    {
        super( "uniqueId" );
        setResultType( SequenceType.SINGLE_STRING );
        this.generator = generator;
    }

    @Override
    protected AbstractXsltFunctionCall createCall()
    {
        return new Call();
    }
}
