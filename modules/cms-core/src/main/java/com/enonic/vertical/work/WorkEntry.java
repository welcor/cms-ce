package com.enonic.vertical.work;

import java.io.Serializable;
import java.util.Date;
import java.util.Properties;

import org.quartz.CronTrigger;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

import com.enonic.esl.util.UUID;

/**
 * This class defines the job task.
 */
public final class WorkEntry
    implements Serializable
{
    public final static String PROP_USERNAME = "username";

    /**
     * Work entry types.
     */
    public final static int SIMPLE = 0;

    public final static int CRON = 1;

    /**
     * Ms in sec.
     */
    private final static long MS_IN_SEC = 1000;

    /**
     * Key.
     */
    private String key;

    /**
     * Name.
     */
    private String name;

    /**
     * Work class.
     */
    private String workClass;

    /**
     * Properties.
     */
    private final Properties properties;

    /**
     * Peer trigger.
     */
    private Trigger trigger;

    /**
     * Create a work entry.
     */
    public WorkEntry()
    {
        this( SIMPLE, null, null );
    }

    /**
     * Create a work entry.
     */
    public WorkEntry( int mode )
    {
        this( mode, null, null );
    }

    /**
     * Create a work entry.
     */
    public WorkEntry( int mode, String key )
    {
        this( mode, key, null );
    }

    /**
     * Create a work entry.
     */
    public WorkEntry( int mode, String key, String name )
    {
        setMode( mode );
        setKey( key );
        setName( name );
        this.properties = new Properties();
    }

    /**
     * Return the user id.
     */
    public String getUserName()
    {
        return this.properties.getProperty( PROP_USERNAME );
    }

    /**
     * Set the user id.
     */
    public void setUserName( String userName )
    {
        this.properties.setProperty( PROP_USERNAME, userName );
    }

    /**
     * Return the key.
     */
    public String getKey()
    {
        return this.key;
    }

    /**
     * Set the key.
     */
    public void setKey( String key )
    {
        if ( key == null )
        {
            this.key = UUID.generateValue();
        }
        else
        {
            this.key = key;
        }
    }

    /**
     * Return the name.
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Set the name.
     */
    public void setName( String name )
    {
        this.name = name;
    }

    /**
     * Return the job class.
     */
    public String getWorkClass()
    {
        return this.workClass;
    }

    /**
     * Set the job class.
     */
    public void setWorkClass( String workClass )
    {
        this.workClass = workClass;
    }

    /**
     * Return the mode.
     */
    public int getMode()
    {
        if ( this.trigger instanceof CronTrigger )
        {
            return CRON;
        }
        else
        {
            return SIMPLE;
        }
    }

    /**
     * Set the mode.
     */
    public void setMode( int mode )
    {
        if ( mode == CRON )
        {
            this.trigger = new CronTrigger();
        }
        else
        {
            this.trigger = new SimpleTrigger();
        }

        this.trigger.setStartTime( new Date() );
    }

    /**
     * Return the start time.
     */
    public Date getStartTime()
    {
        return this.trigger.getStartTime();
    }

    /**
     * Set the start time.
     */
    public void setStartTime( Date time )
        throws IllegalArgumentException
    {
        this.trigger.setStartTime( time );
    }

    /**
     * Return the end time.
     */
    public Date getEndTime()
    {
        return this.trigger.getEndTime();
    }

    /**
     * Set the end time.
     */
    public void setEndTime( Date time )
        throws IllegalArgumentException
    {
        this.trigger.setEndTime( time );
    }

    /**
     * Return the next fire time.
     */
    public Date getNextFireTime()
    {
        return this.trigger.getNextFireTime();
    }

    /**
     * Return the previous fire time.
     */
    public Date getPreviousFireTime()
    {
        return this.trigger.getPreviousFireTime();
    }

    /**
     * Return the final fire time.
     */
    public Date getFinalFireTime()
    {
        return this.trigger.getFinalFireTime();
    }

    /**
     * Return the simple trigger.
     */
    private SimpleTrigger getSimpleTrigger()
        throws IllegalStateException
    {
        if ( this.trigger instanceof SimpleTrigger )
        {
            return (SimpleTrigger) this.trigger;
        }
        else
        {
            throw new IllegalStateException( "Not a simple trigger" );
        }
    }

    /**
     * Return the cron trigger.
     */
    private CronTrigger getCronTrigger()
        throws IllegalStateException
    {
        if ( this.trigger instanceof CronTrigger )
        {
            return (CronTrigger) this.trigger;
        }
        else
        {
            throw new IllegalStateException( "Not a cron trigger" );
        }
    }

    /**
     * Return the repeat count.
     */
    public int getRepeatCount()
        throws IllegalStateException
    {
        return getSimpleTrigger().getRepeatCount();
    }

    /**
     * Set the repeat count.
     */
    public void setRepeatCount( int count )
        throws IllegalStateException
    {
        getSimpleTrigger().setRepeatCount( count );
    }

    /**
     * Return the execution count (repeat + 1).
     */
    public int getExecutionCount()
        throws IllegalStateException
    {
        return getRepeatCount() + 1;
    }

    /**
     * Set the execution count (repeat + 1).
     */
    public void setExecutionCount( int count )
        throws IllegalStateException
    {
        setRepeatCount( count - 1 );
    }

    /**
     * Return the repeat interval (in sec).
     */
    public int getRepeatInterval()
        throws IllegalStateException
    {
        return (int) ( getSimpleTrigger().getRepeatInterval() / MS_IN_SEC );
    }

    /**
     * Set the repeat interval (in sec).
     */
    public void setRepeatInterval( int interval )
        throws IllegalStateException
    {
        long ms = interval * MS_IN_SEC;
        getSimpleTrigger().setRepeatInterval( ms );
    }

    /**
     * Return the cron expression.
     */
    public String getCronExpression()
        throws IllegalStateException
    {
        return getCronTrigger().getCronExpression();
    }

    /**
     * Set the cron expression.
     */
    public void setCronExpression( String expression )
        throws IllegalStateException, IllegalArgumentException
    {
        try
        {
            getCronTrigger().setCronExpression( expression );
        }
        catch ( IllegalStateException ise )
        {
            throw ise;
        }
        catch ( Exception e )
        {
            throw new IllegalArgumentException( e.getMessage() );
        }
    }

    /**
     * Return property names.
     */
    public String[] getPropertyNames()
    {
        return this.properties.keySet().toArray( new String[this.properties.size()] );
    }

    /**
     * Return the property.
     */
    public String getProperty( String name )
    {
        return this.properties.getProperty( name );
    }

    /**
     * Set the property.
     */
    public void setProperty( String name, String value )
    {
        this.properties.setProperty( name, value );
    }

    /**
     * Return the properties.
     */
    public Properties getProperties()
    {
        return this.properties;
    }

    /**
     * Return the trigger peer.
     */
    public Object getTriggerPeer()
    {
        return this.trigger;
    }

    /**
     * Set the trigger peer.
     */
    public void setTriggerPeer( Object peer )
    {
        if ( peer instanceof SimpleTrigger )
        {
            this.trigger = (SimpleTrigger) peer;
        }
        else if ( peer instanceof CronTrigger )
        {
            this.trigger = (CronTrigger) peer;
        }
    }
}
