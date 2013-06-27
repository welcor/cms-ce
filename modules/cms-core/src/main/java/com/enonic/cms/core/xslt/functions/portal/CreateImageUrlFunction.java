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

final class CreateImageUrlFunction
    extends AbstractPortalFunction
{
    private final class Call
        extends AbstractXsltFunctionCall
    {
        @Override
        protected Item call( final XPathContext context, final SequenceIterator[] args )
            throws XPathException
        {
            final String key = toSingleString( args[0] );
            String filter = null;
            String background = null;
            String format = null;
            String quality = null;

            if ( args.length == 2 )
            {
                filter = toSingleString( args[1] );
            }
            else if ( args.length == 3 )
            {
                filter = toSingleString( args[1] );
                background = toSingleString( args[2] );
            }
            else if ( args.length == 4 )
            {
                filter = toSingleString( args[1] );
                background = toSingleString( args[2] );
                format = toSingleString( args[3] );
            }
            else if ( args.length == 5 )
            {
                filter = toSingleString( args[1] );
                background = toSingleString( args[2] );
                format = toSingleString( args[3] );
                quality = toSingleString( args[4] );
            }

            final String result = getPortalFunctions().createImageUrl( key, filter, background, format, quality );
            return createValue( result );
        }
    }

    public CreateImageUrlFunction()
    {
        super( "createImageUrl" );
        setMinimumNumberOfArguments( 1 );
        setMaximumNumberOfArguments( 5 );
        setResultType( SequenceType.SINGLE_STRING );
    }

    @Override
    protected AbstractXsltFunctionCall createCall()
    {
        return new Call();
    }
}
