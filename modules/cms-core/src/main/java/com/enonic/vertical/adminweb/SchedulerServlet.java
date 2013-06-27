/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.vertical.adminweb;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.util.DateUtil;
import com.enonic.esl.util.StringUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.adminweb.wizard.Wizard;
import com.enonic.vertical.adminweb.wizard.WizardException;
import com.enonic.vertical.adminweb.wizard.WizardLogger;
import com.enonic.vertical.engine.VerticalEngineException;
import com.enonic.vertical.work.WorkEntry;
import com.enonic.vertical.work.WorkEntryComparator;
import com.enonic.vertical.work.WorkHelper;
import com.enonic.vertical.work.WorkService;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.api.plugin.ext.TaskHandler;
import com.enonic.cms.core.CalendarUtil;
import com.enonic.cms.core.plugin.PluginManager;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.service.AdminService;


public class SchedulerServlet
    extends AdminHandlerBaseServlet
{
    private static SchedulerServlet INSTANCE;

    private static final String WIZARD_CONFIG_CREATE_UPDATE = "wizardconfig_create_update_workentry.xml";

    private static final String ERROR_REPEAT_INTERVAL = "13";

    private static final String ERROR_REPEAT_COUNT = "14";

    private static final String ERROR_MINUTES = "15";

    private static final String ERROR_TIME = "16";

    private static final String ERROR_CRON_EXPRESSION = "17";

    @Autowired
    private WorkService workService;

    @Autowired
    private PluginManager pluginManager;

    public SchedulerServlet()
    {
        INSTANCE = this;
    }

    public static class CreateUpdateWorkEntryWizard
        extends Wizard
    {
        /**
         *
         */
        public CreateUpdateWorkEntryWizard()
        {
            super();
        }


        protected void appendCustomData( WizardState wizardState, HttpSession session, AdminService admin, ExtendedMap formItems,
                                         ExtendedMap parameters, User user, Document dataconfigDoc, Document wizarddataDoc )
            throws WizardException
        {
            String key = formItems.getString( "key", null );
            if ( key != null )
            {
                WorkService workService = INSTANCE.workService;
                WorkEntry workEntry = workService.getEntry( key );
                if ( workEntry == null )
                {
                    WizardLogger.errorWizard( "Unknown work entry {0}", key );
                }
                org.jdom.Document doc = new org.jdom.Document( new org.jdom.Element( "workentries" ) );
                org.jdom.Element workentryElem = WorkHelper.convertToElement( workEntry );
                doc.getRootElement().addContent( workentryElem );
                SchedulerServlet schedulerServlet = (SchedulerServlet) servlet;
                if ( !wizardState.hasError( ERROR_CRON_EXPRESSION ) )
                {
                    schedulerServlet.updateWorkEntryElement( workEntry, workentryElem );
                }
                XMLTool.mergeDocuments( wizarddataDoc, XMLDocumentFactory.create( doc ).getAsDOMDocument(), true );
            }
            XMLTool.mergeDocuments( wizarddataDoc, getTaskPluginsXML(), true );
        }

        private Document getTaskPluginsXML()
        {
            Collection<TaskHandler> plugins = INSTANCE.pluginManager.getExtensions().getAllTaskPlugins();
            return createXmlDocument( plugins ).getAsDOMDocument();
        }

        protected boolean evaluate( WizardState wizardState, HttpSession session, AdminService admin, ExtendedMap formItems,
                                    String testCondition )
            throws WizardException
        {
            // not used
            return false;
        }


        protected void initialize( AdminService admin, Document wizardconfigDoc )
            throws WizardException
        {
            // not used
        }


        protected void processWizardData( WizardState wizardState, HttpSession session, AdminService admin, ExtendedMap formItems,
                                          User user, Document dataDoc )
            throws VerticalAdminException, VerticalEngineException
        {
            WorkEntry workEntry = new WorkEntry();
            String key = formItems.getString( "key", null );
            if ( key != null )
            {
                workEntry.setKey( key );
            }
            workEntry.setName( formItems.getString( "stepstate_workentry_name" ) );
            workEntry.setWorkClass( formItems.getString( "stepstate_workentry_workclass" ) );
            workEntry.setUserName( user.getName() );

            String[] names = formItems.getStringArray( "stepstate_workentry_properties_property_@name" );
            String[] values = formItems.getStringArray( "stepstate_workentry_properties_property_@value" );
            for ( int i = 0; i < names.length; i++ )
            {
                workEntry.setProperty( names[i], values[i] );
            }

            String type = formItems.getString( "__type" );
            if ( "once".equals( type ) )
            {
                workEntry.setMode( WorkEntry.SIMPLE );
                workEntry.setRepeatInterval( 1 );
                workEntry.setRepeatCount( 0 );
            }
            else if ( "infinite".equals( type ) )
            {
                workEntry.setMode( WorkEntry.SIMPLE );
                workEntry.setRepeatInterval( formItems.getInt( "stepstate_workentry_trigger_repeat_@interval" ) );
                workEntry.setRepeatCount( -1 );
            }
            else if ( "repeatedly".equals( type ) )
            {
                workEntry.setMode( WorkEntry.SIMPLE );
                workEntry.setRepeatInterval( formItems.getInt( "stepstate_workentry_trigger_repeat_@interval" ) );
                int repeatCount = formItems.getInt( "stepstate_workentry_trigger_repeat_@count" );
                workEntry.setRepeatCount( repeatCount );
            }
            else if ( "hourly".equals( type ) )
            {
                workEntry.setMode( WorkEntry.CRON );
                String minutes = formItems.getString( "stepstate_workentry_trigger_hourly_@minutes" );
                workEntry.setCronExpression( "0 " + minutes + " * * * ?" );
            }
            else if ( "daily".equals( type ) )
            {
                workEntry.setMode( WorkEntry.CRON );
                String time = formItems.getString( "stepstate_workentry_trigger_daily_@time" );
                String hours = time.substring( 0, time.indexOf( ':' ) );
                String minutes = time.substring( time.indexOf( ':' ) + 1 );
                workEntry.setCronExpression( "0 " + minutes + " " + hours + " * * ?" );
            }
            else
            {
                workEntry.setMode( WorkEntry.CRON );
                String cronExpression = formItems.getString( "stepstate_workentry_trigger_cron" );
                workEntry.setCronExpression( cronExpression );
            }

            try
            {
                String dateStr = formItems.getString( "datestart", null );
                String timeStr = formItems.getString( "timestart", null );
                if ( dateStr != null )
                {
                    dateStr += " " + timeStr;
                    workEntry.setStartTime( DateUtil.parseDateTime( dateStr, false ) );
                }
                dateStr = formItems.getString( "dateend", null );
                timeStr = formItems.getString( "timeend", null );
                if ( dateStr != null )
                {
                    dateStr += " " + timeStr;
                    workEntry.setEndTime( DateUtil.parseDateTime( dateStr, true ) );
                }
            }
            catch ( ParseException pe )
            {
                WizardLogger.errorWizard( "Failed to parse start or end date", pe );
            }

            INSTANCE.workService.addEntry( workEntry, key != null );
        }

        protected boolean validateState( WizardState wizardState, HttpSession session, AdminService admin, ExtendedMap formItems )
        {
            boolean validated = true;
            String type = formItems.getString( "__type" );
            if ( "infinite".equals( type ) || "repeatedly".equals( type ) )
            {
                int interval = formItems.getInt( "stepstate_workentry_trigger_repeat_@interval" );
                if ( interval < 1 )
                {
                    wizardState.addError( ERROR_REPEAT_INTERVAL, "stepstate_workentry_trigger_repeat_@interval" );
                    validated = false;
                }

                if ( "repeatedly".equals( type ) )
                {
                    int count = formItems.getInt( "stepstate_workentry_trigger_repeat_@count" );
                    if ( count < -1 )
                    {
                        wizardState.addError( ERROR_REPEAT_COUNT, "stepstate_workentry_trigger_repeat_@count" );
                        validated = false;
                    }
                }
            }
            else if ( "hourly".equals( type ) )
            {
                int minutes = formItems.getInt( "stepstate_workentry_trigger_hourly_@minutes" );
                if ( minutes < 0 || minutes > 59 )
                {
                    wizardState.addError( ERROR_MINUTES, "stepstate_workentry_trigger_hourly_@minutes" );
                    validated = false;
                }
            }
            else if ( "daily".equals( type ) )
            {
                String time = formItems.getString( "stepstate_workentry_trigger_daily_@time" );
                String hoursStr = time.substring( 0, time.indexOf( ':' ) );
                String minutesStr = time.substring( time.indexOf( ':' ) + 1 );
                try
                {
                    int hours = Integer.parseInt( hoursStr );
                    if ( hours < 0 || hours > 23 )
                    {
                        wizardState.addError( ERROR_TIME, "stepstate_workentry_trigger_daily_@time" );
                        validated = false;
                    }
                    else
                    {
                        int minutes = Integer.parseInt( minutesStr );
                        if ( minutes < 0 || minutes > 59 )
                        {
                            wizardState.addError( ERROR_TIME, "stepstate_workentry_trigger_daily_@time" );
                            validated = false;
                        }
                    }
                }
                catch ( NumberFormatException e )
                {
                    wizardState.addError( ERROR_TIME, "stepstate_workentry_trigger_daily_@time" );
                    validated = false;
                }
            }
            else if ( "custom".equals( type ) )
            {
                WorkEntry workEntry = new WorkEntry();
                workEntry.setMode( WorkEntry.CRON );
                String cronExpression = formItems.getString( "stepstate_workentry_trigger_cron" );
                try
                {
                    workEntry.setCronExpression( cronExpression );
                }
                catch ( IllegalArgumentException iae )
                {
                    WizardLogger.error( "Error in cron expression " + cronExpression );
                    wizardState.addError( ERROR_CRON_EXPRESSION, "stepstate_workentry_trigger_cron" );
                    validated = false;

                    Document stateDoc = wizardState.getCurrentStepState().getStateDoc();
                    Element workentryElem = XMLTool.getElement( stateDoc.getDocumentElement(), "workentry" );
                    Element triggerElem = XMLTool.getElement( workentryElem, "trigger" );
                    Element dailyElem = XMLTool.getElement( triggerElem, "daily" );
                    if ( dailyElem != null )
                    {
                        triggerElem.removeChild( dailyElem );
                    }
                    Element hourlyElem = XMLTool.getElement( triggerElem, "hourly" );
                    if ( hourlyElem != null )
                    {
                        triggerElem.removeChild( hourlyElem );
                    }
                }
            }

            return validated;
        }


        protected void saveState( WizardState wizardState, HttpServletRequest request, HttpServletResponse response, AdminService admin,
                                  User user, ExtendedMap formItems )
            throws WizardException
        {
            try
            {
                String dateStr = formItems.getString( "datestart", null );
                String timeStr = formItems.getString( "timestart", null );
                if ( dateStr != null )
                {
                    if ( timeStr != null )
                    {
                        dateStr = CalendarUtil.formatDate( DateUtil.parseDate( dateStr ).getTime(), false ).substring( 0, 10 ) + " " + timeStr;
                    }
                    else
                    {
                        dateStr = CalendarUtil.formatDate( DateUtil.parseDate( dateStr ).getTime(), false ).substring( 0, 10 ) + " 00:00:00";
                        formItems.put( "timestart", "00:00:00" );
                    }
                }
                else if ( timeStr != null )
                {
                    dateStr = CalendarUtil.formatCurrentDate().substring( 0, 10 ) + timeStr;
                }
                formItems.put( "stepstate_workentry_trigger_time_@start", dateStr );

                dateStr = formItems.getString( "dateend", null );
                timeStr = formItems.getString( "timeend", null );
                if ( dateStr != null )
                {
                    if ( timeStr != null )
                    {
                        dateStr = CalendarUtil.formatDate( DateUtil.parseDate( dateStr ).getTime(), false ).substring( 0, 10 ) + " " + timeStr;
                    }
                    else
                    {
                        dateStr = CalendarUtil.formatDate( DateUtil.parseDate( dateStr ).getTime(), false ).substring( 0, 10 ) + " 00:00:00";
                        formItems.put( "timeend", "00:00:00" );
                    }
                }
                else if ( timeStr != null )
                {
                    dateStr = CalendarUtil.formatCurrentDate().substring( 0, 10 ) + timeStr;
                }
                formItems.put( "stepstate_workentry_trigger_time_@end", dateStr );
            }
            catch ( ParseException pe )
            {
                WizardLogger.errorWizard( "Failed to parse date", pe );
            }

            String type = formItems.getString( "__type" );
            if ( "infinite".equals( type ) && formItems.containsKey( "stepstate_workentry_trigger_repeat_@count" ) == false )
            {
                formItems.put( "stepstate_workentry_trigger_repeat_@count", -1 );
            }

            super.saveState( wizardState, request, response, admin, user, formItems );
        }
    }


    public void handlerBrowse( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, ExtendedMap parameters, User user, Document verticalDoc )
        throws VerticalAdminException, TransformerException, IOException
    {
        WorkEntry[] workEntries = workService.getEntries();
        Arrays.sort( workEntries, WorkEntryComparator.INSTANCE );

        org.jdom.Document doc = new org.jdom.Document( new org.jdom.Element( "workentries" ) );
        org.jdom.Element root = doc.getRootElement();
        for ( int i = 0; i < workEntries.length; i++ )
        {
            org.jdom.Element workentryElem = WorkHelper.convertToElement( workEntries[i] );
            root.addContent( workentryElem );
            updateWorkEntryElement( workEntries[i], workentryElem );
        }
        XMLTool.mergeDocuments( verticalDoc, XMLDocumentFactory.create( doc ).getAsDOMDocument(), true );

        // transform document
        DOMSource xmlSource = new DOMSource( verticalDoc );
        Source xslSource = AdminStore.getStylesheet( session, "workentry_browse.xsl" );
        transformXML( session, response.getWriter(), xmlSource, xslSource, parameters );
    }

    private void updateWorkEntryElement( WorkEntry entry, org.jdom.Element workentryElem )
    {
        if ( entry.getMode() == WorkEntry.CRON )
        {
            StringTokenizer cronExpression = new StringTokenizer( entry.getCronExpression(), " " );
            // seconds
            String seconds = cronExpression.nextToken();
            // minutes
            String minutes = cronExpression.nextToken();
            // hours
            String hours = cronExpression.nextToken();
            // day of month
            String dayOfMonth = cronExpression.nextToken();
            // day of month
            String month = cronExpression.nextToken();
            // day of week
            String dayOfWeek = cronExpression.nextToken();
            // has year
            boolean hasYear = cronExpression.hasMoreTokens();

            org.jdom.Element triggerElem = workentryElem.getChild( "trigger" );
            if ( "0".equals( seconds ) && "*".equals( dayOfMonth ) && "*".equals( month ) && "?".equals( dayOfWeek ) && hasYear == false )
            {
                if ( "*".equals( hours ) )
                {
                    org.jdom.Element hourlyElem = new org.jdom.Element( "hourly" );
                    hourlyElem.setAttribute( "minutes", minutes );
                    triggerElem.addContent( hourlyElem );
                }
                else if ( StringUtil.isIntegerString( hours ) )
                {
                    org.jdom.Element dailyElem = new org.jdom.Element( "daily" );
                    dailyElem.setAttribute( "time", hours + ":" + minutes );
                    triggerElem.addContent( dailyElem );
                }
            }
        }
    }


    public void handlerWizard( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, ExtendedMap parameters, User user, String wizardName )
        throws VerticalAdminException, VerticalEngineException, TransformerException, IOException
    {
        if ( "createupdate".equals( wizardName ) )
        {
            Wizard createUpdateWizard = Wizard.getInstance( admin, applicationContext, this, session, formItems, WIZARD_CONFIG_CREATE_UPDATE );
            createUpdateWizard.processRequest( request, response, session, admin, formItems, parameters, user );
        }
        else
        {
            super.handlerWizard( request, response, session, admin, formItems, parameters, user, wizardName );
        }
    }


    public void handlerRemove( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, String key )
        throws VerticalAdminException, VerticalEngineException
    {
        workService.deleteEntry( key );
        ExtendedMap params = new ExtendedMap();
        params.put( "page", formItems.getString( "page" ) );
        params.put( "op", "browse" );
        redirectClientToAdminPath( "adminpage", params, request, response );
    }

    private static XMLDocument createXmlDocument( Collection<TaskHandler> plugins )
    {
        org.jdom.Element root = new org.jdom.Element( "task-plugins" );
        if ( plugins != null )
        {
            for ( TaskHandler plugin : plugins )
            {
                org.jdom.Element pluginElement = new org.jdom.Element( "task-plugin" );
                pluginElement.setAttribute( "display-name", plugin.getDisplayName() );

                if ( plugin.getName() != null )
                {
                    pluginElement.setAttribute( "name", plugin.getName() );
                }
                pluginElement.setAttribute( "class", plugin.getClass().getName() );
                root.addContent( pluginElement );
            }
        }
        return XMLDocumentFactory.create( new org.jdom.Document( root ) );
    }
}
