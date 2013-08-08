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

    // Class (JS constructor function)
    var codeArea = cms.ui.CodeArea = function (textAreaId, codeMirrorConfig) {
        this.codeMirror = null;
        this.textAreaToConvert = document.getElementById(textAreaId);
        this.config = codeMirrorConfig || {};
        this.initCodeMirror();
        this.reindentAllLines();
    };

    var proto = codeArea.prototype;

    // -------------------------------------------------------------------------------------------------------------------------------------

    proto.initCodeMirror = function() {
        try {
            if (this.textAreaToConvert) {
                this.setDefaultConfig();
                this.codeMirror = CodeMirror.fromTextArea(this.textAreaToConvert, this.config);
            }
        }
        catch (err) {
            throw('CodeArea: Config error: ' + err);
        }
    };

    proto.getValue = function (w, h) {
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
        var userConfig = this.config;

        if (!userConfig.mode) {
            userConfig.mode = 'application/xml';
        }

        if (!userConfig.lineNumbers) {
            userConfig.lineNumbers = true;
        }

        if (!userConfig.lineWrapping) {
            userConfig.lineWrapping = true;
        }

        if (!userConfig.indentUnit) {
            userConfig.indentUnit = 4;
        }

        /* Add-ons */
        if (!userConfig.autoCloseTags) {
            userConfig.autoCloseTags = true;
        }

        if (!userConfig.styleActiveLine) {
            userConfig.styleActiveLine = true;
        }

        userConfig.highlightSelectionMatches = {showToken: /\w/};
    };

})();