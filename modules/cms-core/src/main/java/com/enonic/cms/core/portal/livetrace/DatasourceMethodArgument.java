package com.enonic.cms.core.portal.livetrace;

public class DatasourceMethodArgument
{
    private MaxLengthedString name = new MaxLengthedString();

    private MaxLengthedString value = new MaxLengthedString();

    private String override;

    DatasourceMethodArgument( String name, String value, String override )
    {
        this.name = new MaxLengthedString( name );
        this.value = new MaxLengthedString( value );
        this.override = override;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getName()
    {
        return name != null ? name.toString() : null;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getValue()
    {
        return value != null ? value.toString() : null;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getOverride()
    {
        return override;
    }
}
