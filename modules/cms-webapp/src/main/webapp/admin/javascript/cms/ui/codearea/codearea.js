/*
 Dependices:

 CSS:

 <link rel="stylesheet" type="text/css" href="css/admin.css"/>

 JS:

 <script type="text/javascript" src="codemirror2/lib/codemirror.js">//</script>
 <script type="text/javascript" src="javascript/codearea.js">//</script>
 <script type="text/javascript" src="javascript/admin.js">//</script>
 */

(function(){
    'use strict';

    // Namespaces
    if (!window.cms) { window.cms = {}; }
    if (!window.cms.ui) { window.cms.ui = {}; }

    var ACTIVE_LINE_CSS_CLASS = 'CodeMirror-activeline';

    // Class (JS constructor function)
    var codeArea = cms.ui.CodeArea = function (textAreaId, codeMirrorConfig) {
        this.codeMirror = null;
        this.textAreaToConvert = document.getElementById(textAreaId);
        this.config = codeMirrorConfig || {};
        this.initCodeMirror();
        // this.reIndent();
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

    proto.reIndent = function () {
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
    };

})();