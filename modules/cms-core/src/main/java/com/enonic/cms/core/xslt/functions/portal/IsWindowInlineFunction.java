/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.xslt.functions.portal;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceType;
import com.enonic.cms.core.xslt.functions.AbstractXsltFunctionCall;

final class IsWindowInlineFunction
    extends AbstractPortalFunction
{
    private final class Call
        extends AbstractXsltFunctionCall
    {
        @Override
        protected Item call( final XPathContext context, final SequenceIterator[] args )
            throws XPathException
        {
            return createValue( getPortalFunctions().isWindowInline() );
        }
    }

    public IsWindowInlineFunction()
    {
        super( "isWindowInline" );
        setResultType( SequenceType.SINGLE_BOOLEAN );
    }

    @Override
    protected AbstractXsltFunctionCall createCall()
    {
        return new Call();
    }
}
