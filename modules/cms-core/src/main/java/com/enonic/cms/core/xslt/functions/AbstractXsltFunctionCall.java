package com.enonic.cms.core.xslt.functions;

import java.util.ArrayList;

import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.BooleanValue;
import net.sf.saxon.value.StringValue;

public abstract class AbstractXsltFunctionCall
    extends ExtensionFunctionCall
{
    protected final SequenceIterator<? extends Item> createValue( final String value )
    {
        return StringValue.makeStringValue( value ).iterate();
    }

    protected final SequenceIterator<? extends Item> createValue( final boolean value )
    {
        return BooleanValue.get( value ).iterate();
    }

    protected final String toSingleString( final SequenceIterator<? extends Item> sequence )
        throws XPathException
    {
        final Item item = sequence.next();
        if ( item != null )
        {
            return item.getStringValue();
        }
        else
        {
            return null;
        }
    }

    protected final String[] toStringArray( final SequenceIterator<? extends Item> sequence )
        throws XPathException
    {
        final ArrayList<String> list = new ArrayList<String>();

        while ( true )
        {
            final String value = toSingleString( sequence );
            if ( value == null )
            {
                break;
            }

            list.add( value );
        }

        return list.toArray( new String[list.size()] );
    }
}
