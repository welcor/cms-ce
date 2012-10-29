package com.enonic.cms.upgrade.task.datasource.method;

import org.jdom.Element;

import com.google.common.base.Strings;

public abstract class DataSourceMethodConverter
{
    private final String name;

    public DataSourceMethodConverter( final String name )
    {
        this.name = name;
    }

    public final String getName()
    {
        return this.name;
    }

    protected final boolean checkMinMax( final String[] params, final int min, final int max )
    {
        return ( params.length >= min ) && ( params.length <= max );
    }

    protected final MethodElementBuilder method()
    {
        return new MethodElementBuilder( this.name );
    }

    protected final MethodElementBuilder method( final String name )
    {
        return new MethodElementBuilder( name );
    }

    public abstract Element convert( final String[] params );

    protected String appendInFilterToQuery( final String existingQuery, final String indexValueString, final String indexFieldName )
    {
        if ( !Strings.isNullOrEmpty( indexValueString ) )
        {
            StringBuilder stringBuilder = new StringBuilder();

            if ( !Strings.isNullOrEmpty( existingQuery ) )
            {
                stringBuilder.append( "(" + existingQuery + ")" );
                stringBuilder.append( " AND " );
            }

            stringBuilder.append( indexFieldName + " IN (" );
            stringBuilder.append( indexValueString );
            stringBuilder.append( ")" );

            return stringBuilder.toString();
        }

        return existingQuery;
    }
}
