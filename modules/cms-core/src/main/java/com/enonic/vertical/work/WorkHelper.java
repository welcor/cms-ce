/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.vertical.work;

import java.util.Date;
import java.util.Properties;

import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.cms.api.plugin.ext.TaskHandler;
import com.enonic.cms.core.CalendarUtil;
import com.enonic.cms.core.plugin.ext.TaskHandlerExtensions;

public final class WorkHelper
{
    private final static Logger LOG = LoggerFactory.getLogger( WorkHelper.class );

    private WorkHelper()
    {
    }

    public static void executeWork( final TaskHandlerExtensions extensions, String className, Properties props )
        throws Exception
    {
        TaskHandler p = extensions.getByName( className.replaceAll( ".*\\.", "" ) );
        if ( p != null )
        {
            p.execute( props );
        }
        else
        {
            LOG.error( "TaskPlugin with name: " + className + ", does not exist." );
            throw new IllegalStateException( "No plugin of class, " + className + " found." );
        }
    }

    public static Element convertToElement( WorkEntry entry )
    {
        Element root = new Element( "workentry" );
        root.setAttribute( "key", entry.getKey() );
        root.addContent( new Element( "name" ).addContent( entry.getName() ) );
        root.addContent( new Element( "workclass" ).addContent( entry.getWorkClass() ) );

        Element propertiesElem = new Element( "properties" );
        root.addContent( propertiesElem );
        String[] propertyNames = entry.getPropertyNames();
        for ( int i = 0; i < propertyNames.length; i++ )
        {
            Element propertyElem = new Element( "property" );
            propertyElem.setAttribute( "name", propertyNames[i] );
            propertyElem.setAttribute( "value", entry.getProperty( propertyNames[i] ) );
            propertiesElem.addContent( propertyElem );
        }

        Element triggerElem = new Element( "trigger" );
        root.addContent( triggerElem );
        triggerElem.setAttribute( "type", ( entry.getMode() == WorkEntry.SIMPLE ? "simple" : "cron" ) );

        Element timeElem = new Element( "time" );
        triggerElem.addContent( timeElem );
        timeElem.setAttribute( "start", CalendarUtil.formatDate( entry.getStartTime().getTime(), true ) );
        Date endDate = entry.getEndTime();
        if ( endDate != null )
        {
            timeElem.setAttribute( "end", CalendarUtil.formatDate( endDate.getTime(), true ) );
        }
        Date nextDate = entry.getNextFireTime();
        if ( nextDate != null )
        {
            timeElem.setAttribute( "next", CalendarUtil.formatDate( nextDate.getTime(), true ) );
        }
        Date previousDate = entry.getPreviousFireTime();
        if ( previousDate != null )
        {
            timeElem.setAttribute( "previous", CalendarUtil.formatDate( previousDate.getTime(), true ) );
        }
        Date finalDate = entry.getFinalFireTime();
        if ( finalDate != null )
        {
            timeElem.setAttribute( "final", CalendarUtil.formatDate( finalDate.getTime(), true ) );
        }

        if ( entry.getMode() == WorkEntry.SIMPLE )
        {
            Element repeatElem = new Element( "repeat" );
            triggerElem.addContent( repeatElem );
            repeatElem.setAttribute( "count", String.valueOf( entry.getRepeatCount() ) );
            repeatElem.setAttribute( "interval", String.valueOf( entry.getRepeatInterval() ) );
        }
        else
        {
            Element cronElem = new Element( "cron" );
            triggerElem.addContent( cronElem );
            cronElem.addContent( entry.getCronExpression() );
        }

        return root;
    }
}
