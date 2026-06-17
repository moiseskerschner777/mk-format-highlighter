package com.moiseskerschner.mkformat

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object MkFileType : LanguageFileType(MkLanguage) {
    override fun getName(): String = "mk-format"
    override fun getDescription(): String = "mk-format outline file"
    override fun getDefaultExtension(): String = "mk"
    private val ICON: Icon = IconLoader.getIcon("/icons/mk.svg", MkFileType::class.java)
    override fun getIcon(): Icon = ICON
}
