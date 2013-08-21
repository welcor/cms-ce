/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.xslt.functions.portal;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceType;

import com.enonic.esl.util.Base64Util;

import com.enonic.cms.core.content.image.ImageUtil;
import com.enonic.cms.core.xslt.functions.AbstractXsltFunctionCall;

final class CreateImagePlaceholderFunction
    extends AbstractPortalFunction
{
    private final static String IMAGE_FORMAT = "gif";

    private final class Call
        extends AbstractXsltFunctionCall
    {
        @Override
        protected Item call( final XPathContext context, final SequenceIterator[] args )
            throws XPathException
        {
            final int width = toSingleInteger( args[0] ).intValue();
            final int height = toSingleInteger( args[1] ).intValue();

            try
            {
                final String value = createImageSrc( width, height, IMAGE_FORMAT );
                return createValue( value );
            }
            catch ( final Exception e )
            {
                throw new XPathException( e );
            }
        }

        private String createImageSrc( final int width, final int height, final String format )
            throws Exception
        {
            final BufferedImage image = createImage( width, height );
            final byte[] bytes = writeToBytes( image, format );

            final StringBuilder str = new StringBuilder();
            str.append( "data:image/" + format + ";base64," );
            str.append( Base64Util.encode( bytes ) );
            return str.toString();
        }

        private BufferedImage createImage( final int width, final int height )
        {
            return new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
        }

        private byte[] writeToBytes( final BufferedImage image, final String format )
            throws Exception
        {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageUtil.writeImage( image, format, out );
            out.close();
            return out.toByteArray();
        }
    }

    public CreateImagePlaceholderFunction()
    {
        super( "createImagePlaceholder" );
        setMinimumNumberOfArguments( 2 );
        setMaximumNumberOfArguments( 2 );
        setResultType( SequenceType.SINGLE_STRING );
    }

    @Override
    protected AbstractXsltFunctionCall createCall()
    {
        return new Call();
    }
}
