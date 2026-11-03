package com.moiseskerschner.mkformat

object MkParent {
    private const val COLON_MARKER = ":"
    private const val SPACE_MARKER = " "
    private const val DOUBLE_SPACE = "  "

    fun isParent(line: CharSequence): Boolean {
        val content = withoutLineBreak(line)
        if (content.isBlank()) return false
        return isColonMarkedParent(content) || isSpaceMarkedParent(content)
    }

    fun isSpaceMarkedParent(line: CharSequence): Boolean {
        val content = withoutLineBreak(line)
        if (content.isBlank()) return false
        return content.endsWith(SPACE_MARKER) && !content.endsWith(DOUBLE_SPACE)
    }

    fun label(line: CharSequence): String {
        val trimmed = line.trim()
        return trimmed.removeSuffix(COLON_MARKER).trim().toString()
    }

    private fun isColonMarkedParent(content: CharSequence): Boolean =
        content.trimEnd().endsWith(COLON_MARKER)

    private fun withoutLineBreak(line: CharSequence): CharSequence = line.trimEnd('\n', '\r')
}
