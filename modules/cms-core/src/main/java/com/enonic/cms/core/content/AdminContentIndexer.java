package com.enonic.cms.core.content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.search.index.ContentIndexService;
import com.enonic.cms.core.search.ContentIndexDataBuilderSpecification;
import com.enonic.cms.core.search.builder.ContentIndexDataBuilder;
import com.enonic.cms.core.search.index.ContentIndexData;
import com.enonic.cms.store.dao.ContentDao;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/28/11
 * Time: 4:26 PM
 */
public class AdminContentIndexer
{
    @Autowired
    private ContentIndexService contentIndexService;

    @Autowired
    private ContentIndexDataBuilder contentIndexDataBuilder;

    @Autowired
    private ContentDao contentDao;


    public void regenerateIndex( List<ContentKey> contentKeys, ContentIndexDataBuilderSpecification spec )
        throws Exception
    {

        List<ContentIndexData> contentIndexDataList = new ArrayList<ContentIndexData>();

        for ( ContentKey contentKey : contentKeys )
        {
            contentIndexDataList.add( contentIndexDataBuilder.build( contentDao.findByKey( contentKey ), spec ) );
        }

        contentIndexService.index( contentIndexDataList );
    }


}
