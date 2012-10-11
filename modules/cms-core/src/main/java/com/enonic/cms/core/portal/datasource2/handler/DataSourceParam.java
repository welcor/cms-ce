package com.enonic.cms.core.portal.datasource2.handler;

public interface DataSourceParam
{
    public DataSourceParam required();

    public String asString();

    public String asString( final String defValue );

    public Integer asInteger();

    public Integer asInteger( final Integer defValue );
}
