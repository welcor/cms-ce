/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.upgrade.task;

import com.enonic.cms.upgrade.UpgradeContext;

final class UpgradeModel0212
    extends AbstractUpgradeTask
{
    public UpgradeModel0212()
    {
        super( 212 );
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
        context.getJdbcTemplate().execute( "UPDATE tUser SET usr_sEmail2 = usr_sEmail" );
        context.getJdbcTemplate().execute( "UPDATE tUser SET usr_sUID2 = usr_sUID" );

        context.getJdbcTemplate().execute( "UPDATE tMenuItem SET mei_sURL2 = mei_sURL" );
        context.getJdbcTemplate().execute( "UPDATE tMenuItem SET mei_sName2 = mei_sName" );

        context.getJdbcTemplate().execute( "UPDATE tGroup SET grp_sName2 = grp_sName" );
        context.getJdbcTemplate().execute( "UPDATE tContentVersion SET cov_sTitle2 = cov_sTitle" );
        context.getJdbcTemplate().execute( "UPDATE tContentObject SET cob_sName2 = cob_sName" );
        context.getJdbcTemplate().execute( "UPDATE tContentHandler SET han_sClass2 = han_sClass" );
        context.getJdbcTemplate().execute( "UPDATE tCategory SET cat_sName2 = cat_sName" );
        context.getJdbcTemplate().execute( "UPDATE tBinaryData SET bda_sFileName2 = bda_sFileName" );
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

        context.reorganizeTablesForDb2( "tUser", "tMenuItem", "tGroup", "tContentVersion", "tContentObject", "tContentHandler", "tCategory", "tBinaryData" );
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
