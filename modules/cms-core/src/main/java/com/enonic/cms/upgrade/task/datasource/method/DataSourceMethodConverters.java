package com.enonic.cms.upgrade.task.datasource.method;

import java.util.Map;

import com.google.common.collect.Maps;

public final class DataSourceMethodConverters
{
    private final Map<String, DataSourceMethodConverter> map;

    public DataSourceMethodConverters()
    {
        this.map = Maps.newHashMap();
        add( new GetLocalesConverter() );
        add( new GetTimeZonesConverter() );
        add( new GetPreferencesConverter() );
        add( new GetUserStoreConverter() );
        add( new GetCountriesConverter() );
        add( new GetCalendarConverter() );
        add( new GetContentVersionConverter() );
        add( new GetUrlAsTextConverter() );
        add( new GetUrlAsXmlConverter() );
        add( new GetFormattedDateConverter() );
        add( new GetIndexValuesConverter() );
        add( new GetAggregatedIndexValuesConverter() );
        add( new GetMenuConverter() );
        add( new GetMenuDataConverter() );
        add( new GetMenuItemConverter() );
        add( new GetSubMenuConverter() );
    }

    private void add( final DataSourceMethodConverter converter )
    {
        this.map.put( converter.getName(), converter );
    }

    public DataSourceMethodConverter get( final String name )
    {
        return this.map.get( name );
    }
}
