package com.moiseskerschner.mkformat

import com.intellij.ide.structureView.StructureViewModelBase
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.psi.PsiFile

class MkStructureViewTreeModel(psiFile: PsiFile) : StructureViewModelBase(psiFile, buildRoot(psiFile)) {

    override fun getSuitableClasses() = arrayOf(PsiFile::class.java)
}

private fun buildRoot(psiFile: PsiFile): MkStructureViewElement {
    val text = psiFile.text
    val nodes = mutableListOf<MkStructureViewElement>()
    var i = 0
    var lineStart = 0

    while (i < text.length) {
        val ch = text[i]
        if (ch == '\n' || ch == '\r') {
            processLine(text, lineStart, i, psiFile, nodes)
            i++
            if (ch == '\r' && i < text.length && text[i] == '\n') i++
            lineStart = i
        } else {
            i++
        }
    }
    if (lineStart < text.length) {
        processLine(text, lineStart, text.length, psiFile, nodes)
    }

    return MkStructureViewElement(psiFile.name, 0, 0, psiFile, nodes)
}

private fun processLine(
    text: String,
    start: Int,
    end: Int,
    psiFile: PsiFile,
    nodes: MutableList<MkStructureViewElement>
) {
    val line = text.substring(start, end)
    val trimmed = line.trimEnd()
    if (trimmed.endsWith(":")) {
        val leadingSpaces = line.length - line.trimStart().length
        val depth = minOf(leadingSpaces / 4, 6)
        val label = trimmed.substring(0, trimmed.length - 1).trim()
        nodes.add(MkStructureViewElement(label, depth, start, psiFile, emptyList()))
    }
}
