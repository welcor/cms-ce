/*
    Internal:                                                       External:

    attachment://$contentKey                                        _attachment/$contentKey
    attachment://$contentKey/binary/$binaryKey                      _attachment/$contentKey/binary/$binaryKey
    image://$contentKey?filter=$filter&size=$size&format=$format    _image/$contentKey?filter=$filter&size=$size&format=$format

 */

(function()
{
    var imgSrcWithSizeParamEQCustomPattern = /^_image\/.+(_size=custom)/im;
    var filterParamPattern = /([\?&]_filter(?:=[^&]*)?)/im;

    var internalImagePattern = /(<img.+?src=")(image:\/\/)(.+?)(".+?\/>)/gim;
    var internalImageAttachmentPattern = /(<img.+?src=")(attachment:\/\/)(.+?)(".+?\/>)/gim;

    var externalImgSrcPattern = /^_image\/(.+)/im;
    var externalImgSrcReplacePattern = 'image://$1';

    var externalImgSrcAttachmentPattern = /^_attachment\/(.*?)/im;
    var externalImgSrcAttachmentReplacePattern = 'attachment://$1';

    tinymce.create('tinymce.plugins.InternalLinkPlugin', {

        init: function( ed, url )
        {
            var t = this;
            t.editor = ed;

            ed.onBeforeSetContent.add(function(ed, o)
            {
                t.transformImagesToExternalFormat(o);
            });

            ed.onPreProcess.add(function(ed, o)
            {
                if ( o.get )
                {
                    t.transformImageSrcToInternalFormat(o);
                }
            });
        },
        // ---------------------------------------------------------------------------------------------------------------------------------

        // Public

        transformImagesToExternalFormat: function ( o )
        {
            var t = this;

            function replacer( match, p1, p2, p3, p4 )
            {
                /*
                match   = <img alt="c" src="image://20844?_size=wide&amp;_format=jpg" title="c" />
                p1      = <img alt="c" src="
                p2      = image://
                p3      = 20844?_size=wide&amp;_format=jpg
                p4      = " title="c" />
                */

                var imageKeyAndUrlParams = p3;
                var filterParamValue = t.resolveFilterParam( 'image://' + imageKeyAndUrlParams );
                if ( filterParamValue !== '' )
                {
                    imageKeyAndUrlParams += '&_filter=' + filterParamValue;
                }
                return p1 + '_image/' + imageKeyAndUrlParams + p4;
            }

            // Replace all images with internal links
            o.content = o.content.replace( internalImagePattern, replacer );
            // Replace all images using image attachment
            o.content = o.content.replace( internalImageAttachmentPattern, replacer );
        },
        // ---------------------------------------------------------------------------------------------------------------------------------

        transformImageSrcToInternalFormat: function( o )
        {
            var t = this;
            var dom = t.editor.dom;
            var imageElements = dom.select('img', o.node);

            tinymce.each( imageElements, function( imageElement )
            {
                var imageSrc = dom.getAttrib(imageElement, 'src');

                if ( imageSrc.match(externalImgSrcPattern) )
                {
                    // Remove filter param for non custom sizes.
                    if ( !t._isCustomSize(imageSrc) )
                    {
                        imageSrc = imageSrc.replace(filterParamPattern, '');
                    }

                    imageSrc = imageSrc.replace(externalImgSrcPattern, externalImgSrcReplacePattern);
                }

                if ( imageSrc.match(externalImgSrcAttachmentPattern) )
                {
                    imageSrc = imageSrc.replace(externalImgSrcAttachmentPattern, externalImgSrcAttachmentReplacePattern);
                }

                dom.setAttrib(imageElement, 'src', imageSrc);
            });
        },
        // ---------------------------------------------------------------------------------------------------------------------------------

        resolveFilterParam: function( imageSrc, customWidth )
        {
            var t = this;

            var filter = '';

            var editorWidth = t._getWidthForEditorInstance();
            var fortyPercentOfEditorWidth = Math.round((editorWidth * 40 / 100));
            var twentyFivePercentOfEditorWidth = Math.round((editorWidth * 25 / 100));
            var fifteenPercentOfEditorWidth = Math.round((editorWidth * 15 / 100));
            var heightForScaleWideFormat = Math.round((editorWidth * 0.42));

            if ( imageSrc.match(/_size\=full/i ) )
            {
                filter = 'scalewidth(' + editorWidth + ')';
            }
            else if ( imageSrc.match(/_size\=wide/i ) )
            {
                filter = 'scalewide(' + editorWidth + ',' + heightForScaleWideFormat + ')';
            }
            else if ( imageSrc.match(/_size\=regular/i) )
            {
                filter = 'scalewidth(' + fortyPercentOfEditorWidth + ')';
            }
            else if ( imageSrc.match(/_size\=square/i) )
            {
                filter = 'scalesquare(' + fortyPercentOfEditorWidth + ')';
            }
            else if ( imageSrc.match(/_size\=list/i) )
            {
                filter = 'scalesquare(' + twentyFivePercentOfEditorWidth + ')';
            }
            else if ( imageSrc.match(/_size\=thumbnail/i) )
            {
                filter = 'scalesquare(' + fifteenPercentOfEditorWidth + ')';
            }                                                                                   // TODO: Use \d+
            else if ( imageSrc.match(/_size\=custom/i) && (customWidth && customWidth.match(/\d/)) )
            {
                filter = 'scalewidth(' + customWidth + ')';
            }
            else
            {
                filter = '';
            }

            return filter;
        },
        // ---------------------------------------------------------------------------------------------------------------------------------

        getInfo: function()
        {
            return {
                longname : 'Internal Link Plugin',
                author : 'tan@enonic.com',
                authorurl : 'http://www.enonic.com',
                infourl : 'http://www.enonic.com',
                version : '0.1'
            };
        },
        // ---------------------------------------------------------------------------------------------------------------------------------

        // Private

        /*
         function: _getWidthForEditorInstance
         */
        _getWidthForEditorInstance: function()
        {
            var t = this;
            var editor = t.editor;

            var bodyElementStyleMarginAndPadding = t._getBodyStyleMarginAndPaddingForEditorInstance( editor );

            var bodyMarginLeft     = bodyElementStyleMarginAndPadding.marginleft;
            var bodyMarginRight    = bodyElementStyleMarginAndPadding.marginright;
            var bodyPaddingLeft    = bodyElementStyleMarginAndPadding.paddingleft;
            var bodyPaddingRight   = bodyElementStyleMarginAndPadding.paddingright;

            var contentAreaWidth   = editor.settings.initial_width - 2; // Initial width - gui left and right border.

            return ( contentAreaWidth - ( bodyMarginLeft + bodyPaddingLeft ) - ( bodyMarginRight + bodyPaddingRight ) );
        },
        // ---------------------------------------------------------------------------------------------------------------------------------

        _getBodyStyleMarginAndPaddingForEditorInstance: function()
        {
            var t = this;
            var editor = t.editor;
            var DOM = editor.dom;

            var editorBodyElement = editor.getBody();

            var margintop, marginright, marginbottom ,marginleft,
                    paddingtop, paddingright, paddingbottom ,paddingleft;

            margintop       = parseInt(DOM.getStyle(editorBodyElement, 'margin-top', true))       || 0;
            marginright     = parseInt(DOM.getStyle(editorBodyElement, 'margin-right', true))     || 0;
            marginbottom    = parseInt(DOM.getStyle(editorBodyElement, 'margin-nottom', true))    || 0;
            marginleft      = parseInt(DOM.getStyle(editorBodyElement, 'margin-left', true))      || 0;

            paddingtop      = parseInt(DOM.getStyle(editorBodyElement, 'padding-top', true))      || 0;
            paddingright    = parseInt(DOM.getStyle(editorBodyElement, 'padding-right', true))    || 0;
            paddingbottom   = parseInt(DOM.getStyle(editorBodyElement, 'padding-bottom', true))   || 0;
            paddingleft     = parseInt(DOM.getStyle(editorBodyElement, 'padding-left', true))     || 0;

            return { 'margintop': margintop, 'marginright': marginright, 'marginbottom': marginbottom, 'marginleft': marginleft,
                'paddingtop': paddingtop, 'paddingright': paddingright, 'paddingbottom': paddingbottom, 'paddingleft': paddingleft };
        },
        // ---------------------------------------------------------------------------------------------------------------------------------

        _isCustomSize: function( imageSrc )
        {
            return imageSrc.match(imgSrcWithSizeParamEQCustomPattern);
        }
        // ---------------------------------------------------------------------------------------------------------------------------------
    });

    tinymce.PluginManager.add('internallinkplugin', tinymce.plugins.InternalLinkPlugin);
})();