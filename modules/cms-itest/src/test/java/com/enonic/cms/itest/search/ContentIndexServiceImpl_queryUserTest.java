package com.enonic.cms.itest.search;

import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.search.query.ContentDocument;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 1/13/12
 * Time: 2:18 PM
 */
public class ContentIndexServiceImpl_queryUserTest
    extends ContentIndexServiceTestBase
{

    @Test
    public void testIndexingAndSearchOnOwnerQualifiedName()
    {
        ContentDocument doc = createContentDocument( 101, "ost", null );
        doc.setOwnerQualifiedName( "incamono\\jvs" );

        contentIndexService.index( doc);

        flushIndex();

        assertContentResultSetEquals( new int[]{101},
                                      contentIndexService.query( new ContentIndexQuery( "owner/qualifiedName = 'incamono\\jvs'" ) ) );
    }


}
