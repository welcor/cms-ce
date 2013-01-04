/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.sql.model;

import java.util.ArrayList;
import java.util.List;

public class Index
{
    private final String name;

    private final List<Column> columns = new ArrayList<Column>();

    private boolean unique = false;

    public Index( String name )
    {
        this.name = name;
    }

    public void addColumn( Column localColumn )
    {
        columns.add( localColumn );
    }

    public String getName()
    {
        return name;
    }

    public List<Column> getColumns()
    {
        return columns;
    }

    public boolean isUnique()
    {
        return unique;
    }

    public void setUnique( final boolean unique )
    {
        this.unique = unique;
    }
}
