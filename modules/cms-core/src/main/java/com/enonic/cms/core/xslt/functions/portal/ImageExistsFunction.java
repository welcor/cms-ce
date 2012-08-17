package com.enonic.cms.core.xslt.functions.portal;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceType;
import com.enonic.cms.core.xslt.functions.AbstractXsltFunctionCall;

final class ImageExistsFunction
    extends AbstractPortalFunction
{
    private final class Call
        extends AbstractXsltFunctionCall
    {
        @Override
        protected Item call( final XPathContext context, final SequenceIterator[] args )
            throws XPathException
        {
            final String imageKey = toSingleString( args[0] );
            final boolean result = getPortalFunctions().imageExists( imageKey );
            return createValue( result );
        }
    }

    public ImageExistsFunction()
    {
        super( "imageExists" );
        setMinimumNumberOfArguments( 1 );
        setMaximumNumberOfArguments( 1 );
        setResultType( SequenceType.SINGLE_BOOLEAN );
    }

    @Override
    protected AbstractXsltFunctionCall createCall()
    {
        return new Call();
    }
}
