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
import com.enonic.cms.upgrade.task.datasource.DatasourceInfoHolder;

abstract class AbstractDataSourceUpgradeTask
    extends AbstractUpgradeTask
{
    private DataSourceConverterHelper helper;

    public AbstractDataSourceUpgradeTask( final int model )
    {
        super( model );
    }

    protected abstract DataSourceConverter newConverter( final UpgradeContext context );

    protected final void upgradeDataSources( final UpgradeContext context )
        throws Exception
    {
        this.helper = new DataSourceConverterHelper( newConverter( context ) );
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
        final Map<Integer, DatasourceInfoHolder> map = loadPageTemplates( conn );
        convertDataSources( map );
        updatePageTemplates( conn, map );
    }

    private void upgradePortlets( final Connection conn )
        throws Exception
    {
        final Map<Integer, DatasourceInfoHolder> map = loadPortlets( conn );
        convertDataSources( map );
        updatePortlets( conn, map );
    }

    private Map<Integer, DatasourceInfoHolder> loadPageTemplates( final Connection conn )
        throws Exception
    {
        return loadXmlMap( conn,
                           "SELECT pat_lkey, pat_xmlData, pat_sname, men_sname FROM tPageTemplate, tMenu WHERE pat_men_lkey = men_lkey" );
    }

    private Map<Integer, DatasourceInfoHolder> loadPortlets( final Connection conn )
        throws Exception
    {
        return loadXmlMap( conn,
                           "SELECT cob_lkey, cob_xmlData, cob_sname, men_sname FROM tContentObject, tMenu WHERE cob_men_lkey = men_lkey" );
    }

    private Map<Integer, DatasourceInfoHolder> loadXmlMap( final Connection conn, final String sql )
        throws Exception
    {
        final Map<Integer, DatasourceInfoHolder> map = Maps.newHashMap();
        final PreparedStatement stmt = conn.prepareStatement( sql );
        final ResultSet result = stmt.executeQuery();

        while ( result.next() )
        {
            DatasourceInfoHolder datasourceInfoHolder = new DatasourceInfoHolder();
            datasourceInfoHolder.setXml( new String( result.getBytes( 2 ), Charsets.UTF_8 ) );
            datasourceInfoHolder.setObjectName( result.getString( 3 ) );
            datasourceInfoHolder.setSite( result.getString( 4 ) );

            map.put( result.getInt( 1 ), datasourceInfoHolder );
        }

        result.close();
        stmt.close();

        return map;
    }

    private void convertDataSources( final Map<Integer, DatasourceInfoHolder> map )
        throws Exception
    {
        for ( final Integer key : map.keySet() )
        {
            final DatasourceInfoHolder datasourceInfoHolder = map.get( key );
            final String result = this.helper.convert( datasourceInfoHolder );
            datasourceInfoHolder.setXml( result );
        }
    }

    private void updatePageTemplates( final Connection conn, final Map<Integer, DatasourceInfoHolder> map )
        throws Exception
    {
        updateXmlMap( conn, "UPDATE tPageTemplate SET pat_xmlData = ? WHERE pat_lkey = ?", map );
    }

    private void updatePortlets( final Connection conn, final Map<Integer, DatasourceInfoHolder> map )
        throws Exception
    {
        updateXmlMap( conn, "UPDATE tContentObject SET cob_xmlData = ? WHERE cob_lkey = ?", map );
    }

    private void updateXmlMap( final Connection conn, final String sql, final Map<Integer, DatasourceInfoHolder> map )
        throws Exception
    {
        final PreparedStatement stmt = conn.prepareStatement( sql );

        for ( final Map.Entry<Integer, DatasourceInfoHolder> entry : map.entrySet() )
        {
            stmt.setBytes( 1, entry.getValue().getXml().getBytes( Charsets.UTF_8 ) );
            stmt.setInt( 2, entry.getKey() );
            stmt.executeUpdate();
        }

        stmt.close();
    }

}
