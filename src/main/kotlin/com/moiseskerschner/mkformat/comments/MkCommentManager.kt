package com.moiseskerschner.mkformat.comments

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.RangeMarker
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.TextRange
import java.util.UUID

class MkCommentManager {

    private val comments = mutableListOf<MkComment>()
    private val markers = mutableListOf<RangeMarker>()

    fun addComment(editor: Editor, request: String): MkComment {
        val document = editor.document
        val selectionStart = editor.selectionModel.selectionStart
        val selectionEnd = editor.selectionModel.selectionEnd
        val text = document.getText(TextRange(selectionStart, selectionEnd))
        val lineNumber = document.getLineNumber(selectionStart) + 1
        val comment = MkComment(
            id = UUID.randomUUID().toString(),
            startOffset = selectionStart,
            endOffset = selectionEnd,
            snippet = text,
            request = request,
            lineNumber = lineNumber
        )
        comments.add(comment)
        markers.add(document.createRangeMarker(selectionStart, selectionEnd))
        return comment
    }

    fun getComments(): List<MkComment> {
        return comments.mapIndexed { index, comment ->
            val marker = markers[index]
            if (!marker.isValid) {
                comment
            } else {
                val document = marker.document
                val currentText = document.getText(TextRange(marker.startOffset, marker.endOffset))
                val currentLine = document.getLineNumber(marker.startOffset) + 1
                comment.copy(
                    startOffset = marker.startOffset,
                    endOffset = marker.endOffset,
                    snippet = currentText,
                    lineNumber = currentLine
                )
            }
        }
    }

    fun removeComment(id: String) {
        val index = comments.indexOfFirst { it.id == id }
        if (index >= 0) {
            markers[index].dispose()
            comments.removeAt(index)
            markers.removeAt(index)
        }
    }

    fun clearAll() {
        markers.forEach { it.dispose() }
        markers.clear()
        comments.clear()
    }

    companion object {
        val KEY = Key.create<MkCommentManager>("mkCommentManager")

        fun getInstance(document: Document): MkCommentManager {
            var manager = document.getUserData(KEY)
            if (manager == null) {
                manager = MkCommentManager()
                document.putUserData(KEY, manager)
            }
            return manager
        }
    }
}
