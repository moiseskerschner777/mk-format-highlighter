package com.moiseskerschner.mkformat

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

object MkFileType : LanguageFileType(MkLanguage) {
    override fun getName(): String = "mk-format"
    override fun getDescription(): String = "mk-format outline file"
    override fun getDefaultExtension(): String = "mk"
    override fun getIcon(): Icon? = null
}
