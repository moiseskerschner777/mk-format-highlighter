package com.moiseskerschner.mkformat

import com.intellij.lexer.LexerBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

/**
 * Walks the file one line at a time. Depth = leading-space count / 4,
 * clamped to 6 (matching the mk-editor artifact's DEPTH_COLORS array,
 * which clamps the same way). A line is a "parent" if its trimmed
 * content ends with ':'.
 */
class MkLexer : LexerBase() {
    private var buffer: CharSequence = ""
    private var bufferEnd = 0
    private var tokenStart = 0
    private var tokenEnd = 0
    private var tokenType: IElementType? = null

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.buffer = buffer
        this.bufferEnd = endOffset
        this.tokenStart = startOffset
        advanceToken()
    }

    override fun getState(): Int = 0
    override fun getTokenType(): IElementType? = tokenType
    override fun getTokenStart(): Int = tokenStart
    override fun getTokenEnd(): Int = tokenEnd

    override fun advance() {
        tokenStart = tokenEnd
        advanceToken()
    }

    override fun getBufferSequence(): CharSequence = buffer
    override fun getBufferEnd(): Int = bufferEnd

    private fun advanceToken() {
        if (tokenStart >= bufferEnd) {
            tokenType = null
            tokenEnd = tokenStart
            return
        }

        var i = tokenStart
        while (i < bufferEnd && buffer[i] != '\n') i++
        val lineEnd = i
        if (i < bufferEnd) i++ // swallow the newline into this token's range
        tokenEnd = i

        val lineText = buffer.subSequence(tokenStart, lineEnd).toString()

        if (lineText.isBlank()) {
            tokenType = TokenType.WHITE_SPACE
            return
        }

        var spaces = 0
        while (spaces < lineText.length && lineText[spaces] == ' ') spaces++
        val depth = minOf(spaces / 4, 6)
        val isParent = lineText.trimEnd().endsWith(":")

        tokenType = if (isParent) MkTokenTypes.DEPTHS_PARENT[depth] else MkTokenTypes.DEPTHS[depth]
    }
}
