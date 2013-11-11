/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.upgrade.task;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.util.StringUtils;

import com.enonic.cms.upgrade.UpgradeContext;

final class UpgradeModel0212
    extends AbstractUpgradeTask
{
    public UpgradeModel0212()
    {
        super( 212 );
    }

    @Override
    protected boolean canModelUpgrade( UpgradeContext context )
    {
        context.logInfo( "Looking for columns in the database that have length 256 and value more than 255 characters and are indexed..." );

        final Map<LargeIndexedColumn, List<String>> xtraLarges = new HashMap<LargeIndexedColumn, List<String>>();

        try
        {
            for ( LargeIndexedColumn column : LargeIndexedColumn.values() )
            {
                List<String> keys = (List<String>) context.getJdbcTemplate().query( column.query(), column.extractor() );

                if ( !keys.isEmpty() )
                {
                    xtraLarges.put( column, keys );
                }
            }
        }
        catch ( Exception ex )
        {
            context.logError( ex.getMessage(), ex );
            return false;
        }

        if ( xtraLarges.isEmpty() )
        {
            context.logInfo( "Upgrade check ok." );
            return true;
        }

        String plural = ( xtraLarges.size() == 1) ? "" : "s";
        context.logWarning( "Column" + plural + " with value more than 255 characters found:" );

        for ( Map.Entry<LargeIndexedColumn, List<String>> xtraLarge : xtraLarges.entrySet() )
        {
            final LargeIndexedColumn column = xtraLarge.getKey();
            final List<String> ids = xtraLarge.getValue();

            final String message =
                "\nTable [" + column.getTable() + "], Column [" + column.name() + "], ID [" + column.getIdColumn() + "] = '" +
                    StringUtils.collectionToCommaDelimitedString( ids ) + "'";

            context.logWarning( message );
        }

        context.logWarning( "\nThe length of value in column" + plural + " above will be reduced." );

        return true;
    }

    private enum LargeIndexedColumn
    {
        bda_sFileName
            {
                String getTable()
                {
                    return "tBinaryData";
                }

                String getIdColumn()
                {
                    return "bda_lKey";
                }
            },
        cat_sName
            {
                String getTable()
                {
                    return "tCategory";
                }

                String getIdColumn()
                {
                    return "cat_lKey";
                }
            },
        han_sClass
            {
                String getTable()
                {
                    return "tContentHandler";
                }

                String getIdColumn()
                {
                    return "han_lKey";
                }
            },
        cob_sName
            {
                String getTable()
                {
                    return "tContentObject";
                }

                String getIdColumn()
                {
                    return "cob_lKey";
                }
            },
        cov_sTitle
            {
                String getTable()
                {
                    return "tContentVersion";
                }

                String getIdColumn()
                {
                    return "cov_lKey";
                }
            },
        grp_sName
            {
                String getTable()
                {
                    return "tGroup";
                }

                String getIdColumn()
                {
                    return "grp_hKey";
                }
            },
        mei_sName
            {
                String getTable()
                {
                    return "tMenuItem";
                }

                String getIdColumn()
                {
                    return "mei_lKey";
                }
            },
        mei_sURL
            {
                String getTable()
                {
                    return "tMenuItem";
                }

                String getIdColumn()
                {
                    return "mei_lKey";
                }
            },
        usr_sUID
            {
                String getTable()
                {
                    return "tUser";
                }

                String getIdColumn()
                {
                    return "usr_hKey";
                }
            },
        usr_sEmail
            {
                String getTable()
                {
                    return "tUser";
                }

                String getIdColumn()
                {
                    return "usr_hKey";
                }
            };

        abstract String getTable();

        abstract String getIdColumn();

        String query()
        {
            return "SELECT " + this.getIdColumn() + " FROM " + this.getTable() + " WHERE @length@(" + this.name() + ") > 255";
        }

        ResultSetExtractor extractor()
        {
            return new IdExtractor( this.getIdColumn() );
        }
    }

    private static class IdExtractor
        implements ResultSetExtractor
    {
        private final String idColumn;

        private IdExtractor( final String idColumn )
        {
            this.idColumn = idColumn;
        }

        public Object extractData( ResultSet rs )
            throws SQLException, DataAccessException
        {
            List<String> ids = new ArrayList<String>();

            while ( rs.next() )
            {
                final String id = rs.getString( idColumn );
                ids.add( id );
            }

            return ids;
        }
    }

    public void upgrade( final UpgradeContext context )
        throws Exception
    {
        context.logInfo( " ########## Reduce size of VARCHAR(256) to size VARCHAR(255) for columns which indexed ########## " );

        context.logInfo( "Add temporary column with index '2' type VARCHAR (255)" );
        addTemporaryColumns( context );

        context.logInfo( "Insert values from old columns to new with index '2'" );
        setValueTemporaryColumns( context );

        context.logInfo( "Drop all current constraints on modified tables" );
        dropTableConstraints( context );

        context.logInfo( "Drop old columns type VARCHAR(256)" );
        dropTableColumns( context );

        context.logInfo( "Add new columns type VARCHAR (255)" );
        createNewColumns( context );

        context.logInfo( "Rollback values from columns with index '2' to new" );
        setValueNewColumns( context );

        context.logInfo( "Drop temporary columns" );
        dropTableConstraints( context );
        dropTemporaryColumns( context );

        context.logInfo( "Re-create all constraints on modified tables" );
        createTableConstraints( context );
    }

    private void addTemporaryColumns( final UpgradeContext context )
        throws Exception
    {
        context.getJdbcTemplate().execute( "ALTER TABLE tUser ADD usr_sEmail2 @varchar(255)@" );
        context.getJdbcTemplate().execute( "ALTER TABLE tUser ADD usr_sUID2 @varchar(255)@" );

        context.getJdbcTemplate().execute( "ALTER TABLE tMenuItem ADD mei_sURL2 @varchar(255)@" );
        context.getJdbcTemplate().execute( "ALTER TABLE tMenuItem ADD mei_sName2 @varchar(255)@" );

        context.getJdbcTemplate().execute( "ALTER TABLE tGroup ADD grp_sName2 @varchar(255)@" );
        context.getJdbcTemplate().execute( "ALTER TABLE tContentVersion ADD cov_sTitle2 @varchar(255)@" );
        context.getJdbcTemplate().execute( "ALTER TABLE tContentObject ADD cob_sName2 @varchar(255)@" );
        context.getJdbcTemplate().execute( "ALTER TABLE tContentHandler ADD han_sClass2 @varchar(255)@" );
        context.getJdbcTemplate().execute( "ALTER TABLE tCategory ADD cat_sName2 @varchar(255)@" );
        context.getJdbcTemplate().execute( "ALTER TABLE tBinaryData ADD bda_sFileName2 @varchar(255)@" );

        context.reorganizeTablesForDb2( "tUser", "tMenuItem", "tGroup", "tContentVersion", "tContentObject", "tContentHandler", "tCategory",
                                        "tBinaryData" );
    }

    private void setValueTemporaryColumns( final UpgradeContext context )
        throws Exception
    {
        context.getJdbcTemplate().execute( "UPDATE tUser SET usr_sEmail2 = @substring@(usr_sEmail, 1, 255)" );
        context.getJdbcTemplate().execute( "UPDATE tUser SET usr_sUID2 = @substring@(usr_sUID, 1, 255)" );

        context.getJdbcTemplate().execute( "UPDATE tMenuItem SET mei_sURL2 = @substring@(mei_sURL, 1, 255)" );
        context.getJdbcTemplate().execute( "UPDATE tMenuItem SET mei_sName2 = @substring@(mei_sName, 1, 255)" );

        context.getJdbcTemplate().execute( "UPDATE tGroup SET grp_sName2 = @substring@(grp_sName, 1, 255)" );
        context.getJdbcTemplate().execute( "UPDATE tContentVersion SET cov_sTitle2 = @substring@(cov_sTitle, 1, 255)" );
        context.getJdbcTemplate().execute( "UPDATE tContentObject SET cob_sName2 = @substring@(cob_sName, 1, 255)" );
        context.getJdbcTemplate().execute( "UPDATE tContentHandler SET han_sClass2 = @substring@(han_sClass, 1, 255)" );
        context.getJdbcTemplate().execute( "UPDATE tCategory SET cat_sName2 = @substring@(cat_sName, 1, 255)" );
        context.getJdbcTemplate().execute( "UPDATE tBinaryData SET bda_sFileName2 = @substring@(bda_sFileName, 1, 255)" );
    }

    private void dropTableConstraints( final UpgradeContext context )
        throws Exception
    {
        context.dropTableConstraints( "tUser", true );
        context.dropTableConstraints( "tMenuItem", true );
        context.dropTableConstraints( "tGroup", true );
        context.dropTableConstraints( "tContentVersion", true );
        context.dropTableConstraints( "tContentObject", true );
        context.dropTableConstraints( "tContentHandler", true );
        context.dropTableConstraints( "tCategory", true );
        context.dropTableConstraints( "tBinaryData", true );
    }

    private void dropTableColumns( final UpgradeContext context )
        throws Exception
    {
        context.getJdbcTemplate().execute( "ALTER TABLE tUser DROP column usr_sEmail" );
        context.getJdbcTemplate().execute( "ALTER TABLE tUser DROP column usr_sUID" );

        context.getJdbcTemplate().execute( "ALTER TABLE tMenuItem DROP column mei_sURL" );
        context.getJdbcTemplate().execute( "ALTER TABLE tMenuItem DROP column mei_sName" );

        context.getJdbcTemplate().execute( "ALTER TABLE tGroup DROP column grp_sName" );
        context.getJdbcTemplate().execute( "ALTER TABLE tContentVersion DROP column cov_sTitle" );
        context.getJdbcTemplate().execute( "ALTER TABLE tContentObject DROP column cob_sName" );
        context.getJdbcTemplate().execute( "ALTER TABLE tContentHandler DROP column han_sClass" );
        context.getJdbcTemplate().execute( "ALTER TABLE tCategory DROP column cat_sName" );
        context.getJdbcTemplate().execute( "ALTER TABLE tBinaryData DROP column bda_sFileName" );

        context.reorganizeTablesForDb2( "tUser", "tMenuItem", "tGroup", "tContentVersion", "tContentObject", "tContentHandler", "tCategory",
                                        "tBinaryData" );
    }

    private void createNewColumns( final UpgradeContext context )
        throws Exception
    {
        context.getJdbcTemplate().execute( "ALTER TABLE tUser ADD usr_sEmail @varchar(255)@ DEFAULT '-'" );
        context.getJdbcTemplate().execute( "ALTER TABLE tUser ADD usr_sUID @varchar(255)@ DEFAULT '-' NOT NULL" );

        context.getJdbcTemplate().execute( "ALTER TABLE tMenuItem ADD mei_sURL @varchar(255)@ DEFAULT '-'" );
        context.getJdbcTemplate().execute( "ALTER TABLE tMenuItem ADD mei_sName @varchar(255)@ DEFAULT '-' NOT NULL" );

        context.getJdbcTemplate().execute( "ALTER TABLE tGroup ADD grp_sName @varchar(255)@ DEFAULT '-' NOT NULL" );
        context.getJdbcTemplate().execute( "ALTER TABLE tContentVersion ADD cov_sTitle @varchar(255)@ DEFAULT '-' NOT NULL" );
        context.getJdbcTemplate().execute( "ALTER TABLE tContentObject ADD cob_sName @varchar(255)@ DEFAULT '-' NOT NULL" );
        context.getJdbcTemplate().execute( "ALTER TABLE tContentHandler ADD han_sClass @varchar(255)@ DEFAULT '-' NOT NULL" );
        context.getJdbcTemplate().execute( "ALTER TABLE tCategory ADD cat_sName @varchar(255)@ DEFAULT '-' NOT NULL" );
        context.getJdbcTemplate().execute( "ALTER TABLE tBinaryData ADD bda_sFileName @varchar(255)@ DEFAULT '-'" );

        context.reorganizeTablesForDb2( "tUser", "tMenuItem", "tGroup", "tContentVersion", "tContentObject", "tContentHandler", "tCategory",
                                        "tBinaryData" );
    }

    private void setValueNewColumns( final UpgradeContext context )
        throws Exception
    {
        context.getJdbcTemplate().execute( "UPDATE tUser SET usr_sEmail = usr_sEmail2" );
        context.getJdbcTemplate().execute( "UPDATE tUser SET usr_sUID = usr_sUID2" );

        context.getJdbcTemplate().execute( "UPDATE tMenuItem SET mei_sURL = mei_sURL2" );
        context.getJdbcTemplate().execute( "UPDATE tMenuItem SET mei_sName = mei_sName2" );

        context.getJdbcTemplate().execute( "UPDATE tGroup SET grp_sName = grp_sName2" );
        context.getJdbcTemplate().execute( "UPDATE tContentVersion SET cov_sTitle = cov_sTitle2" );
        context.getJdbcTemplate().execute( "UPDATE tContentObject SET cob_sName = cob_sName2" );
        context.getJdbcTemplate().execute( "UPDATE tContentHandler SET han_sClass = han_sClass2" );
        context.getJdbcTemplate().execute( "UPDATE tCategory SET cat_sName = cat_sName2" );
        context.getJdbcTemplate().execute( "UPDATE tBinaryData SET bda_sFileName = bda_sFileName2" );
    }

    private void dropTemporaryColumns( final UpgradeContext context )
        throws Exception
    {
        context.getJdbcTemplate().execute( "ALTER TABLE tUser DROP column usr_sEmail2" );
        context.getJdbcTemplate().execute( "ALTER TABLE tUser DROP column usr_sUID2" );

        context.getJdbcTemplate().execute( "ALTER TABLE tMenuItem DROP column mei_sURL2" );
        context.getJdbcTemplate().execute( "ALTER TABLE tMenuItem DROP column mei_sName2" );

        context.getJdbcTemplate().execute( "ALTER TABLE tGroup DROP column grp_sName2" );
        context.getJdbcTemplate().execute( "ALTER TABLE tContentVersion DROP column cov_sTitle2" );
        context.getJdbcTemplate().execute( "ALTER TABLE tContentObject DROP column cob_sName2" );
        context.getJdbcTemplate().execute( "ALTER TABLE tContentHandler DROP column han_sClass2" );
        context.getJdbcTemplate().execute( "ALTER TABLE tCategory DROP column cat_sName2" );
        context.getJdbcTemplate().execute( "ALTER TABLE tBinaryData DROP column bda_sFileName2" );

        context.reorganizeTablesForDb2( "tUser", "tMenuItem", "tGroup", "tContentVersion", "tContentObject", "tContentHandler", "tCategory",
                                        "tBinaryData" );
    }

    private void createTableConstraints( final UpgradeContext context )
        throws Exception
    {
        context.createTableConstraints( "tUser", true );
        context.createTableConstraints( "tMenuItem", true );
        context.createTableConstraints( "tGroup", true );
        context.createTableConstraints( "tContentVersion", true );
        context.createTableConstraints( "tContentObject", true );
        context.createTableConstraints( "tContentHandler", true );
        context.createTableConstraints( "tCategory", true );
        context.createTableConstraints( "tBinaryData", true );
    }
}
