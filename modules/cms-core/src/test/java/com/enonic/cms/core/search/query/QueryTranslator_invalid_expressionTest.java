/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.query;

import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.index.queryexpression.QueryParserException;

public class QueryTranslator_invalid_expressionTest
    extends QueryTranslatorTestBase
{
    @Test(expected = QueryParserException.class)
    public void testNotParsableExpression()
        throws Exception
    {
        ContentIndexQuery query = createContentQuery( "title INN (\"Hello\")" );

        getQueryTranslator().build( query );
    }
}
