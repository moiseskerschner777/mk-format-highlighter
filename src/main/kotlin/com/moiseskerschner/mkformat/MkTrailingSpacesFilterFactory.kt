package com.moiseskerschner.mkformat

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.StripTrailingSpacesFilter
import com.intellij.openapi.editor.StripTrailingSpacesFilterFactory
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project

class MkTrailingSpacesFilterFactory : StripTrailingSpacesFilterFactory() {

    override fun createFilter(project: Project?, document: Document): StripTrailingSpacesFilter {
        if (!isMkDocument(document)) return StripTrailingSpacesFilter.ALL_LINES
        return SpaceMarkedParentPreservingFilter(document)
    }

    private fun isMkDocument(document: Document): Boolean =
        FileDocumentManager.getInstance().getFile(document)?.fileType == MkFileType

    private class SpaceMarkedParentPreservingFilter(private val document: Document) : StripTrailingSpacesFilter {
        override fun isStripSpacesAllowedForLine(line: Int): Boolean =
            !MkParent.isSpaceMarkedParent(lineText(line))

        private fun lineText(line: Int): CharSequence =
            document.charsSequence.subSequence(document.getLineStartOffset(line), document.getLineEndOffset(line))
    }
}
