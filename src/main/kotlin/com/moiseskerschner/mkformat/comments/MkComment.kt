package com.moiseskerschner.mkformat.comments

data class MkComment(
    val id: String,
    val startOffset: Int,
    val endOffset: Int,
    val snippet: String,
    val request: String,
    val lineNumber: Int
)
