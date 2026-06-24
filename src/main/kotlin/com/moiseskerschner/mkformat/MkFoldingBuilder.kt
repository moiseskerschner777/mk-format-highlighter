package com.moiseskerschner.mkformat

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement

class MkFoldingBuilder : FoldingBuilderEx() {

    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        val text = document.text
        val lines = text.split('\n').toTypedArray()
        if (lines.isEmpty()) return emptyArray()

        val lineStarts = IntArray(lines.size + 1)
        var pos = 0
        for (i in lines.indices) {
            lineStarts[i] = pos
            pos += lines[i].length + 1
        }
        lineStarts[lines.size] = pos

        val descriptors = mutableListOf<FoldingDescriptor>()

        for (i in lines.indices) {
            val line = lines[i]
            if (!line.trimEnd().endsWith(":")) continue

            val currentDepth = (line.length - line.trimStart().length) / 4

            // scan forward: skip blanks, stop at terminator (depth <= currentDepth)
            var endLine = i
            var hasNonBlankChild = false
            for (j in i + 1 until lines.size) {
                val nextLine = lines[j]
                if (nextLine.trimEnd().isEmpty()) continue
                val nextDepth = (nextLine.length - nextLine.trimStart().length) / 4
                if (nextDepth <= currentDepth) break
                endLine = j
                hasNonBlankChild = true
            }

            if (hasNonBlankChild) {
                val endOffset = lineStarts[endLine] + lines[endLine].length
                val startOffset = lineStarts[i]
                descriptors.add(
                    FoldingDescriptor(root.node, TextRange(startOffset, endOffset))
                )
            }
        }

        return descriptors.toTypedArray()
    }

    override fun getPlaceholderText(node: ASTNode): String = "..."

    override fun isCollapsedByDefault(node: ASTNode): Boolean = false
}
