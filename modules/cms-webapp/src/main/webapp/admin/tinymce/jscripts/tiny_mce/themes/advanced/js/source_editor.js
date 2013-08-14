var SourceEditor = {

    codeArea: null,

    onLoadInit: function () {
        var self = SourceEditor;
        var textAreaToTransform = document.getElementById('htmlSource');

        textAreaToTransform.value = self.addNewlinesAfterBlockLevelTags(tinyMCEPopup.editor.getContent());

        self.codeArea = new cms.ui.CodeArea(textAreaToTransform.id, null, false);

        // Resize editor on window resize
        if (window.addEventListener) {
            window.addEventListener('resize', function () {
                // Make sure that the resize does not fired repeatedly.
                clearTimeout(this.id);
                this.id = setTimeout(function () {
                    self.resizeCodeAreaToWindowSize();
                }, 300);
            });
        }

        self.resizeCodeAreaToWindowSize();
    },

    resizeCodeAreaToWindowSize: function () {
        var viewportWidth  = document.documentElement.clientWidth,
            viewportHeight = document.documentElement.clientHeight;

        this.codeArea.setSize(viewportWidth - 6, viewportHeight - 60);
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
