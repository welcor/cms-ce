var SourceEditor = {

    codeMirror: null,

    onLoadInit: function () {

        var self = SourceEditor;

        var textArea = document.getElementById('htmlSource');
        // Populate textarea with html content from editor
        textArea.value = self.addNewlinesAfterBlockLevelTags(tinyMCEPopup.editor.getContent());

        self.codeMirror = CodeMirror.fromTextArea(textArea, {
            mode: 'htmlmixed',
            lineNumbers: true,
            lineWrapping: true,
            indentUnit: 4
        });

        self.indentAll();
    },

    indentAll: function () {
        var cm = this.codeMirror,
            lineCount = cm.lineCount();

        for (var i = 0, e = lineCount; i < e; ++i) {
            cm.indentLine(i);
        }
    },

    // Add some newlines as the parsed content from TinyMCE is stripped for newlines
    addNewlinesAfterBlockLevelTags: function (content) {
        var contentWithNewlines = content;

        // P tags
        contentWithNewlines = contentWithNewlines.replace(/(<p(?:\s+[^>]*)?>)/gim, '$1\n');
        contentWithNewlines = contentWithNewlines.replace(/<\/p>/gim, '\n</p>');

        // H1 - H6 tags
        contentWithNewlines = contentWithNewlines.replace(/(<h[1-6].*?>)/gim, '$1\n');
        contentWithNewlines = contentWithNewlines.replace(/(<\/h[1-6].*?>)/gim, '\n$1');

        return contentWithNewlines;
    },

    saveContent: function () {
        tinyMCEPopup.editor.setContent(this.codeMirror.getValue(''));
        tinyMCEPopup.close();
    }

};

tinyMCEPopup.requireLangPack();
tinyMCEPopup.onInit.add(SourceEditor.onLoadInit);
