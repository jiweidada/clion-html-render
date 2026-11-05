package com.htmlrender.preview

import com.intellij.ide.highlighter.HtmlFileType
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

/**
 * Provider for the preview side of the composite editor: renders the HTML
 * file with an embedded JCEF (Chromium) browser.
 */
class HtmlRenderPreviewProvider : FileEditorProvider {

    override fun accept(project: Project, file: VirtualFile): Boolean =
        file.isInLocalFileSystem && file.fileType == HtmlFileType.INSTANCE

    override fun createEditor(project: Project, file: VirtualFile): FileEditor =
        HtmlRenderPreviewEditor(file)

    override fun getEditorTypeId(): String = "html-render-preview"

    override fun getPolicy(): FileEditorPolicy = FileEditorPolicy.NONE
}
