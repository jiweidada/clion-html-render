package com.htmlrender.preview

import com.intellij.ide.highlighter.HtmlFileType
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.fileEditor.TextEditorWithPreview
import com.intellij.openapi.fileEditor.TextEditorWithPreviewProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

/**
 * Makes HTML files open in a TextEditorWithPreview composite whose initial
 * layout is "preview only" — i.e. the file opens directly rendered, exactly
 * like Markdown files with the "Preview only" default.
 *
 * The platform's default policy of [TextEditorWithPreviewProvider] is
 * HIDE_DEFAULT_EDITOR, so the plain text editor tab is hidden and only this
 * composite tab is shown. The composite's toolbar provides the standard
 * Editor / Split / Preview toggle buttons.
 */
class HtmlRenderFileEditorProvider : TextEditorWithPreviewProvider(HtmlRenderPreviewProvider()) {

    override fun accept(project: Project, file: VirtualFile): Boolean =
        file.isInLocalFileSystem && file.fileType == HtmlFileType.INSTANCE

    override fun createSplitEditor(textEditor: TextEditor, previewEditor: FileEditor): FileEditor {
        return TextEditorWithPreview(
            textEditor,
            previewEditor,
            "HTML Render",
            TextEditorWithPreview.Layout.SHOW_PREVIEW
        )
    }
}
