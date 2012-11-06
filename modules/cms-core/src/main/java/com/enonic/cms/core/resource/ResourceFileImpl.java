/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.util.FileCopyUtils;

import com.enonic.cms.framework.io.UnicodeInputStream;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

final class ResourceFileImpl
    extends ResourceBaseImpl
    implements ResourceFile
{
    public ResourceFileImpl( FileResourceService service, FileResourceName name )
    {
        super( service, name );
    }

    public String getMimeType()
    {
        return ensureResource().getMimeType();
    }

    public long getSize()
    {
        return ensureResource().getSize();
    }

    public XMLDocument getDataAsXml()
    {
        return doGetDataAsXml( true );
    }

    public String getDataAsString()
    {
        FileResourceData data = this.service.getResourceData( this.name );
        return data != null ? data.getAsString() : null;
    }

    public byte[] getDataAsByteArray()
    {
        FileResourceData data = this.service.getResourceData( this.name );
        return data != null ? data.getAsBytes() : null;
    }

    public InputStream getDataAsInputStream()
    {
        byte[] data = getDataAsByteArray();
        return data != null ? new ByteArrayInputStream( data ) : null;
    }

    private ByteArrayOutputStream getAsByteArrayOutputStream( boolean skipBOM )
    {
        try
        {
            InputStream in = doGetDataAsInputStream( skipBOM );
            if ( in == null )
            {
                return null;
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            FileCopyUtils.copy( in, out );
            return out;
        }
        catch ( IOException e )
        {
            return null;
        }
    }

    private XMLDocument doGetDataAsXml( boolean skipBOM )
    {
        byte[] data = doGetDataAsByteArray( skipBOM );
        if ( data == null )
        {
            return null;
        }
        return XMLDocumentFactory.create( data, "UTF-8" );
    }

    private byte[] doGetDataAsByteArray( boolean skipBOM )
    {
        ByteArrayOutputStream out = getAsByteArrayOutputStream( skipBOM );
        if ( out == null )
        {
            return null;
        }
        return out.toByteArray();
    }

    private InputStream doGetDataAsInputStream( boolean skipBOM )
    {
        try
        {
            return new UnicodeInputStream( new ByteArrayInputStream( getDataAsByteArray() ), skipBOM );
        }
        catch ( Exception e )
        {
            throw new IllegalArgumentException( e );
        }
    }
}
