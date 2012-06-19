package com.enonic.cms.core.content.category;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.MemberOfResolver;
import com.enonic.cms.core.time.TimeService;

@Component
public class CreateCategoryCommandProcessorFactory extends AbstractCategoryCommandProcessorFactory
{
    @Autowired
    private TimeService timeService;

    @Autowired
    private MemberOfResolver memberOfResolver;

    @Autowired
    private UnitFactory unitFactory;

    CreateCategoryCommandProcessor create( StoreNewCategoryCommand command )
    {
        Preconditions.checkNotNull( command.getCreator(), "Creator in command must be specified" );

        final UserEntity creator = resolveUser( command.getCreator(), "creator" );
        final CategoryEntity parentCategory = resolveCategory( command.getParentCategory() );
        final ContentTypeEntity contentType = resolveContentType( command.getContentType() );
        final CategoryAccessStorer categoryAccessStorer = new CategoryAccessStorer( groupDao );
        final CategoryAccessResolver categoryAccessResolver = new CategoryAccessResolver( groupDao );
        final CreateCategoryAccessChecker createCategoryAccessChecker =
            new CreateCategoryAccessChecker( memberOfResolver, categoryAccessResolver ).creator( creator );

        final CreateCategoryCommandProcessor createCategoryCommandProcessor =
            new CreateCategoryCommandProcessor( timeService, categoryDao, unitFactory, categoryAccessStorer, createCategoryAccessChecker );

        createCategoryCommandProcessor.setCreator( creator );
        createCategoryCommandProcessor.setParentCategory( parentCategory );
        createCategoryCommandProcessor.setContentType( contentType );

        return createCategoryCommandProcessor;
    }
}
