/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.imports;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.cms.framework.util.GenericConcurrencyLock;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.CtyImportConfig;
import com.enonic.cms.core.content.contenttype.CtyImportPurgeConfig;
import com.enonic.cms.core.search.query.ContentIndexService;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.store.dao.ContentDao;


public class ImportJobImpl
    implements ImportJob
{
    private static final Logger LOG = LoggerFactory.getLogger( ImportJobImpl.class );

    private ImportService importService;

    private ContentIndexService contentIndexService;

    private ContentDao contentDao;

    private ImportResult importResult;

    private UserEntity importer;

    private CategoryEntity categoryToImportTo;

    private CtyImportConfig importConfig;

    private ImportDataReader importDataReader;

    private DateTime defaultPublishFrom;

    private DateTime defaultPublishTo;

    private List<ContentKey> contentNotAffectedByImport;

    private Map<String, ContentKey> existingContentKeysBySyncValue;

    private static boolean executeInOneTransaction = false;

    private UserEntity assignee;

    private Date assignmentDueDate;

    private String assignmentDescription;

    private static GenericConcurrencyLock<CategoryKey> concurrencyLock = GenericConcurrencyLock.create();

    public ImportResult start()
    {
        final Lock locker = concurrencyLock.getLock( categoryToImportTo.getKey() );

        try
        {
            locker.lock();

            LOG.info( "Starting content import job #" + this.getImportJobNumber() );
            LOG.info(
                "Import job #" + this.getImportJobNumber() + ": importing to category: key = " + categoryToImportTo.getKey() + ", path = " +
                    categoryToImportTo.getPathAsString() );

            importConfig.validateContentTypeImportConfig( categoryToImportTo.getContentType().getContentTypeConfig() );

            if ( importConfig.isSyncEnabled() )
            {
                initSyncMode();
            }

            importResult = new ImportResult();
            importResult.startTimer();

            int count = 0;

            while ( importDataReader.hasMoreEntries() )
            {
                count++;

                final ImportDataEntry nextEntry = importDataReader.getNextEntry();

                long lastEntryImportTime = System.currentTimeMillis();

                if ( executeInOneTransaction )
                {
                    importService.importData_withoutRequiresNewPropagation_for_test_only( nextEntry, this );
                }
                else
                {
                    importService.importData( nextEntry, this );
                }

                LOG.info( "Import job #" + this.getImportJobNumber() + "entry #" + count + " finished in " +
                              ( System.currentTimeMillis() - lastEntryImportTime ) + " milliseconds." );
            }

            if ( importConfig.isSyncEnabled() )
            {
                // Import content is done... now what to do with the unaffected content in the category we imported to?
                handleUnaffectedContentInCategory();
            }

            importResult.stopTimer();

            LOG.info( "Finished content import job #" + this.getImportJobNumber() );

            return importResult;
        }
        finally
        {
            locker.unlock();
        }
    }

    private int getImportJobNumber()
    {
        return this.hashCode();
    }

    private void initSyncMode()
    {
        contentNotAffectedByImport = contentDao.findContentKeysByCategory( categoryToImportTo.getKey() );

        LOG.info( "Import job #" + this.getImportJobNumber() + ": found " + contentNotAffectedByImport.size() +
                      " existing content in category: " + categoryToImportTo.getPathAsString() );

        existingContentKeysBySyncValue =
            new ExistingContentBySyncValueResolver( contentIndexService ).resolve( categoryToImportTo, importConfig );

        LOG.info( "Import job #" + this.getImportJobNumber() + ": found " + existingContentKeysBySyncValue.size() +
                      " matching content keys (by sync value) in category: " + categoryToImportTo.getPathAsString() );
    }

    private void handleUnaffectedContentInCategory()
    {
        if ( contentNotAffectedByImport.isEmpty() )
        {
            LOG.info( "Import job #" + this.getImportJobNumber() +
                          ": No remaining content to purge. All content in the category was affected by the import the job." );
            return;
        }

        for ( ContentKey contentKey : contentNotAffectedByImport )
        {
            handleUnaffectedContent( contentKey );
        }
    }

    private void handleUnaffectedContent( ContentKey contentKey )
    {
        if ( CtyImportPurgeConfig.ARCHIVE == importConfig.getPurge() )
        {
            if ( executeInOneTransaction )
            {
                importService.archiveContent_withoutRequiresNewPropagation_for_test_only( importer, contentKey, importResult );
            }
            else
            {
                importService.archiveContent( importer, contentKey, importResult );
            }

        }
        else if ( CtyImportPurgeConfig.DELETE == importConfig.getPurge() )
        {
            if ( executeInOneTransaction )
            {
                importService.deleteContent_withoutRequiresNewPropagation_for_test_only( importer, contentKey, importResult );
            }
            else
            {
                importService.deleteContent( importer, contentKey, importResult );
            }
        }
        else if ( CtyImportPurgeConfig.NONE == importConfig.getPurge() )
        {

            final ContentEntity content = contentDao.findByKey( contentKey );
            importResult.addRemaining( content );
        }
    }

    public ContentKey resolveExistingContentBySyncValue( final ImportDataEntry importDataEntry )
    {
        //String value = StringUtil.replaceECC( importDataEntry.getSyncValue() );
        String value = importDataEntry.getSyncValue();
        return existingContentKeysBySyncValue.get( value.toLowerCase() );
    }

    public void registerImportedContent( ContentKey contentKey )
    {
        if ( importConfig.isSyncEnabled() )
        {
            contentNotAffectedByImport.remove( contentKey );
        }
    }

    public CategoryEntity getCategoryToImportTo()
    {
        return categoryToImportTo;
    }

    public DateTime getDefaultPublishFrom()
    {
        return defaultPublishFrom;
    }

    public DateTime getDefaultPublishTo()
    {
        return defaultPublishTo;
    }

    public UserEntity getImporter()
    {
        return importer;
    }

    public CtyImportConfig getImportConfig()
    {
        return importConfig;
    }

    public ImportResult getImportResult()
    {
        return importResult;
    }

    public void setImportService( ImportService value )
    {
        this.importService = value;
    }

    public void setContentIndexService( ContentIndexService value )
    {
        this.contentIndexService = value;
    }

    public void setContentDao( ContentDao value )
    {
        this.contentDao = value;
    }

    public void setImporter( UserEntity value )
    {
        this.importer = value;
    }

    public void setCategoryToImportTo( CategoryEntity value )
    {
        this.categoryToImportTo = value;
    }

    public void setImportConfig( CtyImportConfig value )
    {
        this.importConfig = value;
    }

    public void setImportDataReader( ImportDataReader value )
    {
        this.importDataReader = value;
    }

    public void setDefaultPublishFrom( DateTime value )
    {
        this.defaultPublishFrom = value;
    }

    public void setDefaultPublishTo( DateTime value )
    {
        this.defaultPublishTo = value;
    }

    public void setExecuteInOneTransaction( boolean value )
    {
        executeInOneTransaction = value;
    }

    public UserEntity getAssignee()
    {
        return assignee;
    }

    public void setAssignee( UserEntity assignee )
    {
        this.assignee = assignee;
    }

    public Date getAssignmentDueDate()
    {
        return assignmentDueDate;
    }

    public String getAssignmentDescription()
    {
        return assignmentDescription;
    }

    public void setAssignmentDueDate( Date assignmentDueDate )
    {
        this.assignmentDueDate = assignmentDueDate;
    }

    public void setAssignmentDescription( String assignmentDescription )
    {
        this.assignmentDescription = assignmentDescription;
    }
}

