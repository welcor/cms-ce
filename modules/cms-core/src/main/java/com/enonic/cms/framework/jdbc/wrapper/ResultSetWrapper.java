/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.jdbc.wrapper;

import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.openjpa.lib.jdbc.DelegatingResultSet;

public abstract class ResultSetWrapper
    extends DelegatingResultSet
{
    protected final ResultSet result;

    public ResultSetWrapper( final ResultSet result, final Statement stmt )
    {
        super( result, stmt );
        this.result = result;
    }
}
