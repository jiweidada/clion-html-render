package com.htmlrender.preview

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.fileEditor.TextEditorWithPreview

/**
 * Reloads the rendered page from disk on demand.
 */
class RefreshPreviewAction(private val preview: HtmlRenderPreviewEditor) :
    AnAction("Refresh Preview", "Reload the rendered page from disk", AllIcons.Actions.Refresh) {

    override fun actionPerformed(e: AnActionEvent) {
        preview.refresh()
    }
}

/**
 * Composite "editor + preview" editor that adds a Refresh button next to the
 * platform's built-in Editor / Split / Preview toggles.
 *
 * The platform exposes two toolbar paths and only one of them is active at a
 * time: [isShowActionsInTabs] decides whether the layout toggles live in the
 * editor tab header (new UI) or in a floating toolbar. Both hooks are
 * overridden so the button shows up in either case.
 */
class HtmlRenderCompositeEditor(
    textEditor: TextEditor,
    previewEditor: HtmlRenderPreviewEditor,
    name: String,
) : TextEditorWithPreview(
    textEditor,
    previewEditor,
    name,
    TextEditorWithPreview.Layout.SHOW_PREVIEW,
) {

    private val refreshAction = RefreshPreviewAction(previewEditor)

    /** Floating-toolbar path (classic UI or "actions in tabs" switched off). */
    override fun createRightToolbarActionGroup(): ActionGroup =
        DefaultActionGroup(refreshAction)

    /** Tab-header path (new UI with tabs on top). */
    override fun createTabActions(): Array<AnAction> =
        super.createTabActions() + refreshAction
}
