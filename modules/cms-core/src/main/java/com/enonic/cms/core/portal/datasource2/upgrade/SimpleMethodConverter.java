package com.enonic.cms.core.portal.datasource2.upgrade;

import java.util.List;

import org.jdom.Element;

final class SimpleMethodConverter
    extends MethodConverter
{
    private final String name;

    private final String[] paramNames;

    public SimpleMethodConverter( final String name, final String... paramNames )
    {
        this.name = name;
        this.paramNames = paramNames;
    }

    @Override
    public boolean canHandle( final String methodName )
    {
        return this.name.equalsIgnoreCase( methodName );
    }

    @Override
    public void doConvert( final Element result, final String methodName, final List<String> values )
    {
        setName( result, this.name );
        for ( int i = 0; i < this.paramNames.length; i++ )
        {
            addParameter( result, values, i, this.paramNames[i] );
        }
    }
}
