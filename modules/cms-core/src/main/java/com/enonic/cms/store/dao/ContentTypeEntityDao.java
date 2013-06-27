/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.store.support.EntityPageList;

@Repository("contentTypeDao")
public class ContentTypeEntityDao
    extends AbstractBaseEntityDao<ContentTypeEntity>
    implements ContentTypeDao
{

    public ContentTypeEntity findByKey( int key )
    {
        return get( ContentTypeEntity.class, key );
    }

    public ContentTypeEntity findByKey( ContentTypeKey key )
    {
        return get( ContentTypeEntity.class, key.toInt() );
    }

    public ContentTypeEntity findByName( String name )
    {
        return findSingleByNamedQuery( ContentTypeEntity.class, "ContentTypeEntity.getByName", "name", name );
    }

    public List<ContentTypeEntity> getAll()
    {
        return findByNamedQuery( ContentTypeEntity.class, "ContentTypeEntity.getAll" );
    }

    public List getResourceUsageCountCSS()
    {
        return getHibernateTemplate().findByNamedQuery( "ContentTypeEntity.getResourceUsageCountCSS" );
    }

    public List<ContentTypeEntity> findByCSS( ResourceKey resourceKey )
    {
        return findByNamedQuery( ContentTypeEntity.class, "ContentTypeEntity.findByCSS", "defaultCssKey", resourceKey );
    }

    private List<ContentTypeEntity> findByCSSPrefix( String prefix )
    {
        return findByNamedQuery( ContentTypeEntity.class, "ContentTypeEntity.findByCSSPrefix", "defaultCssKeyPrefix", prefix );
    }

    public void updateResourceCSSReference( ResourceKey oldResourceKey, ResourceKey newResourceKey )
    {
        List<ContentTypeEntity> entityList = findByCSS( oldResourceKey );

        for ( ContentTypeEntity entity : entityList )
        {
            entity.setDefaultCssKey( newResourceKey );
        }
    }

    public void updateResourceCSSReferencePrefix( String oldPrefix, String newPrefix )
    {
        List<ContentTypeEntity> entityList = findByCSSPrefix( oldPrefix + "%" );

        for ( ContentTypeEntity entity : entityList )
        {
            String key = entity.getDefaultCssKey().toString();
            key = key.replace( oldPrefix, newPrefix );
            entity.setDefaultCssKey( ResourceKey.from( key ) );
        }
    }

    public EntityPageList<ContentTypeEntity> findAll( int index, int count )
    {
        return findPageList( ContentTypeEntity.class, null, index, count );
    }
}
