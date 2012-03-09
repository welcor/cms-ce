package com.enonic.vertical.work;

/**
 * This interface defines the work service.
 */
public interface WorkService
{
    /**
     * Return true if enabled.
     */
    public boolean isEnabled();

    /**
     * Add the job task.
     */
    public void addEntry( WorkEntry task )
        throws WorkException;

    /**
     * Add the job task.
     */
    public void addEntry( WorkEntry task, boolean replace )
        throws WorkException;

    /**
     * Delete task.
     */
    public void deleteEntry( WorkEntry entry )
        throws WorkException;

    /**
     * Delete task.
     */
    public void deleteEntry( String key )
        throws WorkException;

    /**
     * Return entry keys.
     */
    public String[] getEntryKeys()
        throws WorkException;

    /**
     * Return a single entry.
     */
    public WorkEntry getEntry( String key )
        throws WorkException;

    /**
     * Return the tasks.
     */
    public WorkEntry[] getEntries()
        throws WorkException;
}
