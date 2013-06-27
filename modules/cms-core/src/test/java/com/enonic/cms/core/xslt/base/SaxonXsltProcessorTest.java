/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.xslt.base;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;

import net.sf.saxon.TransformerFactoryImpl;

public abstract class SaxonXsltProcessorTest<T extends SaxonXsltProcessor>
    extends BaseXsltProcessorTest<T>
{
    @Override
    protected final Transformer createTransformer( final Source source )
        throws Exception
    {
        return new TransformerFactoryImpl().newTransformer( source );
    }
}
