package com.moiseskerschner.mkformat.comments

object MkCommentFormatter {
    fun format(fileName: String, comments: List<MkComment>): String {
        val sb = StringBuilder()
        sb.appendLine("file: $fileName")
        for (comment in comments) {
            sb.appendLine()
            sb.appendLine("line ${comment.lineNumber}: \"${comment.snippet}\"")
            sb.append("> ${comment.request}")
        }
        return sb.toString()
    }
}
