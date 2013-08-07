var SourceEditor = {

    codeArea: null,

    onLoadInit: function () {
        var self = SourceEditor;
        var textAreaToTransform = document.getElementById('htmlSource');

        textAreaToTransform.value = self.addNewlinesAfterBlockLevelTags(tinyMCEPopup.editor.getContent());

        self.codeArea = new cms.ui.CodeArea(textAreaToTransform.id);
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
        tinyMCEPopup.editor.setContent(this.codeArea.getValue(''));
        tinyMCEPopup.close();
    }

};

tinyMCEPopup.requireLangPack();
tinyMCEPopup.onInit.add(SourceEditor.onLoadInit);
