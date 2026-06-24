package com.moiseskerschner.mkformat

import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.pom.Navigatable
import com.intellij.psi.PsiFile
import javax.swing.Icon

class MkStructureViewElement(
    val text: String,
    val depth: Int,
    val offset: Int,
    val psiFile: PsiFile,
    val children: List<MkStructureViewElement>
) : StructureViewTreeElement, ItemPresentation, Navigatable {

    override fun getValue(): Any = psiFile

    override fun getChildren(): Array<TreeElement> = children.toTypedArray()

    override fun getPresentation(): ItemPresentation = this

    override fun getPresentableText(): String = text

    override fun getLocationString(): String? = null

    override fun getIcon(unused: Boolean): Icon = MkFileType.icon

    override fun navigate(requestFocus: Boolean) {
        val descriptor = OpenFileDescriptor(psiFile.project, psiFile.virtualFile, offset)
        descriptor.navigate(requestFocus)
    }

    override fun canNavigate(): Boolean = true

    override fun canNavigateToSource(): Boolean = true
}
