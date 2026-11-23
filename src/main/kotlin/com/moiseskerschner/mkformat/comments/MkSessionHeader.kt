package com.moiseskerschner.mkformat.comments

object MkSessionHeader {

    private const val SESSION_PREFIX = "session:"

    fun terminalTabName(documentText: String): String? {
        val firstLine = documentText.lineSequence().firstOrNull { it.isNotBlank() } ?: return null
        val trimmedLine = firstLine.trim()
        if (!trimmedLine.startsWith(SESSION_PREFIX, ignoreCase = true)) return null
        return trimmedLine.substring(SESSION_PREFIX.length).trim().ifEmpty { null }
    }

}
