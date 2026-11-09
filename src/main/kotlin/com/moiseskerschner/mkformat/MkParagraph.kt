package com.moiseskerschner.mkformat

object MkParagraph {
    private val SENTENCE_TERMINATORS = setOf('.', '!', '?')
    private val LIST_ITEM_MARKERS = setOf('-', '*', '•')
    private const val LINE_WRAP_MARKER = ','

    fun leavesParagraphOpen(proseLine: CharSequence): Boolean {
        val content = proseLine.trim()
        if (content.isEmpty()) return false
        return content.last() !in SENTENCE_TERMINATORS
    }

    fun wrapsOntoNextLine(proseLine: CharSequence): Boolean =
        proseLine.trimEnd().endsWith(LINE_WRAP_MARKER)

    fun isListItemMarker(word: String): Boolean =
        word.length == 1 && word.first() in LIST_ITEM_MARKERS

    fun startsNewItem(line: CharSequence): Boolean {
        val content = line.trimStart()
        if (content.isEmpty()) return false
        return content.first() in LIST_ITEM_MARKERS
    }
}
