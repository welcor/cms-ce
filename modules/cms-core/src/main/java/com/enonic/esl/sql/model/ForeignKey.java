/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.sql.model;

import java.util.ArrayList;
import java.util.List;

public class ForeignKey
{
    private final String name;

    private final Table remoteTable;

    private final List<Reference> references = new ArrayList<Reference>();

    public class Reference
    {
        public final Column localColumn;

        public final Column remoteColumn;

        public Reference( Column localColumn, Column remoteColumn )
        {
            this.localColumn = localColumn;
            this.remoteColumn = remoteColumn;
        }

    }

    public ForeignKey( String name, Table remoteTable )
    {
        this.name = name;
        this.remoteTable = remoteTable;
    }

    public void addReference( Column localColumn, Column remoteColumn )
    {
        references.add( new Reference( localColumn, remoteColumn ) );
    }

    public String getName()
    {
        return name;
    }

    public Table getRemoteTable()
    {
        return remoteTable;
    }

    public ForeignKey.Reference[] getReferences()
    {
        return this.references.toArray( new ForeignKey.Reference[this.references.size()] );
    }
}
