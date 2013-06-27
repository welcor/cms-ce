/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.VerticalEngineLogger;
import com.enonic.vertical.engine.filters.Filter;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.CalendarUtil;
import com.enonic.cms.core.content.category.UnitEntity;
import com.enonic.cms.core.content.category.UnitKey;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;

@Component
public class UnitHandler
    extends BaseHandler
{
    private final static String UNI_SELECT = "SELECT uni_lKey,uni_lan_lKey,lan_sDescription,uni_sName,uni_sDescription," +
        "uni_lSuperKey,uni_bDeleted,uni_dteTimestamp,cat_lKey,cat_sName" + " FROM tUnit" + " JOIN tLanguage ON uni_lan_lKey=lan_lKey" +
        " JOIN tCategory ON tCategory.cat_uni_lKey = tUnit.uni_lKey" + " WHERE (uni_bDeleted=0) AND cat_cat_lSuper IS NULL";

    private final static String UNI_SELECT_NAME =
        "SELECT uni_lKey, uni_sName, cat_sName, cat_lKey, lan_lKey, lan_sDescription, lan_sCode" + " FROM tUnit" +
            " JOIN tLanguage ON tLanguage.lan_lKey = tUnit.uni_lan_lKey" + " JOIN tCategory ON tCategory.cat_uni_lKey = tUnit.uni_lKey" +
            " WHERE (uni_bDeleted=0) AND cat_cat_lSuper IS NULL ";

    private final static String UNI_WHERE_CLAUSE = " uni_lKey=?";


    /**
     * @param unitKey int
     * @return String
     */
    public XMLDocument getUnit( int unitKey )
    {
        StringBuffer sql = new StringBuffer( UNI_SELECT );
        sql.append( " AND" );
        sql.append( UNI_WHERE_CLAUSE );
        int[] paramValue = {unitKey};

        return XMLDocumentFactory.create( getUnit( sql.toString(), paramValue ) );
    }

    private Document getUnit( String sql, int[] paramValue )
    {
        sql += " ORDER BY uni_sName ASC";

        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        Document doc = null;

        try
        {
            doc = XMLTool.createDocument( "units" );
            Element root = doc.getDocumentElement();

            Connection con = getConnection();
            preparedStmt = con.prepareStatement( sql );
            int length = ( paramValue != null ? paramValue.length : 0 );
            for ( int i = 0; i < length; i++ )
            {
                preparedStmt.setInt( i + 1, paramValue[i] );
            }
            resultSet = preparedStmt.executeQuery();

            while ( resultSet.next() )
            {
                int unitkey = resultSet.getInt( "uni_lKey" );
                // int sitekey = resultSet.getInt("uni_sit_lKey");

                Element elem = XMLTool.createElement( doc, root, "unit" );
                elem.setAttribute( "key", Integer.toString( unitkey ) );
                // elem.setAttribute("sitekey", Integer.toString(sitekey));
                String superkey = resultSet.getString( "uni_lSuperKey" );
                if ( !resultSet.wasNull() )
                {
                    elem.setAttribute( "superkey", superkey );
                }
                elem.setAttribute( "languagekey", resultSet.getString( "uni_lan_lKey" ) );
                elem.setAttribute( "language", resultSet.getString( "lan_sDescription" ) );
                elem.setAttribute( "categorykey", resultSet.getString( "cat_lKey" ) );
                elem.setAttribute( "categoryname", resultSet.getString( "cat_sName" ) );

                // sub-elements
                XMLTool.createElement( doc, elem, "name", resultSet.getString( "uni_sName" ) );
                String description = resultSet.getString( "uni_sDescription" );
                if ( !resultSet.wasNull() )
                {
                    XMLTool.createElement( doc, elem, "description", description );
                }

                Timestamp timestamp = resultSet.getTimestamp( "uni_dteTimestamp" );
                XMLTool.createElement( doc, elem, "timestamp", CalendarUtil.formatTimestamp( timestamp, true ) );

                Element ctyElem = XMLTool.createElement( doc, elem, "contenttypes" );
                int[] contentTypeKeys = getUnitContentTypes( unitkey );
                for ( int contentTypeKey : contentTypeKeys )
                {
                    XMLTool.createElement( doc, ctyElem, "contenttype" ).setAttribute( "key", String.valueOf( contentTypeKey ) );
                }
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get units: %t";
            VerticalEngineLogger.error( message, sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
        }

        return doc;
    }

    public String getUnitName( int unitKey )
    {
        final UnitEntity entity = this.unitDao.findByKey( new UnitKey( unitKey ) );
        return entity != null ? entity.getName() : null;
    }

    public Document getUnitNamesXML( Filter filter )
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        Document doc;

        try
        {
            doc = XMLTool.createDocument( "unitnames" );
            Element root = doc.getDocumentElement();

            StringBuffer sql = new StringBuffer( UNI_SELECT_NAME );
            sql.append( " ORDER BY uni_sName ASC" );

            con = getConnection();
            preparedStmt = con.prepareStatement( sql.toString() );
            resultSet = preparedStmt.executeQuery();

            while ( resultSet.next() )
            {
                if ( filter != null && filter.filter( baseEngine, resultSet ) )
                {
                    continue;
                }

                String siteName = resultSet.getString( "uni_sName" );
                Element unitname = XMLTool.createElement( doc, root, "unitname", siteName );
                unitname.setAttribute( "key", resultSet.getString( "uni_lKey" ) );
                unitname.setAttribute( "categoryname", resultSet.getString( "cat_sName" ) );
                unitname.setAttribute( "categorykey", resultSet.getString( "cat_lKey" ) );
                unitname.setAttribute( "languagekey", resultSet.getString( "lan_lKey" ) );
                unitname.setAttribute( "language", resultSet.getString( "lan_sDescription" ) );
                unitname.setAttribute( "languagecode", resultSet.getString( "lan_sCode" ) );
            }

            resultSet.close();
            resultSet = null;
            preparedStmt.close();
            preparedStmt = null;
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get unit names: %t";
            VerticalEngineLogger.error( message, sqle );
            doc = null;
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
        }

        return doc;
    }

    public XMLDocument getUnits()
    {
        String sql = UNI_SELECT;
        return XMLDocumentFactory.create( getUnit( sql, null ) );
    }

    private int[] getUnitContentTypes( int unitKey )
    {
        final UnitEntity entity = this.unitDao.findByKey( new UnitKey( unitKey ) );
        if ( entity == null )
        {
            return new int[0];
        }

        final Set<ContentTypeEntity> contentTypes = entity.getContentTypes();

        int index = 0;
        final int[] result = new int[contentTypes.size()];
        for ( final ContentTypeEntity contentType : contentTypes )
        {
            result[index++] = contentType.getKey();
        }

        return result;
    }

    public int getUnitLanguageKey( int unitKey )
    {
        final UnitEntity entity = this.unitDao.findByKey( new UnitKey( unitKey ) );
        return entity != null ? entity.getLanguage().getKey().toInt() : -1;
    }
}
