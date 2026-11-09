package com.moiseskerschner.mkformat

import com.intellij.lexer.LexerBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

class MkLexer : LexerBase() {
    private var buffer: CharSequence = ""
    private var bufferEnd = 0
    private var tokenStart = 0
    private var tokenEnd = 0
    private var tokenType: IElementType? = null
    private var tokenStartState = FRESH_LINE_STATE
    private var nextTokenState = FRESH_LINE_STATE

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.buffer = buffer
        this.bufferEnd = endOffset
        this.tokenStart = startOffset
        this.tokenStartState = initialState
        advanceToken()
    }

    override fun getState(): Int = tokenStartState
    override fun getTokenType(): IElementType? = tokenType
    override fun getTokenStart(): Int = tokenStart
    override fun getTokenEnd(): Int = tokenEnd

    override fun advance() {
        tokenStart = tokenEnd
        tokenStartState = nextTokenState
        advanceToken()
    }

    override fun getBufferSequence(): CharSequence = buffer
    override fun getBufferEnd(): Int = bufferEnd

    private fun advanceToken() {
        nextTokenState = FRESH_LINE_STATE
        if (tokenStart >= bufferEnd) {
            tokenType = null
            tokenEnd = tokenStart
            return
        }
        if (isInlineContentState(tokenStartState)) {
            lexInlineContent()
            return
        }
        lexLineStart()
    }

    private fun lexInlineContent() {
        val depth = tokenStartState - INLINE_CONTENT_STATE_OFFSET
        tokenEnd = endOfLineIncludingBreak(tokenStart)
        tokenType = MkTokenTypes.DEPTHS[depth]
    }

    private fun lexLineStart() {
        val lineText = buffer.subSequence(tokenStart, endOfLine(tokenStart)).toString()
        tokenEnd = endOfLineIncludingBreak(tokenStart)

        if (lineText.isBlank()) {
            tokenType = TokenType.WHITE_SPACE
            return
        }

        val depth = depthOf(lineText)
        if (MkParent.isParent(lineText)) {
            tokenType = MkTokenTypes.DEPTHS_PARENT[depth]
            return
        }

        val labelLength = labelLengthOutsideParagraph(lineText, depth)
        if (labelLength == null) {
            lexProseLine(lineText, depth)
            return
        }

        tokenEnd = tokenStart + labelLength
        tokenType = MkTokenTypes.DEPTHS_PARENT[depth]
        nextTokenState = INLINE_CONTENT_STATE_OFFSET + depth
    }

    private fun lexProseLine(lineText: String, depth: Int) {
        tokenType = proseTokenType(lineText, depth)
        if (MkParagraph.leavesParagraphOpen(lineText)) {
            nextTokenState = OPEN_PARAGRAPH_STATE_OFFSET + depth
        }
    }

    private fun proseTokenType(lineText: String, depth: Int): IElementType {
        if (wrapsIntoParentLine(lineText, depth)) return MkTokenTypes.DEPTHS_PARENT[depth]
        return MkTokenTypes.DEPTHS[depth]
    }

    private fun wrapsIntoParentLine(lineText: String, depth: Int): Boolean {
        if (!MkParagraph.wrapsOntoNextLine(lineText)) return false
        var lineStart = tokenEnd
        while (lineStart < bufferEnd) {
            val followingLine = buffer.subSequence(lineStart, endOfLine(lineStart)).toString()
            if (!continuesSameParagraph(followingLine, depth)) return false
            if (MkParent.isParent(followingLine)) return true
            if (!MkParagraph.wrapsOntoNextLine(followingLine)) return false
            lineStart = endOfLineIncludingBreak(lineStart)
        }
        return false
    }

    private fun continuesSameParagraph(lineText: String, depth: Int): Boolean {
        if (lineText.isBlank()) return false
        if (MkParagraph.startsNewItem(lineText)) return false
        return depthOf(lineText) == depth
    }

    private fun labelLengthOutsideParagraph(lineText: String, depth: Int): Int? {
        if (continuesOpenParagraph(lineText, depth)) return null
        return MkInlineLabel.labelLength(lineText)
    }

    private fun continuesOpenParagraph(lineText: String, depth: Int): Boolean {
        if (!isOpenParagraphState(tokenStartState)) return false
        if (MkParagraph.startsNewItem(lineText)) return false
        return tokenStartState - OPEN_PARAGRAPH_STATE_OFFSET == depth
    }

    private fun endOfLine(from: Int): Int {
        var index = from
        while (index < bufferEnd && buffer[index] != '\n') index++
        return index
    }

    private fun endOfLineIncludingBreak(from: Int): Int {
        val lineEnd = endOfLine(from)
        return if (lineEnd < bufferEnd) lineEnd + 1 else lineEnd
    }

    private fun depthOf(lineText: String): Int {
        val leadingSpaces = lineText.length - lineText.trimStart(' ').length
        return minOf(leadingSpaces / SPACES_PER_DEPTH, MAX_DEPTH)
    }

    private fun isOpenParagraphState(lexerState: Int): Boolean =
        lexerState in OPEN_PARAGRAPH_STATE_OFFSET until INLINE_CONTENT_STATE_OFFSET

    private fun isInlineContentState(lexerState: Int): Boolean = lexerState >= INLINE_CONTENT_STATE_OFFSET

    private companion object {
        const val SPACES_PER_DEPTH = 4
        const val MAX_DEPTH = 6
        const val FRESH_LINE_STATE = 0
        const val OPEN_PARAGRAPH_STATE_OFFSET = 1
        const val INLINE_CONTENT_STATE_OFFSET = OPEN_PARAGRAPH_STATE_OFFSET + MAX_DEPTH + 1
    }
}
