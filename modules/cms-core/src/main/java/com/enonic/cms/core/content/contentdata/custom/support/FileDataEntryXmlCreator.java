/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom.support;

import org.jdom.Element;

import com.enonic.cms.core.content.contentdata.ContentDataXPathCreator;
import com.enonic.cms.core.content.contentdata.custom.RelationDataEntry;
import com.enonic.cms.core.content.contentdata.custom.contentkeybased.FileDataEntry;
import com.enonic.cms.core.content.contentdata.custom.relationdataentrylistbased.FilesDataEntry;

public class FileDataEntryXmlCreator
{
    public void createAndAddElement( Element parentEl, FilesDataEntry filesDataEntry, boolean inBlockGroup )
    {
        final Element entryEl = ContentDataXPathCreator.ensurePath( parentEl, stripContentdataWhenNotBlockGroup(
            filesDataEntry.getConfig().getRelativeXPath(), inBlockGroup ) );

        for ( final RelationDataEntry fileDataEntry : filesDataEntry.getEntries() )
        {
            addFileElement( entryEl, fileDataEntry );
        }
    }

    public void createAndAddElement( Element parentEl, FileDataEntry fileDataEntry, boolean inBlockGroup )
    {
        final Element entryEl = ContentDataXPathCreator.ensurePath( parentEl, stripContentdataWhenNotBlockGroup(
            fileDataEntry.getConfig().getRelativeXPath(), inBlockGroup ) );

        addFileElement( entryEl, fileDataEntry );
    }

    private void addFileElement( Element entryEl, RelationDataEntry entry )
    {
        final Element fileEl = new Element( "file" );

        fileEl.setAttribute( "key", entry.getContentKey().toString() );

        if ( entry.isMarkedAsDeleted() )
        {
            fileEl.setAttribute( "deleted", "" + entry.isMarkedAsDeleted() );
        }

        entryEl.addContent( fileEl );
    }

    private String stripContentdataWhenNotBlockGroup( String xpath, boolean inBlockGroup )
    {
        if ( !inBlockGroup && xpath.startsWith( "contentdata/" ) )
        {
            return xpath.substring( "contentdata/".length() );
        }
        else if ( !inBlockGroup && xpath.startsWith( "/contentdata/" ) )
        {
            return xpath.substring( "/contentdata/".length() );
        }
        return xpath;
    }
}
