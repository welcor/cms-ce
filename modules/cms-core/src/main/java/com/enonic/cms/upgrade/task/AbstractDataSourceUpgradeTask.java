package com.enonic.cms.upgrade.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;

import com.enonic.cms.upgrade.UpgradeContext;
import com.enonic.cms.upgrade.task.datasource.DataSourceConverter;
import com.enonic.cms.upgrade.task.datasource.DataSourceConverterHelper;

abstract class AbstractDataSourceUpgradeTask
    extends AbstractUpgradeTask
{
    private final DataSourceConverterHelper helper;

    public AbstractDataSourceUpgradeTask( final int model, final DataSourceConverter converter )
    {
        super( model );
        this.helper = new DataSourceConverterHelper( converter );
    }

    protected final void upgradeDataSources( final UpgradeContext context )
        throws Exception
    {
        final Connection conn = context.getConnection();

        try
        {
            context.logInfo( "Converting pageTemplate datasources..." );
            upgradePageTemplates( conn );
            context.logInfo( "Converting portlet datasources..." );
            upgradePortlets( conn );
        }
        finally
        {
            context.close( conn );
        }
    }

    private void upgradePageTemplates( final Connection conn )
        throws Exception
    {
        final Map<Integer, String> map = loadPageTemplates( conn );
        convertDataSources( map );
        updatePageTemplates( conn, map );
    }

    private void upgradePortlets( final Connection conn )
        throws Exception
    {
        final Map<Integer, String> map = loadPortlets( conn );
        convertDataSources( map );
        updatePortlets( conn, map );
    }

    private Map<Integer, String> loadPageTemplates( final Connection conn )
        throws Exception
    {
        return loadXmlMap( conn, "SELECT pat_lkey, pat_xmlData FROM tPageTemplate" );
    }

    private Map<Integer, String> loadPortlets( final Connection conn )
        throws Exception
    {
        return loadXmlMap( conn, "SELECT cob_lkey, cob_xmlData FROM tContentObject" );
    }

    private Map<Integer, String> loadXmlMap( final Connection conn, final String sql )
        throws Exception
    {
        final Map<Integer, String> map = Maps.newHashMap();
        final PreparedStatement stmt = conn.prepareStatement( sql );
        final ResultSet result = stmt.executeQuery();

        while ( result.next() )
        {
            map.put( result.getInt( 1 ), new String( result.getBytes( 2 ), Charsets.UTF_8 ) );
        }

        result.close();
        stmt.close();

        return map;
    }

    private void convertDataSources( final Map<Integer, String> map )
        throws Exception
    {
        for ( final Integer key : map.keySet() )
        {
            final String result = this.helper.convert( map.get( key ) );
            map.put( key, result );
        }
    }

    private void updatePageTemplates( final Connection conn, final Map<Integer, String> map )
        throws Exception
    {
        updateXmlMap( conn, "UPDATE tPageTemplate SET pat_xmlData = ? WHERE pat_lkey = ?", map );
    }

    private void updatePortlets( final Connection conn, final Map<Integer, String> map )
        throws Exception
    {
        updateXmlMap( conn, "UPDATE tContentObject SET cob_xmlData = ? WHERE cob_lkey = ?", map );
    }

    private void updateXmlMap( final Connection conn, final String sql, final Map<Integer, String> map )
        throws Exception
    {
        final PreparedStatement stmt = conn.prepareStatement( sql );

        for ( final Map.Entry<Integer, String> entry : map.entrySet() )
        {
            stmt.setBytes( 1, entry.getValue().getBytes( Charsets.UTF_8 ) );
            stmt.setInt( 2, entry.getKey() );
            stmt.executeUpdate();
        }

        stmt.close();
    }
}
