package com.moiseskerschner.mkformat.comments

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationProvider
import com.moiseskerschner.mkformat.MkFileType
import java.util.function.Function
import javax.swing.JComponent

class MkCommentBarProvider : EditorNotificationProvider {

    override fun collectNotificationData(
        project: Project,
        file: VirtualFile
    ): Function<in FileEditor, out JComponent?>? {
        if (file.fileType !is MkFileType) return null
        val document = FileDocumentManager.getInstance().getDocument(file) ?: return null
        val manager = MkCommentManager.getInstance(document)
        val count = manager.getComments().size
        if (count == 0) return null
        return Function<FileEditor, JComponent> { MkCommentBarPanel(count, file.name, manager.getComments()) }
    }
}
