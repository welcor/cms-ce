/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.facet.model;

import java.io.StringReader;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;

import com.enonic.cms.core.search.facet.FacetQueryException;

public final class FacetsModelFactory
{

    public FacetsModel buildFromXml( String xml )
    {
        Object unmarshal;
        final CustomValidationEventHandler customValidationEventHandler = new CustomValidationEventHandler();

        try
        {
            final JAXBContext context = JAXBContext.newInstance( FacetsModel.class );
            final Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setEventHandler( customValidationEventHandler );
            unmarshal = unmarshaller.unmarshal( new StringReader( xml ) );
        }
        catch ( Exception e )
        {
            throw new FacetQueryException( "Could not build facets from xml", e );
        }

        handlerValidationErrors( customValidationEventHandler );
        return (FacetsModel) unmarshal;
    }

    private void handlerValidationErrors( final CustomValidationEventHandler customValidationEventHandler )
    {
        final List<ValidationEvent> errors = customValidationEventHandler.getErrors();

        if ( !errors.isEmpty() )
        {
            StringBuilder errorBuilder = new StringBuilder();

            for ( ValidationEvent error : errors )
            {
                errorBuilder.append( error.getMessage() + "\n" );
            }

            throw new FacetQueryException( "Failed to build facet: " + errorBuilder.toString() );
        }

    }


}
