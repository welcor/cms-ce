package com.enonic.cms.core.xslt.functions.portal;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceType;

import com.enonic.cms.core.xslt.functions.AbstractXsltFunctionCall;

final class CreatePermalinkFunction
    extends AbstractPortalFunction
{
    private final class Call
        extends AbstractXsltFunctionCall
    {
        @Override
        protected Item call( final XPathContext context, final SequenceIterator[] args )
            throws XPathException
        {
            final String contentKey = toSingleString( args[0] );
            String[] params = new String[0];

            if ( args.length > 1 )
            {
                params = toStringArray( args[1] );
            }

            final String result = getPortalFunctions().createPermalink( contentKey, params );
            return createValue( result );
        }
    }

    public CreatePermalinkFunction()
    {
        super( "createPermalink" );
        setMinimumNumberOfArguments( 1 );
        setArgumentTypes( SequenceType.SINGLE_ATOMIC, SequenceType.ANY_SEQUENCE );
        setResultType( SequenceType.SINGLE_STRING );
    }

    @Override
    protected AbstractXsltFunctionCall createCall()
    {
        return new Call();
    }
}
