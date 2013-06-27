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

final class IsWindowEmptyFunction
    extends AbstractPortalFunction
{
    private final class Call
        extends AbstractXsltFunctionCall
    {
        @Override
        protected Item call( final XPathContext context, final SequenceIterator[] args )
            throws XPathException
        {
            final String windowId = toSingleString( args[0] );
            String[] params = new String[0];

            if ( args.length > 1 )
            {
                params = toStringArray( args[1] );
            }

            final boolean result = getPortalFunctions().isWindowEmpty( windowId, params );
            return createValue( result );
        }
    }

    public IsWindowEmptyFunction()
    {
        super( "isWindowEmpty" );
        setMinimumNumberOfArguments( 1 );
        setMaximumNumberOfArguments( 2 );
        setResultType( SequenceType.SINGLE_BOOLEAN );
    }

    @Override
    protected AbstractXsltFunctionCall createCall()
    {
        return new Call();
    }
}
