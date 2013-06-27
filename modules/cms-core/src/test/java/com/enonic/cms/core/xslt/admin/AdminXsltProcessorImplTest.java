/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.xslt.admin;

import javax.xml.transform.Transformer;

import com.enonic.cms.core.xslt.base.SaxonXsltProcessorTest;

public class AdminXsltProcessorImplTest
    extends SaxonXsltProcessorTest<AdminXsltProcessorImpl>
{
    @Override
    protected AdminXsltProcessorImpl createProcessor( final Transformer transformer )
        throws Exception
    {
        return new AdminXsltProcessorImpl( transformer );
    }
}
