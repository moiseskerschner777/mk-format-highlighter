package com.moiseskerschner.mkformat.comments

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile

object MkCommentFormatter {
    fun getRelativePath(project: Project, vf: VirtualFile): String {
        val contentRoot = ProjectRootManager.getInstance(project).fileIndex.getContentRootForFile(vf)
        return if (contentRoot != null) {
            VfsUtilCore.getRelativePath(vf, contentRoot) ?: vf.name
        } else {
            vf.name
        }
    }

    fun format(displayPath: String, fileName: String, comments: List<MkComment>): String {
        val sb = StringBuilder()
        sb.appendLine("$displayPath: $fileName")
        for (comment in comments) {
            sb.appendLine()
            sb.appendLine("line ${comment.lineNumber}: \"${comment.snippet}\"")
            sb.append("> ${comment.request}")
        }
        return sb.toString()
    }
}
