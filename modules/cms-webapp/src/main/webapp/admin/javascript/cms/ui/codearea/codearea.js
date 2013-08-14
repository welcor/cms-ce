/*
    Dependices:

        common/codearea-scripts.xsl

        - contains all necessary js and css

    Usage:

        <xsl:include href="common/codearea-scripts.xsl"/>

        <head>
            <xsl:call-template name="codearea-scripts"/> ...
        </head>

        <textarea id="xml_field">
            Hello, world!
        </textarea>
        <script>
            var codeArea = new cms.ui.CodeArea('xml_field', [codemirror-options])
        </script>
*/

(function(){
    'use strict';
    // Namespaces
    if (!window.cms) { window.cms = {}; }
    if (!window.cms.ui) { window.cms.ui = {}; }

    /**
     * Class CodeArea
     *
     * @textAreaId {String} id of the textarea element that should be transformed.
     * @codeMirrorConfig {CodeMirrorConfig} optional
     * @enableAutoResize {Boolean} optional
     */
    var codeArea = cms.ui.CodeArea = function (textAreaId, codeMirrorConfig, enableAutoResize) {
        this.codeMirror = null;
        this.textAreaToConvert = document.getElementById(textAreaId);
        this.config = codeMirrorConfig || {};
        this.enableAutoResize = enableAutoResize || true;

        this.initCodeMirror();
        this.reindentAllLines();
    };

    var proto = codeArea.prototype;

    // -------------------------------------------------------------------------------------------------------------------------------------

    proto.initCodeMirror = function() {
        try {
            if (this.textAreaToConvert) {
                this.setDefaultConfig();
                if (this.enableAutoResize) {
                    this.appendCodeMirrorAutoResizeCssToHeadElement();
                }

                this.codeMirror = CodeMirror.fromTextArea(this.textAreaToConvert, this.config);
            }
        }
        catch (err) {
            throw('CodeArea: Config error: ' + err);
        }
    };

    proto.getValue = function () {
        return this.codeMirror.getValue();
    };

    proto.reindentAllLines = function () {
        var lineCount = this.codeMirror.lineCount();
        for (var line = 0; line < lineCount; line++) {
            this.codeMirror.indentLine(line);
        }
    };

    proto.setSize = function (w, h) {
        this.codeMirror.setSize(w, h);
    };

    proto.setDefaultConfig = function () {
        var config = this.config;

        if (this.enableAutoResize) {
            config.viewportMargin = Infinity;
        }

        if (!config.mode) {
            config.mode = 'application/xml';
        }

        if (!config.lineNumbers) {
            config.lineNumbers = true;
        }

        if (!config.lineWrapping) {
            config.lineWrapping = true;
        }

        if (!config.indentUnit) {
            config.indentUnit = 4;
        }

        /* Add-on specific configuration */

        if (!config.autoCloseTags) {
            config.autoCloseTags = true;
        }

        if (!config.styleActiveLine) {
            config.styleActiveLine = true;
        }
        config.highlightSelectionMatches = {showToken: /\w/};
    };

    proto.appendCodeMirrorAutoResizeCssToHeadElement = function () {
        var css = '.CodeMirror { height: auto } .CodeMirror-scroll {  min-height: 130px; overflow-y: hidden; overflow-x: auto; }',
            headEl = document.getElementsByTagName('head')[0],
            styleEl = document.createElement('style');

        styleEl.type = 'text/css';
        if (styleEl.styleSheet) {
            styleEl.styleSheet.cssText = css;
        } else {
            styleEl.appendChild(document.createTextNode(css));
        }

        headEl.appendChild(styleEl);
    };

    proto.isBrowserSupported = function () {
        var isInternetExplorerAndVersionLowerThan8 = document.all && document.documentMode == undefined;

        return !isInternetExplorerAndVersionLowerThan8;
    }

})();