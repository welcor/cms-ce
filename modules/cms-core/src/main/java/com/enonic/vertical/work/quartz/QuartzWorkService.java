/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.vertical.work.quartz;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.vertical.work.WorkEntry;
import com.enonic.vertical.work.WorkException;
import com.enonic.vertical.work.WorkService;

import com.enonic.cms.api.plugin.ext.TaskHandler;
import com.enonic.cms.core.plugin.ext.TaskHandlerExtensions;

public final class QuartzWorkService
    implements WorkService
{
    private final static String DEFAULT_GROUP = "default";

    private TaskHandlerExtensions extensions;

    private QuartzSchedulerManager schedulerManager;

    @Autowired
    public void setExtensions( final TaskHandlerExtensions extensions )
    {
        this.extensions = extensions;
    }

    public boolean isEnabled()
    {
        return this.schedulerManager.isEnabled();
    }

    @Autowired
    public void setSchedulerManager( final QuartzSchedulerManager schedulerManager )
    {
        this.schedulerManager = schedulerManager;
    }

    private Scheduler getScheduler()
        throws WorkException
    {
        if ( isEnabled() )
        {
            return this.schedulerManager.getScheduler();
        }
        else
        {
            throw new WorkException( "Scheduler is not enabled" );
        }
    }

    private JobDetail createJobDetail( WorkEntry from )
    {
        JobDetail to = new JobDetail();
        to.setName( from.getKey() );
        to.setGroup( DEFAULT_GROUP );
        to.setDescription( from.getName() );
        to.setDurability( false );
        to.setJobClass( WorkJobAdapter.class );
        to.setJobDataMap( createJobDataMap( from ) );
        return to;
    }

    private Trigger createTrigger( WorkEntry from )
    {
        Trigger trigger = (Trigger) from.getTriggerPeer();
        trigger.setName( from.getKey() );
        trigger.setGroup( DEFAULT_GROUP );
        return trigger;
    }

    private WorkEntry createEntry( JobDetail job, Trigger trigger )
    {
        WorkEntry to = new WorkEntry();
        to.setKey( job.getName() );
        to.setName( job.getDescription() );

        JobDataMap map = job.getJobDataMap();
        to.setWorkClass( map.getString( WorkJobAdapter.CLASS_KEY ) );

        for ( Iterator i = map.keySet().iterator(); i.hasNext(); )
        {
            String key = (String) i.next();

            if ( isPublicProperty( key ) )
            {
                to.setProperty( key, map.getString( key ) );
            }
        }

        to.setTriggerPeer( trigger );
        return to;
    }

    private boolean isPublicProperty( String key )
    {
        if ( key.equals( WorkJobAdapter.CLASS_KEY ) )
        {
            return false;
        }
        else if ( key.equals( WorkEntry.PROP_USERNAME ) )
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    private JobDataMap createJobDataMap( WorkEntry entry )
    {
        JobDataMap map = new JobDataMap();
        map.put( WorkJobAdapter.CLASS_KEY, entry.getWorkClass() );

        String[] props = entry.getPropertyNames();
        for ( int i = 0; i < props.length; i++ )
        {
            map.put( props[i], entry.getProperty( props[i] ) );
        }

        return map;
    }

    public void addEntry( WorkEntry task )
        throws WorkException
    {
        addEntry( task, false );
    }

    public void addEntry( WorkEntry work, boolean replace )
        throws WorkException
    {
        if ( work != null )
        {
            validateEntry( work );
            WorkEntry oldEntry = null;
            if ( replace )
            {
                oldEntry = getEntry( work.getKey() );
                deleteEntry( work );
            }

            JobDetail job = createJobDetail( work );
            Trigger trigger = createTrigger( work );

            try
            {
                Scheduler scheduler = getScheduler();
                scheduler.scheduleJob( job, trigger );
            }
            catch ( SchedulerException e )
            {
                addEntry( oldEntry, false );
                throw new WorkException( "Failed to add work entry", e );
            }
        }
    }

    public void deleteEntry( WorkEntry work )
        throws WorkException
    {
        deleteEntry( work.getKey() );
    }

    public void deleteEntry( String key )
        throws WorkException
    {
        try
        {
            Scheduler scheduler = getScheduler();
            scheduler.deleteJob( key, DEFAULT_GROUP );
        }
        catch ( SchedulerException e )
        {
            throw new WorkException( "Failed to delete work entry", e );
        }
    }

    public WorkEntry[] getEntries()
        throws WorkException
    {
        List<WorkEntry> entries = new LinkedList<WorkEntry>();
        List<JobDetail> jobDetailList = getJobDetails();

        for ( JobDetail detail : jobDetailList )
        {
            Trigger trigger = getTrigger( detail );
            entries.add( createEntry( detail, trigger ) );
        }

        return entries.toArray( new WorkEntry[entries.size()] );
    }

    private JobDetail getJobDetail( String key )
        throws WorkException
    {
        try
        {
            Scheduler scheduler = getScheduler();
            return scheduler.getJobDetail( key, DEFAULT_GROUP );
        }
        catch ( SchedulerException e )
        {
            throw new WorkException( e.getMessage() );
        }
    }

    private Trigger getTrigger( JobDetail job )
        throws WorkException
    {
        try
        {
            Scheduler scheduler = getScheduler();
            Trigger[] triggers = scheduler.getTriggersOfJob( job.getName(), job.getGroup() );
            if ( triggers.length > 0 )
            {
                return triggers[0];
            }
        }
        catch ( SchedulerException e )
        {
            throw new WorkException( e.getMessage(), e );
        }

        return null;
    }

    public String[] getEntryKeys()
        throws WorkException
    {
        try
        {
            Scheduler scheduler = getScheduler();
            return scheduler.getJobNames( DEFAULT_GROUP );
        }
        catch ( SchedulerException e )
        {
            throw new WorkException( e.getMessage() );
        }
    }

    private List<JobDetail> getJobDetails()
        throws WorkException
    {
        try
        {
            Scheduler scheduler = getScheduler();
            LinkedList<JobDetail> list = new LinkedList<JobDetail>();
            String[] names = scheduler.getJobNames( DEFAULT_GROUP );
            for ( int i = 0; i < names.length; i++ )
            {
                list.add( scheduler.getJobDetail( names[i], DEFAULT_GROUP ) );
            }

            return list;
        }
        catch ( SchedulerException e )
        {
            throw new WorkException( e.getMessage(), e );
        }
    }

    /**
     * Return a single entry.
     */
    public WorkEntry getEntry( String key )
        throws WorkException
    {
        JobDetail job = getJobDetail( key );
        if ( job != null )
        {
            Trigger trigger = getTrigger( job );
            return createEntry( job, trigger );
        }
        else
        {
            return null;
        }
    }

    /**
     * Checks the entry and makes sure there's a valid TaskPlugin class, backing this work entry.
     *
     * @param entry The class specifying the task.
     * @throws WorkException If no TaskPlugin is found.
     */
    private void validateEntry( WorkEntry entry )
        throws WorkException
    {
        String clzName = entry.getWorkClass();
        TaskHandler taskPlugin = this.extensions.getByName( clzName );
        if ( taskPlugin == null )
        {
            throw new WorkException( "Work class " + clzName + " not valid" );
        }
    }
}
