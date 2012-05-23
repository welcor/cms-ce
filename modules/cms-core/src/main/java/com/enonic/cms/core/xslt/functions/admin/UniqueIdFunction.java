package com.enonic.cms.core.xslt.functions.admin;

import java.util.UUID;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
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
        public SequenceIterator<? extends Item> call( final SequenceIterator<? extends Item>[] args, final XPathContext context )
            throws XPathException
        {
            final String id = generateId();
            return createValue( id );
        }
    }

    public UniqueIdFunction()
    {
        super( "uniqueId" );
        setResultType( SequenceType.SINGLE_STRING );
    }

    protected String generateId()
    {
        return UUID.randomUUID().toString().replaceAll( "-", "" );
    }

    @Override
    public ExtensionFunctionCall makeCallExpression()
    {
        return new Call();
    }
}
