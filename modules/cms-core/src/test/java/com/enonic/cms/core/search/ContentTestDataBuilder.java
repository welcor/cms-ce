package com.enonic.cms.core.search;


import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.joda.time.DateTime;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentHandlerEntity;
import com.enonic.cms.core.content.ContentHandlerName;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentStatus;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.ContentVersionKey;
import com.enonic.cms.core.content.binary.BinaryDataEntity;
import com.enonic.cms.core.content.binary.ContentBinaryDataEntity;
import com.enonic.cms.core.content.binary.ContentBinaryDataKey;
import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contentdata.custom.BinaryDataEntry;
import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.DateDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contenttype.ContentTypeConfig;
import com.enonic.cms.core.content.contenttype.ContentTypeConfigParser;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfigType;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/29/11
 * Time: 3:11 PM
 */
public class ContentTestDataBuilder
{
    private ContentTypeConfig config;

    private Document standardConfigDoc;

    private ContentEntity content;

    public void buildConfig( String configXml )
        throws Exception
    {

        this.standardConfigDoc = JDOMUtil.parseDocument( configXml.toString() );
        this.config = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, getStandardConfigRoot() );

    }

    public ContentTestDataBuilder createContent( int key, String name )
    {
        ContentHandlerEntity contentHandler = new ContentHandlerEntity();
        contentHandler.setClassName( ContentHandlerName.CUSTOM.getHandlerClassShortName() );

        ContentTypeEntity contentType = new ContentTypeEntity();
        contentType.setHandler( contentHandler );
        contentType.setData( this.getStandardConfigDoc() );

        CategoryEntity cat = new CategoryEntity();
        cat.setKey( new CategoryKey( "1" ) );
        cat.setContentType( contentType );

        final Date now = Calendar.getInstance().getTime();

        ContentEntity content = new ContentEntity();
        content.setCategory( cat );
        content.setKey( new ContentKey( key ) );
        content.setName( name );
        content.setAvailableFrom( now );
        content.setCreatedAt( now );

        this.content = content;

        return this;
    }

    public ContentTestDataBuilder addMainVersion( int key, ContentStatus status )
    {
        ContentVersionEntity contentVersion = new ContentVersionEntity();
        contentVersion.setKey( new ContentVersionKey( key ) );
        contentVersion.setStatus( status );

        this.content.setMainVersion( contentVersion );
        this.content.addVersion( contentVersion );

        return this;
    }

    public ContentTestDataBuilder addCustomContent( Map<String, Object> contentDataMap )
    {
        CustomContentData contentData = new CustomContentData( config );

        for ( String dataEntryName : contentDataMap.keySet() )
        {
            DataEntryConfig dataEntryConfig = config.getInputConfig( dataEntryName );

            if ( dataEntryConfig == null )
            {
                throw new IllegalArgumentException(
                    "Input with name: " + dataEntryName + " not found in config. Please check your config" );
            }

            if ( dataEntryConfig.getType().equals( DataEntryConfigType.TEXT ) )
            {
                contentData.add( new TextDataEntry( dataEntryConfig, contentDataMap.get( dataEntryName ).toString() ) );
            }

            if ( dataEntryConfig.getType().equals( DataEntryConfigType.DATE ) )
            {
                contentData.add( new DateDataEntry( dataEntryConfig, (Date) contentDataMap.get( dataEntryName ) ) );
            }

            if ( dataEntryConfig.getType().equals( DataEntryConfigType.BINARY ) )
            {
                contentData.add( new BinaryDataEntry( dataEntryConfig, contentDataMap.get( dataEntryName ).toString() ) );
            }

        }

        this.content.getMainVersion().setContentData( contentData );

        return this;
    }

    public ContentTestDataBuilder addBinaryData( int key, String blobKey )
    {
        ContentBinaryDataEntity contentBinaryData = new ContentBinaryDataEntity();
        contentBinaryData.setKey( new ContentBinaryDataKey( key ) );
        contentBinaryData.setLabel( "source" );

        BinaryDataEntity binaryData = new BinaryDataEntity();
        binaryData.setKey( key );
        binaryData.setBlobKey( blobKey );

        contentBinaryData.setBinaryData( binaryData );

        this.content.getMainVersion().addContentBinaryData( contentBinaryData );

        return this;
    }


    public DataEntryConfig getDataEntryConfig( String dataEntryName )
    {
        return this.config.getInputConfig( dataEntryName );
    }

    private Element getStandardConfigRoot()
    {
        return this.standardConfigDoc.getRootElement();
    }

    public ContentTypeConfig getConfig()
    {
        return config;
    }

    public void setConfig( ContentTypeConfig config )
    {
        this.config = config;
    }

    public Document getStandardConfigDoc()
    {
        return standardConfigDoc;
    }

    public void setStandardConfigDoc( Document standardConfigDoc )
    {
        this.standardConfigDoc = standardConfigDoc;
    }

    public ContentEntity build()
    {
        return this.content;
    }
}
