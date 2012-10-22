package com.enonic.cms.upgrade.task;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.jdbc.core.RowMapper;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

import com.enonic.cms.framework.blob.BlobKey;

import com.enonic.cms.upgrade.UpgradeContext;
import com.enonic.cms.upgrade.UpgradeException;

final class UpgradeModel0204
    extends AbstractUpgradeTask
{

    private static final String GET_CHILDREN_OF_SQL = "select * from tVirtualFile where vf_sParentKey = ?";

    private static final String ROOT_KEY = DigestUtils.shaHex( "/".getBytes() );

    private File resourceRoot;

    private File blobstoreRoot;

    public UpgradeModel0204()
    {
        super( 204 );
    }

    @Override
    public void upgrade( final UpgradeContext context )
        throws Exception
    {
        context.logInfo( "Moving all resource files from blobstore to resource folder" );

        initBlobstore( context );
        initResourceRoot( context );

        VirtualFileItem root = getRootElement( context );
        processFiles( context, root, "" );
    }

    private void initResourceRoot( final UpgradeContext context )
        throws UpgradeException
    {
        resourceRoot = new File( context.getProperty( "cms.resource.path" ) );

        if ( resourceRoot.exists() )
        {
            context.logWarning( "Resource folder " + resourceRoot.getAbsolutePath() + " already exists" );
        }

        context.logInfo( "Initializing resource root at " + resourceRoot.getAbsolutePath() );
        resourceRoot.mkdirs();

        if ( !resourceRoot.exists() )
        {
            throw new UpgradeException( "Not able to create resource folder " + resourceRoot.getAbsolutePath() );
        }
    }

    private void initBlobstore( final UpgradeContext context )
        throws UpgradeException
    {
        blobstoreRoot = new File( context.getProperty( "cms.blobstore.dir" ) );

        if ( !blobstoreRoot.exists() || !blobstoreRoot.canRead() )
        {
            throw new UpgradeException( ( "Cannot read blobstore " + blobstoreRoot.getAbsolutePath() ) );
        }
    }

    private void processFiles( final UpgradeContext context, final VirtualFileItem root, final String currentPath )
        throws Exception
    {
        final List<VirtualFileItem> elementsWithParent = getElementsWithParent( context, root.key );

        for ( VirtualFileItem item : elementsWithParent )
        {
            final String itemPath = createPath( currentPath, item );

            final boolean isFolder = item.length < 0;

            if ( isFolder )
            {
                createFolder( context, itemPath );
                processFiles( context, item, itemPath );
            }
            else
            {
                createFile( context, item, itemPath );
            }
        }
    }

    private boolean createFolder( UpgradeContext context, String path )
    {
        context.logInfo( "Create folder: " + path );

        File newFolder = new File( resourceRoot + "/" + path );

        if ( newFolder.exists() )
        {
            return false;
        }

        return newFolder.mkdirs();
    }

    private boolean createFile( UpgradeContext context, VirtualFileItem fileItem, String path )
    {
        context.logInfo( "Copy file: " + path );

        if ( Strings.isNullOrEmpty( fileItem.blobkey ) )
        {
            context.logWarning( "No blobkey found for file " + fileItem.name );
            return false;
        }

        File blobFile = getBlobFile( new BlobKey( fileItem.blobkey ) );

        if ( !blobFile.exists() )
        {
            context.logWarning( "No blobfile found for file with name " + fileItem.name );
            return false;
        }

        File newFile = new File( resourceRoot + "/" + path );

        try
        {
            Files.copy( blobFile, newFile );
            return true;
        }
        catch ( IOException e )
        {
            context.logError( "Not able to copy file " + blobFile.getAbsolutePath() + " to " + newFile.getAbsolutePath(), e );
            return false;
        }
    }

    private File getBlobFile( final BlobKey key )
    {
        final String id = key.toString();
        File file = blobstoreRoot;
        file = new File( file, id.substring( 0, 2 ) );
        file = new File( file, id.substring( 2, 4 ) );
        file = new File( file, id.substring( 4, 6 ) );
        return new File( file, id );
    }

    private String createPath( final String currentPath, final VirtualFileItem item )
    {
        return currentPath + "/" + item.name;
    }

    private List<VirtualFileItem> getElementsWithParent( UpgradeContext context, String parentKey )
        throws Exception
    {
        List<VirtualFileItem> children = Lists.newArrayList();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try
        {
            conn = context.getConnection();

            ps = conn.prepareStatement( GET_CHILDREN_OF_SQL );
            ps.setString( 1, parentKey );
            rs = ps.executeQuery();

            while ( rs.next() )
            {
                children.add( createVirtualFileItem( rs ) );
            }
        }
        finally
        {
            context.close( rs );
            context.close( ps );
            context.close( conn );
        }

        return children;
    }


    private VirtualFileItem getRootElement( UpgradeContext context )
        throws Exception
    {
        context.logInfo( "Fetching root with key: " + ROOT_KEY );

        final List<VirtualFileItem> rootItems =
            context.getJdbcTemplate().query( "SELECT * FROM tVirtualFile where vf_skey = '" + ROOT_KEY + "'",
                                             new VirtualFileItemExtractor() );
        if ( rootItems.size() != 1 )
        {
            throw new RuntimeException( "Expected one root-object" );
        }

        return rootItems.get( 0 );
    }

    private static VirtualFileItem createVirtualFileItem( final ResultSet rs )
        throws SQLException
    {
        VirtualFileItem item = new VirtualFileItem();

        final String vf_skey = rs.getString( "vf_skey" );
        item.key = vf_skey;
        item.name = rs.getString( "vf_sname" );
        item.parentKey = rs.getString( "vf_sparentkey" );
        item.length = rs.getLong( "vf_llength" );
        item.blobkey = rs.getString( "vf_sblobkey" );
        return item;
    }

    private static class VirtualFileItemExtractor
        implements RowMapper
    {
        @Override
        public Object mapRow( final ResultSet resultSet, final int i )
            throws SQLException
        {
            return createVirtualFileItem( resultSet );
        }
    }

    private static class VirtualFileItem
    {
        String key;

        String parentKey;

        String name;

        long length;

        String blobkey;
    }
}
