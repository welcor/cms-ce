/**
 * $Id: editor_plugin_src.js 000 200=-05-24 00:00:00Z tan $
 *
 * @author Tan
 * @copyright Copyright ? 2004-2008, Enonic, All rights reserved.
 *
 * This plugin fixes som quirks in the content document and various
 * browser differences in the document.
 */

(function () {
    tinymce.create('tinymce.plugins.CMSModifications', {
        init: function (ed, url) {
            var t = this;

            ed.onBeforeSetContent.add(function (ed, o) {
                t._removeEncodedCdataTags(o);
                t._fixHtmlKludgesBeforeContentIsSet(o)
            });

            ed.onGetContent.add(function (ed, o) {
                t._fixHtmlKludgesOnGetContent(o);
            });

            ed.onBeforeGetContent.add(function (ed, o) {
                t._cleanPreTag(ed);
            });

            ed.onPostRender.add(function (ed, cm) {
                t._addHtmlAndFullScreenButton(ed, cm);
            });

            /*
             If the caret is inside a PRE element IE inserts a new PRE element each time the user hits enter.
             This will make the behaviour more Gecko like where a BR element is created.
             */
            if (tinymce.isIE || tinymce.isWebKit) {
                ed.onKeyDown.add(function (ed, e) {
                    var selection = ed.selection;
                    var selectedNodeIsPreElementAndKeyboardKeyIsEnter = selection.getNode().nodeName == 'PRE' && e.keyCode == 13;
                    if (selectedNodeIsPreElementAndKeyboardKeyIsEnter) {

                        // IE will not display the new line if there is no content.
                        selection.setContent('<br id="__" />&nbsp;', { format: 'raw' });
                        var n = ed.dom.get('__');
                        n.removeAttribute('id');

                        selection.select(n);
                        selection.collapse();

                        return tinymce.dom.Event.cancel(e);
                    }
                });
            }

        },

        getInfo: function () {
            return {
                longname: 'CMS Modifications',
                author: 'tan@enonic.com',
                authorurl: 'http://www.enonic.com',
                infourl: 'http://www.enonic.com',
                version: "1.0"
            };
        },

        _addHtmlAndFullScreenButton: function (ed, cm) {
            if (ed.settings.readonly) {
                return;
            }
            var editorId = ed.id;
            var pathRow = document.getElementById(editorId + '_path_row');
            var statusBar = pathRow.parentNode;

            pathRow.style.padding = '3px 0 0 0';
            statusBar.style.height = '26px';

            var buttons = '';

            if (ed.settings.accessToHtmlSource) {
                buttons += '<a class="mceButton mceButtonEnabled cms_code" title="' + ed.getLang('advanced.code_desc') + '" ' +
                           'onclick="javascript:tinyMCE.get(\'' + editorId + '\').execCommand(\'mceCodeEditor\',false); return false;" ' +
                           'onmousedown="return false;" href="javascript:;" ' +
                           'style="float:left"><span class="mceIcon cms_code"></span></a>';
            }

            buttons += '<a class="mceButton mceButtonEnabled mce_fullscreen" ' +
                       'title="' + ed.getLang('fullscreen.desc') + '" onclick="javascript:tinyMCE.get(\'' + editorId +
                       '\').execCommand(\'mceFullScreen\',false); return false;" ' +
                       'href="javascript:;" style="float:left"><span class="mceIcon mce_fullscreen"></span></a><span class="mceSeparator" style="float:left"></span>';

            var buttonWrapper = document.createElement('div');
            buttonWrapper.id = editorId + '_cms_button_wrapper';
            buttonWrapper.style.cssFloat = 'left';

            buttonWrapper.innerHTML = buttons;

            statusBar.insertBefore(buttonWrapper, pathRow);
        },

        /**
         * Removes encoded CDATA tags
         *  &lt;![CDATA[
         *  ]]>
         *
         * TinyMCE wraps a CDATA tag around script content. Since the XMLTool.serialize() also encodes CDATA tags.
         * TinyMCE will do this each time the content is opened.
         */
        _removeEncodedCdataTags: function (options) {
            options.content = options.content.replace(/\/\/ &lt;!\[CDATA\[/gim, '');
            options.content = options.content.replace(/\/\/ ]]&gt;/gim, '');
        },

        _fixHtmlKludgesBeforeContentIsSet: function (options) {
            options.content = options.content.replace(/<script\s+(.+)\/>/g, '<script $1><\/script>');
            // Make sure empty TD elements are padded with "&nbsp"
            options.content = options.content.replace(/<td\/>/g, '<td>&nbsp;<\/td>');
            // Make sure empty TEXTAREA elements are padded with "&nbsp"
            options.content = options.content.replace(/<textarea\s+(.+)\/>/g, '<textarea $1><\/textarea>');
            // Make sure empty LABEL elements are padded with "&nbsp"
            options.content = options.content.replace(/<label\s+(.+)\/>/g, '<label $1><\/label>');
            // Make sure @_moz_dirty is added to IMG elements so the ui works properly for Fx.
            options.content = options.content.replace(/<img\s+(.+)\/>/g, '<img $1 _moz_dirty="" \/>');
            // Make sure empty EMBED elements are padded with "&nbsp"
            options.content = options.content.replace(/<embed(.+\n).+\/>/g, '<embed$1>.</embed>');
        },

        _fixHtmlKludgesOnGetContent: function (options) {
            // Pad IFRAME elements with "cms_content"
            options.content = options.content.replace(/<iframe(.+?)>(|\s+)<\/iframe>/g, '<iframe$1>cms_content<\/iframe>');
            // Pad VIDEO elements with "cms_content"
            options.content = options.content.replace(/<video(.+?)>(|\s+)<\/video>/g, '<video$1>cms_content<\/video>');
            // Pad empty TD and TH elements with "&nbsp;"
            options.content = options.content.replace(/<td><\/td>/g, '<td>&nbsp;</td>');
            options.content = options.content.replace(/<th><\/th>/g, '<th>&nbsp;</th>');
        },

        _cleanPreTag: function (ed) {
            var pre = ed.dom.select('pre');

            // Replace &nbsp; entities with spaces in each PRE element
            tinymce.each(pre, function (el) {
                var cn = el.childNodes, n;
                // Find each node in the PRE element and do the necessary replacement.
                // We could use PRE.innerHTML and replace the value, but IE will not insert a space then.
                // Instead we loop through each node in the PRE element and do the replace.
                for (var i = 0; i < cn.length; i++) {
                    n = cn[i];
                    if (n.nodeValue) {
                        n.nodeValue = n.nodeValue.replace(/\xA0/gm, ' ');
                    }
                }
            });

            // Replace <br> elements with new lines
            var br = ed.dom.select('pre br');
            for (var i = 0; i < br.length; i++) {
                var nlChar;
                if (tinymce.isIE) {
                    nlChar = '\r\n';
                }
                else {
                    nlChar = '\n';
                }

                var nl = ed.getDoc().createTextNode(nlChar);
                ed.dom.insertAfter(nl, br[i]);
                ed.dom.remove(br[i]);
            }
        }

    });

    tinymce.PluginManager.add('cmsmodifications', tinymce.plugins.CMSModifications);
})();
