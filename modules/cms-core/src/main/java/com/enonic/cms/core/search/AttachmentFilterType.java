package com.enonic.cms.core.search;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/8/11
 * Time: 4:23 PM
 */
public enum AttachmentFilterType
{
    FILENAME( "attachments.filename" ),
    TEXT( "attachments.text" );


    private String fieldRepresentation;

    AttachmentFilterType( String fieldRepresentation )
    {
        this.fieldRepresentation = fieldRepresentation;
    }

    public String getFieldRepresentation()
    {
        return fieldRepresentation;
    }
}
