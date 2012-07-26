package com.enonic.cms.itest.search;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

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
public class ContentIndexServiceTestDBBase
    extends ContentIndexServiceTestBase
{

    protected DomainFactory factory;

    @Autowired
    protected DomainFixture fixture;

    @Test
    public void dummy()
    {

    }

}
