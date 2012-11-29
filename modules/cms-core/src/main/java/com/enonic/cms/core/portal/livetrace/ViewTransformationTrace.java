package com.enonic.cms.core.portal.livetrace;

public class ViewTransformationTrace
    extends BaseTrace
    implements Trace
{
    private String view;

    private Traces<ViewFunctionTrace> viewFunctionTraces;

    @SuppressWarnings("UnusedDeclaration")
    public String getView()
    {
        return view;
    }

    void setView( String view )
    {
        this.view = view;
    }

    void addViewFunctionTrace( ViewFunctionTrace trace )
    {
        if ( viewFunctionTraces == null )
        {
            viewFunctionTraces = Traces.create();
        }
        viewFunctionTraces.add( trace );
    }

    @SuppressWarnings("UnusedDeclaration")
    public Traces<ViewFunctionTrace> getViewFunctionTraces()
    {
        return viewFunctionTraces;
    }
}
