package com.enonic.cms.core.content.category;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.language.LanguageEntity;
import com.enonic.cms.core.time.TimeService;
import com.enonic.cms.store.dao.ContentTypeDao;
import com.enonic.cms.store.dao.LanguageDao;

@Component
public class UnitFactory
{
    @Autowired
    private TimeService timeService;

    @Autowired
    private LanguageDao languageDao;

    @Autowired
    private ContentTypeDao contentTypeDao;

    UnitEntity createNewUnit( final StoreNewCategoryCommand command )
    {
        Preconditions.checkNotNull( command.getLanguage(), "Expected language to be specified when creating a content archive" );
        final LanguageEntity language = languageDao.findByKey( command.getLanguage() );
        Preconditions.checkNotNull( language, "Specified language does not exist: " + command.getLanguage() );

        final UnitEntity newUnit = new UnitEntity();
        newUnit.setDeleted( false );
        newUnit.setName( command.getName() );
        newUnit.setLanguage( language );
        newUnit.setTimestamp( timeService.getNowAsDateTime().toDate() );
        if ( command.getAllowedContentTypes() != null )
        {
            for ( ContentTypeKey allowedContentTypeKey : command.getAllowedContentTypes() )
            {
                final ContentTypeEntity allowedContentType = contentTypeDao.findByKey( allowedContentTypeKey );
                Preconditions.checkNotNull( allowedContentType, "Specified content type to allow for content archive does not exist: " +
                    allowedContentTypeKey );
                newUnit.addContentType( allowedContentType );
            }
        }

        return newUnit;
    }
}
