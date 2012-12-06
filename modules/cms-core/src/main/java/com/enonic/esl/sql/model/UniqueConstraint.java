/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.sql.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UniqueConstraint
{
    public static Pattern REQUIRED_NAME_PATTERN = Pattern.compile( "^.*_UC\\d$", Pattern.CASE_INSENSITIVE );

    private String name;

    private List<Column> columns = new ArrayList<Column>();

    public UniqueConstraint( String name )
    {
        Matcher matcher = REQUIRED_NAME_PATTERN.matcher( name );

        if ( !matcher.matches() )
        {
            throw new IllegalArgumentException( "The name of an UNIQUE CONSTRAIN must end with 'UC<number>'" );
        }

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
