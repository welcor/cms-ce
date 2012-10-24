package com.enonic.cms.core.portal.datasource.handler;

public interface DataSourceParam
{
    public DataSourceParam required();

    public String asString();

    public String asString( String defValue );

    public Integer asInteger();

    public Integer asInteger( Integer defValue );

    public Boolean asBoolean();

    public Boolean asBoolean( Boolean defValue );

    public String[] asStringArray();

    public String[] asStringArray( String... defValues );

    public Integer[] asIntegerArray();

    public Integer[] asIntegerArray( Integer... defValues );
}
