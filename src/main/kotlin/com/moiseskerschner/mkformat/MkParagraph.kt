package com.moiseskerschner.mkformat

object MkParagraph {
    private val SENTENCE_TERMINATORS = setOf('.', '!', '?')
    private val LIST_ITEM_MARKERS = setOf('-', '*', '•')

    fun leavesParagraphOpen(proseLine: CharSequence): Boolean {
        val content = proseLine.trim()
        if (content.isEmpty()) return false
        return content.last() !in SENTENCE_TERMINATORS
    }

    fun startsNewItem(line: CharSequence): Boolean {
        val content = line.trimStart()
        if (content.isEmpty()) return false
        return content.first() in LIST_ITEM_MARKERS
    }
}
