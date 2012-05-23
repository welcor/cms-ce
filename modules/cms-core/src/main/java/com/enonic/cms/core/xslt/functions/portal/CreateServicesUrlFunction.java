package com.enonic.cms.core.xslt.functions.portal;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceType;
import com.enonic.cms.core.xslt.functions.AbstractXsltFunctionCall;

final class CreateServicesUrlFunction
    extends AbstractPortalFunction
{
    private final class Call
        extends AbstractXsltFunctionCall
    {
        @Override
        public SequenceIterator<? extends Item> call( final SequenceIterator<? extends Item>[] args, final XPathContext context )
            throws XPathException
        {
            final String handler = toSingleString( args[0] );
            final String operation = toSingleString( args[1] );
            String[] params = new String[0];
            String redirect = null;

            if (args.length == 3) {
                params = toStringArray( args[2] );
            } else if (args.length == 4) {
                redirect = toSingleString( args[2] );
                params = toStringArray( args[3] );
            }

            final String result = getPortalFunctions().createServicesUrl( handler, operation, params, redirect );
            return createValue( result );
        }
    }

    public CreateServicesUrlFunction()
    {
        super( "createServicesUrl" );
        setMinimumNumberOfArguments( 2 );
        setMaximumNumberOfArguments( 4 );
        setResultType( SequenceType.SINGLE_STRING );
    }

    @Override
    public ExtensionFunctionCall makeCallExpression()
    {
        return new Call();
    }
}
