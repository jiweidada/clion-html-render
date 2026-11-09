package com.htmlrender.preview

import com.intellij.codeHighlighting.BackgroundEditorHighlighter
import com.intellij.ide.structureView.StructureViewBuilder
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorLocation
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.fileEditor.FileEditorStateLevel
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.ui.jcef.JBCefApp
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.util.Alarm
import com.intellij.util.messages.MessageBusConnection
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.handler.CefLoadHandlerAdapter
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import javax.swing.JComponent
import java.text.SimpleDateFormat
import java.util.Date
import javax.swing.JLabel
import javax.swing.SwingConstants

/**
 * Renders an HTML file with JCEF and reloads it automatically (300 ms
 * debounce) when the file changes on disk, e.g. after a save in the source
 * editor.
 */
class HtmlRenderPreviewEditor(private val file: VirtualFile) : FileEditor {

    private val browser: JBCefBrowser? = if (JBCefApp.isSupported()) JBCefBrowser() else null
    private val fallback: JLabel = JLabel("JCEF is not available in this environment", SwingConstants.CENTER)
    private val changeSupport = PropertyChangeSupport(this)
    private val alarm = Alarm(Alarm.ThreadToUse.SWING_THREAD)
    private val connection: MessageBusConnection = ApplicationManager.getApplication().messageBus.connect()
    private val userDataHolder = UserDataHolderBase()

    /** Set right before a manual reload; consumed once the new document is ready. */
    @Volatile
    private var refreshNotice: String? = null

    init {
        if (browser != null) {
            browser.jbCefClient.addLoadHandler(object : CefLoadHandlerAdapter() {
                override fun onLoadEnd(cefBrowser: CefBrowser, frame: CefFrame?, httpStatusCode: Int) {
                    if (frame?.isMain != true) return
                    val notice = refreshNotice ?: return
                    refreshNotice = null
                    cefBrowser.executeJavaScript(toastScript(notice), cefBrowser.url, 0)
                }
            }, browser.cefBrowser)
            browser.loadURL(fileUrl())
            connection.subscribe(VirtualFileManager.VFS_CHANGES, object : BulkFileListener {
                override fun after(events: MutableList<out VFileEvent>) {
                    if (events.any { it.file == file }) scheduleReload()
                }
            })
        }
    }

    private fun fileUrl(): String = try {
        VfsUtilCore.virtualToIoFile(file).toURI().toString()
    } catch (e: Exception) {
        file.url
    }

    /** Reloads the page from disk immediately (toolbar Refresh button). */
    fun refresh() {
        if (!file.isValid || browser == null) return
        // Shown by the load handler once the reloaded document has finished
        // loading, so the user gets visible proof that the reload happened.
        refreshNotice = SimpleDateFormat("HH:mm:ss").format(Date())
        browser.loadURL(fileUrl())
    }

    /** A short self-removing "refreshed at ..." badge in the bottom-right corner. */
    private fun toastScript(stamp: String): String = """
        (function () {
          var id = '__htmlRenderRefreshToast';
          var old = document.getElementById(id);
          if (old) { old.remove(); }
          var el = document.createElement('div');
          el.id = id;
          el.textContent = '已刷新 $stamp';
          el.style.cssText = 'position:fixed;right:16px;bottom:16px;z-index:2147483647;' +
            'padding:8px 14px;border-radius:8px;background:rgba(28,28,30,.9);color:#fff;' +
            'font:13px/1.4 "Segoe UI",system-ui,sans-serif;box-shadow:0 4px 16px rgba(0,0,0,.28);' +
            'opacity:0;transition:opacity .18s ease;pointer-events:none';
          document.documentElement.appendChild(el);
          requestAnimationFrame(function () { el.style.opacity = '1'; });
          setTimeout(function () {
            el.style.opacity = '0';
            setTimeout(function () { el.remove(); }, 250);
          }, 1400);
        })();
    """.trimIndent()

    private fun scheduleReload() {
        alarm.cancelAllRequests()
        alarm.addRequest({
            if (file.isValid) browser?.loadURL(fileUrl())
        }, 300)
    }

    override fun getComponent(): JComponent = browser?.component ?: fallback

    override fun getPreferredFocusedComponent(): JComponent? = browser?.component

    override fun getName(): String = "HTML Render"

    override fun getFile(): VirtualFile? = file

    override fun isModified(): Boolean = false

    override fun isValid(): Boolean = true

    override fun selectNotify() = Unit

    override fun deselectNotify() = Unit

    override fun getState(level: FileEditorStateLevel): FileEditorState = FileEditorState.INSTANCE

    override fun setState(state: FileEditorState) = Unit

    override fun addPropertyChangeListener(listener: PropertyChangeListener) =
        changeSupport.addPropertyChangeListener(listener)

    override fun removePropertyChangeListener(listener: PropertyChangeListener) =
        changeSupport.removePropertyChangeListener(listener)

    override fun <T : Any> getUserData(key: Key<T>): T? = userDataHolder.getUserData(key)

    override fun <T : Any> putUserData(key: Key<T>, value: T?) {
        userDataHolder.putUserData(key, value)
    }

    override fun dispose() {
        alarm.cancelAllRequests()
        connection.disconnect()
        browser?.dispose()
    }

    override fun getBackgroundHighlighter(): BackgroundEditorHighlighter? = null

    override fun getCurrentLocation(): FileEditorLocation? = null

    override fun getStructureViewBuilder(): StructureViewBuilder? = null



}
