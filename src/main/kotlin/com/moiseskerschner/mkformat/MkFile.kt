package com.moiseskerschner.mkformat

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider

class MkFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, MkLanguage) {
    override fun getFileType(): FileType = MkFileType
}
