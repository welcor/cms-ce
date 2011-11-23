package com.enonic.cms.core.search.batch;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/22/11
 * Time: 1:59 PM
 */
public interface BatchLoader<T>
{
    public int getTotal();

    public void setBatchSize( int size );

    public boolean hasNext();

    public List<T> next();

    public void reset();
}
