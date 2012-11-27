/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.sql.model;

import java.util.ArrayList;
import java.util.List;

public class UniqueConstraint
{
    private String name;

    private List<Column> columns = new ArrayList<Column>();

    public UniqueConstraint( String name )
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
}
