package com.moiseskerschner.mkformat

import com.intellij.ide.structureView.StructureViewModelBase
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.psi.PsiFile

class MkStructureViewTreeModel(private val psiFile: PsiFile) : StructureViewModelBase(psiFile, buildRoot(psiFile)) {

    override fun getSuitableClasses() = arrayOf(PsiFile::class.java)

    override fun getCurrentEditorElement(): Any? {
        val editor = FileEditorManager.getInstance(psiFile.project).selectedTextEditor ?: return null
        val root = getRoot() as? MkStructureViewElement ?: return null
        val caretOffset = editor.caretModel.offset
        return findElementAtOffset(root, caretOffset)
    }

    private fun findElementAtOffset(element: MkStructureViewElement, caretOffset: Int): MkStructureViewElement? {
        var best: MkStructureViewElement? = null
        val stack = ArrayDeque<MkStructureViewElement>()
        stack.addLast(element)
        while (stack.isNotEmpty()) {
            val current = stack.removeLast()
            if (current.offset <= caretOffset) {
                if (best == null || current.offset > best.offset) {
                    best = current
                }
            }
            for (child in current.children.asReversed()) {
                if (child.offset <= caretOffset) {
                    stack.addLast(child)
                }
            }
        }
        return best
    }
}

private fun buildRoot(psiFile: PsiFile): MkStructureViewElement {
    val text = psiFile.text
    val stack = arrayOfNulls<MkStructureViewElement>(7)
    val rootChildren = mutableListOf<MkStructureViewElement>()
    var i = 0
    var lineStart = 0

    while (i < text.length) {
        val ch = text[i]
        if (ch == '\n' || ch == '\r') {
            processLine(text, lineStart, i, psiFile, stack, rootChildren)
            i++
            if (ch == '\r' && i < text.length && text[i] == '\n') i++
            lineStart = i
        } else {
            i++
        }
    }
    if (lineStart < text.length) {
        processLine(text, lineStart, text.length, psiFile, stack, rootChildren)
    }

    return MkStructureViewElement(psiFile.name, 0, 0, psiFile, rootChildren)
}

private fun processLine(
    text: String,
    start: Int,
    end: Int,
    psiFile: PsiFile,
    stack: Array<MkStructureViewElement?>,
    rootChildren: MutableList<MkStructureViewElement>
) {
    val line = text.substring(start, end)
    val trimmed = line.trimEnd()
    if (trimmed.endsWith(":")) {
        val leadingSpaces = line.length - line.trimStart().length
        val depth = minOf(leadingSpaces / 4, 6)
        val label = trimmed.substring(0, trimmed.length - 1).trim()
        val node = MkStructureViewElement(label, depth, start, psiFile, mutableListOf())
        stack[depth] = node
        if (depth == 0) {
            rootChildren.add(node)
        } else {
            var parentDepth = depth - 1
            while (parentDepth >= 0 && stack[parentDepth] == null) {
                parentDepth--
            }
            if (parentDepth >= 0) {
                stack[parentDepth]!!.children.add(node)
            }
            for (d in depth + 1..6) {
                stack[d] = null
            }
        }
    }
}
