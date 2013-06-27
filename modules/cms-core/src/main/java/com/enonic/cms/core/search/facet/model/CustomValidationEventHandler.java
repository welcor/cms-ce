/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.facet.model;

import java.util.List;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

import com.google.common.collect.Lists;

public class CustomValidationEventHandler
    implements ValidationEventHandler
{

    private List<ValidationEvent> errors = Lists.newArrayList();

    public boolean handleEvent( ValidationEvent event )
    {
        final int severity = event.getSeverity();

        if ( severity > 0 )
        {
            errors.add( event );
        }

        return true;
    }

    public List<ValidationEvent> getErrors()
    {
        return errors;
    }
}
