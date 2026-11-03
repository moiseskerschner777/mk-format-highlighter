package com.moiseskerschner.mkformat

object MkInlineLabel {
    private const val LABEL_SEPARATOR = ':'
    private const val MAX_LABEL_WORDS = 4
    private val WORD_SEPARATOR = Regex("\\s+")

    fun labelLength(line: CharSequence): Int? {
        if (MkParent.isParent(line)) return null
        val separatorIndex = firstSeparatorFollowedByWhitespace(line) ?: return null
        val label = line.subSequence(0, separatorIndex)
        if (label.isBlank()) return null
        if (isTooLongForLabel(label)) return null
        if (line.subSequence(separatorIndex + 1, line.length).isBlank()) return null
        return separatorIndex + 1
    }

    private fun isTooLongForLabel(label: CharSequence): Boolean =
        label.trim().split(WORD_SEPARATOR).size > MAX_LABEL_WORDS

    private fun firstSeparatorFollowedByWhitespace(line: CharSequence): Int? {
        for (index in 0 until line.length - 1) {
            if (line[index] == LABEL_SEPARATOR && line[index + 1].isWhitespace()) return index
        }
        return null
    }
}
