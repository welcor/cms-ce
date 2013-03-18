package com.enonic.cms.core.xslt.functions.admin;

import java.math.BigInteger;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.IntegerValue;
import net.sf.saxon.value.NumericValue;
import net.sf.saxon.value.SequenceType;

import com.enonic.cms.core.xslt.functions.AbstractXsltFunctionCall;

final class ToIntegerFunction
    extends AbstractAdminFunction
{
    private final class Call
        extends AbstractXsltFunctionCall
    {
        @Override
        protected Item call( final XPathContext context, final SequenceIterator[] args )
            throws XPathException
        {
            final Long value = toSingleInteger( args[0] );

            if ( value == null )
            {
                return null;
            }

            final BigInteger bigInteger = BigInteger.valueOf( value );
            return IntegerValue.makeIntegerValue( bigInteger );
        }
    }

    public ToIntegerFunction()
    {
        super( "toInteger" );
        setResultType( SequenceType.SINGLE_INTEGER );
        setArgumentTypes( SequenceType.SINGLE_ATOMIC );
        setMinimumNumberOfArguments( 1 );
    }

    @Override
    protected AbstractXsltFunctionCall createCall()
    {
        return new Call();
    }

    private Long toSingleInteger( final SequenceIterator it )
        throws XPathException
    {
        final Item item = it.next();
        if ( item == null )
        {
            return null;
        }
        else if ( item instanceof NumericValue )
        {
            return ( (NumericValue) item ).getDecimalValue().longValue();
        }
        else
        {
            return Long.parseLong( item.getStringValue() );
        }
    }
}
