/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.imports;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.security.user.UserEntity;

public interface ImportService
{
    boolean importData( ImportDataEntry importDataEntry, ImportJob importJob );

    boolean importData_withoutRequiresNewPropagation_for_test_only( ImportDataEntry importDataEntry, ImportJob importJob );

    void archiveContent( UserEntity importer, ContentKey contentKey, ImportResult importResult );

    void archiveContent_withoutRequiresNewPropagation_for_test_only( UserEntity importer, ContentKey contentKey,
                                                                     ImportResult importResult );

    void deleteContent( UserEntity importer, ContentKey contentKey, ImportResult importResult );

    void deleteContent_withoutRequiresNewPropagation_for_test_only( UserEntity importer, ContentKey contentKey,
                                                                    ImportResult importResult );
}
