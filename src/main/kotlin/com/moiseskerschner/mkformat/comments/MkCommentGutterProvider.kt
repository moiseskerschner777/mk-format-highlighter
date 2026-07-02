package com.moiseskerschner.mkformat.comments

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement

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
                @Suppress("DEPRECATION")
                result.add(
                    LineMarkerInfo(
                        element,
                        element.textRange,
                        AllIcons.General.Balloon,
                        { _ -> "Comment" },
                        null,
                        GutterIconRenderer.Alignment.RIGHT
                    )
                )
            }
        }
    }
}
