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
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.JBUI
import com.moiseskerschner.mkformat.MkFileType
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.datatransfer.StringSelection
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JPanel

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

        val textField = JBTextField()
        textField.preferredSize = Dimension(350, 24)

        val panel = JPanel(BorderLayout())
        panel.border = JBUI.Borders.compound(
            JBUI.Borders.customLine(JBColor.border(), 1),
            JBUI.Borders.empty(2)
        )
        panel.add(textField, BorderLayout.CENTER)

        val popup = JBPopupFactory.getInstance()
            .createComponentPopupBuilder(panel, textField)
            .setRequestFocus(true)
            .setCancelKeyEnabled(false)
            .setCancelOnWindowDeactivation(true)
            .createPopup()

        textField.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                when (e.keyCode) {
                    KeyEvent.VK_ENTER -> {
                        val request = textField.text
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
                    }
                    KeyEvent.VK_ESCAPE -> {
                        popup.cancel()
                    }
                }
            }
        })

        popup.showInBestPositionFor(editor)
    }
}
