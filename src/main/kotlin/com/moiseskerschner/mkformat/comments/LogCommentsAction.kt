package com.moiseskerschner.mkformat.comments

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.diagnostic.Logger

class LogCommentsAction : AnAction() {

    private val logger = Logger.getInstance(LogCommentsAction::class.java)

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val manager = MkCommentManager.getInstance(editor.document)

        if (editor.selectionModel.hasSelection()) {
            manager.addComment(editor, "test request")
        }

        val comments = manager.getComments()
        for (comment in comments) {
            logger.info("line ${comment.lineNumber}: \"${comment.snippet}\" — ${comment.request}")
        }
    }
}
