package com.enonic.cms.core.portal.rendering.tracing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

import static org.junit.Assert.*;

public class RenderTraceHistoryTest
{
    @Test
    public void testSerialize()
        throws Exception
    {
        final RenderTraceHistory history = new RenderTraceHistory();
        assertNotNull( history.getHistory() );
        assertEquals( 0, history.getHistory().size() );

        history.getHistory().add( new RenderTraceInfo() );
        assertEquals( 1, history.getHistory().size() );

        final byte[] data = serialize( history );
        final Object newObject = deSerialize( data );

        assertNotNull( newObject );
        assertTrue( newObject instanceof RenderTraceHistory );

        final RenderTraceHistory newHistory = (RenderTraceHistory) newObject;
        assertNotNull( newHistory.getHistory() );
        assertEquals( 0, newHistory.getHistory().size() );
    }

    private byte[] serialize( final Object obj )
        throws Exception
    {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ObjectOutputStream objectOut = new ObjectOutputStream( out );

        objectOut.writeObject( obj );

        objectOut.close();
        return out.toByteArray();
    }

    private Object deSerialize( final byte[] data )
        throws Exception
    {
        final ByteArrayInputStream in = new ByteArrayInputStream( data );
        final ObjectInputStream objectIn = new ObjectInputStream( in );

        final Object obj = objectIn.readObject();

        objectIn.close();
        return obj;
    }
}
