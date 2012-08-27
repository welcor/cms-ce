/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.imports;

import java.util.List;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.security.user.UserEntity;

public interface ImportService
{
    boolean importData( ImportDataReader importDataReader, ImportJob importJob );

    boolean importData_withoutRequiresNewPropagation_for_test_only( ImportDataReader importDataReader, ImportJob importJob );

    void archiveContent( UserEntity importer, List<ContentKey> contentKeys, ImportResult importResult );

    void archiveContent_withoutRequiresNewPropagation_for_test_only( UserEntity importer, List<ContentKey> contentKeys,
                                                                     ImportResult importResult );

    void deleteContent( UserEntity importer, List<ContentKey> contentKeys, ImportResult importResult );

    void deleteContent_withoutRequiresNewPropagation_for_test_only( UserEntity importer, List<ContentKey> contentKeys,
                                                                    ImportResult importResult );
}
