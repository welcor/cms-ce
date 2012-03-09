package com.enonic.vertical.work.quartz;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * This class defines a simple invoker job.
 */
public final class WorkJobAdapter
    implements Job
{

    private final static Logger LOG = Logger.getLogger( WorkJobAdapter.class.getName() );

    public final static String CLASS_KEY = "class";

    public void execute( JobExecutionContext context )
        throws JobExecutionException
    {

        // Find the data
        JobDetail detail = context.getJobDetail();
        JobDataMap dataMap = detail.getJobDataMap();

        Properties props = new Properties();
        props.putAll( dataMap );

        execute( props );
    }

    private void execute( Properties props )
        throws JobExecutionException
    {
        String className = props.getProperty( CLASS_KEY );
        if ( className == null )
        {
            throw new JobExecutionException( "'class' not specified" );
        }

        try
        {
            QuartzHelper.getWorkRunner().executeWork( className, props );
        }
        catch ( Exception e )
        {
            LOG.log( Level.SEVERE, "Failed to execute job [" + className + "]: " + e.getMessage(), e );
            throw new JobExecutionException( e );
        }
    }
}
