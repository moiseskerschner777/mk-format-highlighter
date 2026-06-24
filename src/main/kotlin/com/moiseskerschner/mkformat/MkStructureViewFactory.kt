package com.moiseskerschner.mkformat

import com.intellij.lang.PsiStructureViewFactory
import com.intellij.ide.structureView.StructureViewBuilder
import com.intellij.ide.structureView.StructureViewModelBase
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile

class MkStructureViewFactory : PsiStructureViewFactory {
    override fun getStructureViewBuilder(psiFile: PsiFile): StructureViewBuilder {
        return object : TreeBasedStructureViewBuilder() {
            override fun createStructureViewModel(editor: Editor?) =
                StructureViewModelBase(psiFile, object : StructureViewTreeElement {
                    override fun getValue() = psiFile
                    override fun getChildren() = emptyArray<StructureViewTreeElement>()
                    override fun getPresentation() = object : ItemPresentation {
                        override fun getPresentableText() = psiFile.name
                        override fun getIcon(unused: Boolean) = MkFileType.icon
                        override fun getLocationString() = null
                    }
                })
        }
    }
}
