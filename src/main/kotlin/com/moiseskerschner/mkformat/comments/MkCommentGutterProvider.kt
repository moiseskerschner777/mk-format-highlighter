package com.moiseskerschner.mkformat.comments

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.ui.EditorNotifications
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel

class MkCommentGutterProvider : LineMarkerProvider {

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? = null

    override fun collectSlowLineMarkers(
        elements: MutableList<out PsiElement>,
        result: MutableCollection<in LineMarkerInfo<*>>
    ) {
        if (elements.isEmpty()) return
        val file = elements.first().containingFile ?: return
        val document = PsiDocumentManager.getInstance(file.project).getDocument(file) ?: return
        val manager = MkCommentManager.getInstance(document)
        val comments = manager.getComments()
        if (comments.isEmpty()) return

        val commentedLines = comments.map { document.getLineNumber(it.startOffset) }.toSet()
        val seenLines = mutableSetOf<Int>()

        for (element in elements) {
            val line = document.getLineNumber(element.textRange.startOffset)
            if (line in commentedLines && line !in seenLines) {
                seenLines.add(line)
                result.add(
                    object : LineMarkerInfo<PsiElement>(
                        element,
                        element.textRange,
                        AllIcons.General.Balloon,
                        { _ -> "Comment" },
                        { _, psiElement ->
                            val editor = FileEditorManager.getInstance(psiElement.project).selectedTextEditor
                            if (editor != null) {
                                showCommentPopup(editor, line)
                            }
                        },
                        GutterIconRenderer.Alignment.RIGHT
                    ) {}
                )
            }
        }
    }

    private fun showCommentPopup(editor: Editor, line: Int) {
        val project = editor.project ?: return
        val document = editor.document
        val manager = MkCommentManager.getInstance(document)
        val commentsOnLine = manager.getComments().filter {
            document.getLineNumber(it.startOffset) == line
        }
        if (commentsOnLine.isEmpty()) return

        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        var popupRef: JBPopup? = null

        for (comment in commentsOnLine) {
            val row = JPanel(BorderLayout())
            row.add(JLabel("  ${comment.request}  "), BorderLayout.CENTER)
            val clearBtn = JButton("x")
            clearBtn.preferredSize = Dimension(24, 20)
            clearBtn.addActionListener {
                manager.removeComment(comment.id)
                popupRef?.cancel()
                val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document)
                if (psiFile != null) {
                    DaemonCodeAnalyzer.getInstance(project).restart(psiFile)
                }
                EditorNotifications.getInstance(project).updateAllNotifications()
            }
            row.add(clearBtn, BorderLayout.EAST)
            panel.add(row)
        }

        val popup = JBPopupFactory.getInstance()
            .createComponentPopupBuilder(panel, null)
            .setCancelOnClickOutside(true)
            .setCancelKeyEnabled(true)
            .createPopup()
        popupRef = popup
        popup.showCenteredInCurrentWindow(project)
    }
}
