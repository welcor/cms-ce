/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.Collection;
import java.util.List;
import java.util.SortedMap;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentSpecification;
import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.content.resultset.RelatedChildContent;
import com.enonic.cms.core.content.resultset.RelatedParentContent;
import com.enonic.cms.store.support.EntityPageList;

public interface ContentDao
    extends EntityDao<ContentEntity>
{
    ContentEntity findByKey( ContentKey contentKey );

    SortedMap<ContentKey, ContentEntity> findByKeys( List<ContentKey> contentKeys );

    List<ContentKey> findBySpecification( ContentSpecification specification, String orderBy, int count );

    Collection<RelatedChildContent> findRelatedChildrenByKeys( RelatedChildContentQuery relatedChildContentQuery );

    Collection<RelatedParentContent> findRelatedParentByKeys( RelatedParentContentQuery relatedParentContentQuery );

    List<ContentKey> findContentKeysByContentType( ContentTypeEntity contentType );

    List<ContentKey> findContentKeysByCategory( CategoryEntity category );

    int getNumberOfRelatedParentsByKey( List<ContentKey> contentKeys );

    List<ContentKey> findAll();

    EntityPageList<ContentEntity> findAll( int index, int count );

    int findCountBySpecification( ContentSpecification specification );

    boolean checkNameExists( CategoryEntity category, String name );

    long countContentByCategory( CategoryEntity category );
}
