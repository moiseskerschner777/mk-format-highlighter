package com.moiseskerschner.mkformat.comments

import com.intellij.openapi.ide.CopyPasteManager
import java.awt.datatransfer.StringSelection
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel

class MkCommentBarPanel(count: Int, fileName: String, comments: List<MkComment>) : JPanel() {
    init {
        val text = if (count == 1) "1 comment stacked" else "$count comments stacked"
        add(JLabel(text))
        val copyButton = JButton("Copy for agent")
        copyButton.addActionListener {
            val formatted = MkCommentFormatter.format(fileName, comments)
            CopyPasteManager.getInstance().setContents(StringSelection(formatted))
        }
        add(copyButton)
    }
}
