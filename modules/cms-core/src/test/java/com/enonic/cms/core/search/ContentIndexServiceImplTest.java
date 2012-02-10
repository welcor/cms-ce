package com.enonic.cms.core.search;

import org.elasticsearch.client.Client;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.core.search.builder.ContentIndexDataBuilder;
import com.enonic.cms.core.search.query.QueryTranslator;
import com.enonic.cms.store.dao.ContentDao;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/10/12
 * Time: 3:41 PM
 */
public class ContentIndexServiceImplTest
{

    private ContentIndexServiceImpl contentIndexService = new ContentIndexServiceImpl();

    private Client client;

    private ContentDao contentDao;

    private ContentIndexDataBuilder contentIndexDataBuilder;

    private IndexMappingProvider indexMappingProvider;

    private QueryTranslator queryTranslator;

    @Before
    public void setUp()
    {

        client = Mockito.mock( Client.class );
        contentDao = Mockito.mock( ContentDao.class );
        contentIndexDataBuilder = Mockito.mock( ContentIndexDataBuilder.class );
        indexMappingProvider = Mockito.mock( IndexMappingProvider.class );
        queryTranslator = Mockito.mock( QueryTranslator.class );

        contentIndexService.setClient( client );
        contentIndexService.setContentDao( contentDao );
        contentIndexService.setContentIndexDataBuilder( contentIndexDataBuilder );
        contentIndexService.setIndexMappingProvider( indexMappingProvider );
        contentIndexService.setQueryTranslator( queryTranslator );
    }

    @Test
    public void testStuff()
    {

    }
}
