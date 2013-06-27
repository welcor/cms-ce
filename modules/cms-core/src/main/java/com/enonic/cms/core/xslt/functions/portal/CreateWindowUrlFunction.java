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

final class CreateWindowUrlFunction
    extends AbstractPortalFunction
{
    private final class Call
        extends AbstractXsltFunctionCall
    {
        @Override
        protected Item call( final XPathContext context, final SequenceIterator[] args )
            throws XPathException
        {
            String windowKey = null;
            String[] params = new String[0];
            String outputFormat = null;

            if (args.length == 1) {
                params = toStringArray( args[0] );
            } else if (args.length == 2) {
                windowKey = toSingleString( args[0] );
                params = toStringArray( args[1] );
            } else if (args.length == 3) {
                windowKey = toSingleString( args[0] );
                params = toStringArray( args[1] );
                outputFormat = toSingleString( args[2] );
            }

            final String result = getPortalFunctions().createWindowUrl( windowKey, params, outputFormat );
            return createValue( result );
        }
    }

    public CreateWindowUrlFunction()
    {
        super( "createWindowUrl" );
        setMinimumNumberOfArguments( 0 );
        setMaximumNumberOfArguments( 3 );
        setResultType( SequenceType.SINGLE_STRING );
    }

    @Override
    protected AbstractXsltFunctionCall createCall()
    {
        return new Call();
    }
}
