package com.enonic.cms.core.search.query.factory.facet.model;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;

import com.enonic.cms.core.search.IndexException;

public final class FacetsModelFactory
{
    public FacetsModel buildFromXml( String xml )
    {
        try
        {
            final JAXBContext context = JAXBContext.newInstance( FacetsModel.class );
            return (FacetsModel) context.createUnmarshaller().unmarshal( new StringReader( xml ) );
        }
        catch ( Exception e )
        {
            throw new IndexException( "Could not build facets from xml", e );
        }
    }
}
