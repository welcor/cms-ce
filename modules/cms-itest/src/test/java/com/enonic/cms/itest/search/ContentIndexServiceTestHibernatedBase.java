package com.enonic.cms.itest.search;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentStatus;
import com.enonic.cms.core.content.access.ContentAccessEntity;
import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.command.CreateContentCommand;
import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contenttype.ContentTypeConfig;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 1/2/12
 * Time: 3:09 PM
 */
@TransactionConfiguration(defaultRollback = true)
@DirtiesContext
@Transactional
public class ContentIndexServiceTestHibernatedBase
    extends ContentIndexServiceTestBase
{

    protected DomainFactory factory;

    @Autowired
    protected DomainFixture fixture;

    protected ContentAccessEntity createContentAccess( final String userName, boolean read, boolean update )
    {
        return createContentAccess( fixture.findUserByName( userName ).getUserGroup(), read, update );
    }

    protected ContentAccessEntity createContentAccess( GroupEntity group, boolean read, boolean update )
    {
        ContentAccessEntity contentAccess = new ContentAccessEntity();
        contentAccess.setGroup( group );
        contentAccess.setReadAccess( read );
        contentAccess.setUpdateAccess( update );
        return contentAccess;
    }






}
