package com.moiseskerschner.mkformat

import com.intellij.lang.PsiStructureViewFactory
import com.intellij.ide.structureView.StructureViewBuilder
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile

private val LOG = Logger.getInstance("mk-format.StructureViewFactory")

class MkStructureViewFactory : PsiStructureViewFactory {
    override fun getStructureViewBuilder(psiFile: PsiFile): StructureViewBuilder {
        LOG.info("Creating structure view builder for ${psiFile.name}")
        return object : TreeBasedStructureViewBuilder() {
            override fun createStructureViewModel(editor: Editor?): MkStructureViewTreeModel {
                LOG.info("Creating structure view model for ${psiFile.name}")
                return try {
                    MkStructureViewTreeModel(psiFile, editor)
                } catch (e: Exception) {
                    LOG.error("Failed to create structure view model", e)
                    throw e
                }
            }
        }
    }
}
