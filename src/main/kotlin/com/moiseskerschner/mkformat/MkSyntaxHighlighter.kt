package com.moiseskerschner.mkformat

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType

class MkSyntaxHighlighter : SyntaxHighlighterBase() {
    override fun getHighlightingLexer(): Lexer = MkLexer()

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> {
        val depthIdx = MkTokenTypes.DEPTHS.indexOf(tokenType)
        if (depthIdx >= 0) return arrayOf(MkColors.DEPTH_KEYS[depthIdx])

        val parentIdx = MkTokenTypes.DEPTHS_PARENT.indexOf(tokenType)
        if (parentIdx >= 0) return arrayOf(MkColors.DEPTH_KEYS[parentIdx], MkColors.PARENT_BOLD)

        return emptyArray()
    }
}
