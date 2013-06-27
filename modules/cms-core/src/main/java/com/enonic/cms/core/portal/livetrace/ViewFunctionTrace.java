/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.livetrace;


import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

public class ViewFunctionTrace
    extends BaseTrace
    implements Trace
{
    private String name;

    private List<ViewFunctionArgument> arguments = new ArrayList<ViewFunctionArgument>();

    private Traces<Trace> traces;

    void setName( String name )
    {
        this.name = name;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getName()
    {
        return name;
    }

    void addArgument( ViewFunctionArgument argument )
    {
        arguments.add( argument );
    }

    @SuppressWarnings("UnusedDeclaration")
    public List<ViewFunctionArgument> getArguments()
    {
        return arguments;
    }

    void addTrace( Trace trace )
    {
        Preconditions.checkArgument( !( trace instanceof ViewFunctionTrace ),
                                     "Preventing infinite recursion: trying to add trace of same type" );

        if ( traces == null )
        {
            traces = Traces.create();
        }
        traces.add( trace );
    }

    public Traces<? extends Trace> getTraces()
    {
        return traces;
    }
}
