package com.enonic.cms.core.search.index;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.search.IndexType;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/22/11
 * Time: 1:39 PM
 */
public interface ContentIndexService
{

    public void index( final ContentIndexData content );

    public void index( final ContentIndexData... content );


    public void delete( final ContentKey... contentKeys );

    public void delete( final CategoryKey... categoryKeys );

    public void delete( final ContentTypeKey... contentTypeKeys );


    public void update( final ContentKey... contentKeys );

    public void update( final CategoryKey... categoryKeys );

    public void update( final ContentTypeKey... contentTypeKeys );


}
