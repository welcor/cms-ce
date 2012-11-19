package com.enonic.cms.core.search.result;

import org.jdom.Document;
import org.springframework.util.StringUtils;

import com.enonic.cms.framework.util.JDOMUtil;

import static org.junit.Assert.*;

public class FacetResultSetTestBase
{

    protected void compareIgnoreWhitespacesAndLinebreaks( String expected, final Document doc )
    {
        String resultString = JDOMUtil.prettyPrintDocument( doc );

        System.out.println( resultString );

        expected = StringUtils.trimAllWhitespace( expected );
        resultString = StringUtils.trimAllWhitespace( resultString );
        assertEquals( StringUtils.trimTrailingWhitespace( expected ), StringUtils.trimTrailingWhitespace( resultString ) );
    }

}
