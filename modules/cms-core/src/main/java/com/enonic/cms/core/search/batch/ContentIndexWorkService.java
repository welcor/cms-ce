package com.enonic.cms.core.search.batch;

import org.springframework.beans.factory.InitializingBean;

import com.enonic.cms.core.search.builder.ContentIndexDataBuilder;
import com.enonic.cms.core.search.index.ContentIndexData;
import com.enonic.cms.core.search.index.ContentIndexService;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/22/11
 * Time: 2:25 PM
 */
public class ContentIndexWorkService
    implements Runnable, InitializingBean
{

    ContentIndexService contentIndexService;

    ContentIndexDataBuilder contentIndexDataBuilder;

    public void afterPropertiesSet()
        throws Exception
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void run()
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    public void indexAllContent()
        throws Exception
    {
        ContentIndexJobSpecification allContentSpecification = ContentIndexJobSpecification.createAllContentSpecification();
        doIndexContent( allContentSpecification );
    }

    public void indexContent( ContentIndexJobSpecification specification )
        throws Exception
    {
        doIndexContent( specification );
    }

    private void doIndexContent( ContentIndexJobSpecification specification )
        throws Exception
    {

        // Loader.fetch

        ContentIndexData contentIndexData = contentIndexDataBuilder.build( null, specification.getContentIndexDataBuilderSpecification() );

        contentIndexService.index( contentIndexData );
    }

}

