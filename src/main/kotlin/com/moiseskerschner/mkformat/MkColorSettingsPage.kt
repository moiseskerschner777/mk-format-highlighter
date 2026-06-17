package com.moiseskerschner.mkformat

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import javax.swing.Icon

class MkColorSettingsPage : ColorSettingsPage {
    private val descriptors = arrayOf(
        AttributesDescriptor("Depth 0 (top level)", MkColors.DEPTH0),
        AttributesDescriptor("Depth 1", MkColors.DEPTH1),
        AttributesDescriptor("Depth 2", MkColors.DEPTH2),
        AttributesDescriptor("Depth 3", MkColors.DEPTH3),
        AttributesDescriptor("Depth 4", MkColors.DEPTH4),
        AttributesDescriptor("Depth 5", MkColors.DEPTH5),
        AttributesDescriptor("Depth 6+ (deepest)", MkColors.DEPTH6),
        AttributesDescriptor("Parent line (bold)", MkColors.PARENT_BOLD)
    )

    override fun getDisplayName(): String = "mk-format"
    override fun getIcon(): Icon? = null
    override fun getHighlighter(): SyntaxHighlighter = MkSyntaxHighlighter()

    override fun getDemoText(): String = """
project:

    prompt_provider:
        base.py:
            validity_provider.py:
                - full-module chunks:
                    database.py and config.py returned intact
        registry.py
    indexer.py
""".trimIndent()

    override fun getAdditionalHighlightingTagToDescriptorMap(): MutableMap<String, TextAttributesKey>? = null
    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = descriptors
    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY
}
