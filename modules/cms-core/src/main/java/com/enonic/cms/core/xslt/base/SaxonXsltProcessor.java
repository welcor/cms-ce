package com.enonic.cms.core.xslt.base;

import javax.xml.transform.Transformer;

import net.sf.saxon.value.UntypedAtomicValue;

public abstract class SaxonXsltProcessor
    extends BaseXsltProcessor
{
    public SaxonXsltProcessor( final Transformer transformer )
    {
        super( transformer );
    }

    @Override
    protected Object createUntypedAtomicValue( final String value )
    {
        return new UntypedAtomicValue( value );
    }
}
