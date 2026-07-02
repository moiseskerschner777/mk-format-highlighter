package com.moiseskerschner.mkformat.comments

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.psi.PsiDocumentManager
import com.intellij.ui.EditorNotifications
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBTextArea
import com.intellij.util.ui.JBUI
import com.moiseskerschner.mkformat.MkFileType
import java.awt.Dimension
import java.awt.KeyboardFocusManager
import java.awt.KeyEventDispatcher
import java.awt.datatransfer.StringSelection
import java.awt.event.KeyEvent
import javax.swing.JPanel
import javax.swing.JScrollPane

class AddCommentInlineAction : AnAction() {

    private val logger = Logger.getInstance(AddCommentInlineAction::class.java)

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.EDT

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        val vFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        e.presentation.isEnabledAndVisible = editor != null
                && vFile?.fileType is MkFileType
                && editor.selectionModel.hasSelection()
    }

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val vFile = e.getData(CommonDataKeys.VIRTUAL_FILE)

        val textArea = JBTextArea()
        textArea.rows = 3
        textArea.columns = 40
        textArea.lineWrap = true
        textArea.wrapStyleWord = true

        val scrollPane = JScrollPane(textArea)
        scrollPane.preferredSize = Dimension(350, 60)
        scrollPane.border = JBUI.Borders.compound(
            JBUI.Borders.customLine(JBColor.border(), 1),
            JBUI.Borders.empty(2)
        )

        val popup = JBPopupFactory.getInstance()
            .createComponentPopupBuilder(scrollPane, textArea)
            .setRequestFocus(true)
            .setCancelKeyEnabled(false)
            .setCancelOnWindowDeactivation(false)
            .setCancelOnClickOutside(false)
            .createPopup()

        val dispatcher = KeyEventDispatcher { event ->
            if (event.id == KeyEvent.KEY_PRESSED && event.keyCode == KeyEvent.VK_ENTER) {
                if (event.isShiftDown) {
                    textArea.replaceSelection("\n")
                    return@KeyEventDispatcher true
                }
                val request = textArea.text
                if (request.isNotBlank()) {
                    val manager = MkCommentManager.getInstance(editor.document)
                    val comment = manager.addComment(editor, request)
                    logger.info("line ${comment.lineNumber}: \"${comment.snippet}\" — ${comment.request}")
                    if (vFile != null) {
                        val formatted = MkCommentFormatter.format(vFile.name, manager.getComments())
                        CopyPasteManager.getInstance().setContents(StringSelection(formatted))
                    }
                    val project = editor.project
                    if (project != null) {
                        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.document)
                        if (psiFile != null) {
                            DaemonCodeAnalyzer.getInstance(project).restart(psiFile)
                        }
                        EditorNotifications.getInstance(project).updateAllNotifications()
                    }
                }
                popup.cancel()
                return@KeyEventDispatcher true
            }
            if (event.id == KeyEvent.KEY_PRESSED && event.keyCode == KeyEvent.VK_ESCAPE) {
                popup.cancel()
                return@KeyEventDispatcher true
            }
            false
        }

        val kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager()
        kfm.addKeyEventDispatcher(dispatcher)

        val savedOffset = editor.caretModel.offset
        val selectionStart = editor.selectionModel.selectionStart
        editor.caretModel.moveToOffset(selectionStart)
        popup.showInBestPositionFor(editor)
        editor.caretModel.moveToOffset(savedOffset)
    }
}
