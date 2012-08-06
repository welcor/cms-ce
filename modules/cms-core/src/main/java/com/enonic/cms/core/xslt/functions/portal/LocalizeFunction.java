package com.enonic.cms.core.xslt.functions.portal;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceType;
import com.enonic.cms.core.xslt.functions.AbstractXsltFunctionCall;

final class LocalizeFunction
    extends AbstractPortalFunction
{
    private final class Call
        extends AbstractXsltFunctionCall
    {
        @Override
        public SequenceIterator<? extends Item> call( final SequenceIterator<? extends Item>[] args, final XPathContext context )
            throws XPathException
        {
            final String phrase = toSingleString( args[0] );
            String[] params = new String[0];
            String locale = null;

            if ( args.length == 2 )
            {
                params = toStringArray( args[1] );
            }
            else if ( args.length == 3 )
            {
                params = toStringArray( args[1] );
                locale = toSingleString( args[2] );
            }

            final String result = getPortalFunctions().localize( phrase, params, locale );
            return createValue( result );
        }
    }

    public LocalizeFunction()
    {
        super( "localize" );
        setMinimumNumberOfArguments( 1 );
        setMaximumNumberOfArguments( 3 );
        setResultType( SequenceType.SINGLE_STRING );
    }

    @Override
    public ExtensionFunctionCall makeCallExpression()
    {
        return new Call();
    }
}
