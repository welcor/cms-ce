package com.enonic.cms.core.search.index;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.search.batch.BatchLoader;
import com.enonic.cms.store.dao.ContentDao;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/22/11
 * Time: 2:00 PM
 */
final class ContentLoader
    implements BatchLoader<ContentEntity>
{

    private ContentDao contentDao;

    public ContentLoader()
    {

    }

    public int getTotal()
    {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setBatchSize( int size )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean hasNext()
    {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<ContentEntity> next()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void reset()
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Autowired
    public void setContentDao( ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }
}
