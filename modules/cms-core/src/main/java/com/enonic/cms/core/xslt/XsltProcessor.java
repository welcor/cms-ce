/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.xslt;

import javax.xml.transform.Result;
import javax.xml.transform.Source;

/**
 * This interface defines the xslt processor.
 */
public interface XsltProcessor
{
    /**
     * Return the output method.
     */
    public String getOutputMethod();

    /**
     * Return the output encoding.
     */
    public String getOutputEncoding();

    /**
     * Return the output media type.
     */
    public String getOutputMediaType();

    /**
     * Set omit xml declaration.
     */
    public void setOmitXmlDecl( boolean omitXmlDecl );

    /**
     * Return the content type.
     */
    public String getContentType();

    /**
     * Process the xml with stylesheet.
     */
    public String process( Source xml )
        throws XsltProcessorException;

    /**
     * Process the xml with stylesheet.
     */
    public void process( Source xml, Result result )
        throws XsltProcessorException;

    /**
     * Set parameter.
     */
    public void setParameter( String name, Object value );
}
