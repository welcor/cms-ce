package com.enonic.vertical.work;

import java.util.Comparator;


/**
 * WorkEntryComparator.
 *
 * @author Georg Lundesgaard (glu@enonic.com)
 */
public final class WorkEntryComparator
    implements Comparator
{
    /**
     * Instance of comparator.
     */
    public final static WorkEntryComparator INSTANCE = new WorkEntryComparator();

    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare( Object o1, Object o2 )
    {
        WorkEntry entry1 = (WorkEntry) o1;
        WorkEntry entry2 = (WorkEntry) o2;

        if ( entry1.getNextFireTime() == null && entry2.getNextFireTime() == null )
        {
            return 0;
        }
        else if ( entry1.getNextFireTime() == null )
        {
            return -1;
        }
        else if ( entry2.getNextFireTime() == null )
        {
            return 1;
        }

        long diff = ( entry1.getNextFireTime().getTime() - entry2.getNextFireTime().getTime() );
        if ( diff < 0 )
        {
            return -1;
        }
        else if ( diff > 0 )
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }

    /**
     * @see java.util.Comparator#equals(java.lang.Object)
     */
    public boolean equals( Object obj )
    {
        return obj instanceof WorkEntryComparator;
    }
}
