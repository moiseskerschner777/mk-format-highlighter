package com.moiseskerschner.mkformat.comments

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import java.awt.Dimension
import java.awt.Font
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea

class MkCommentBarPanel(count: Int, fileName: String, comments: List<MkComment>, project: Project) : JPanel() {
    init {
        val text = if (count == 1) "1 comment stacked" else "$count comments stacked"
        val label = JLabel(text)
        label.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                val formatted = MkCommentFormatter.format(fileName, comments)
                val textArea = JTextArea(formatted)
                textArea.isEditable = false
                textArea.font = Font("Monospaced", Font.PLAIN, 12)
                val scrollPane = JScrollPane(textArea)
                scrollPane.preferredSize = Dimension(500, 300)
                val popup = JBPopupFactory.getInstance()
                    .createComponentPopupBuilder(scrollPane, textArea)
                    .setCancelOnClickOutside(true)
                    .setCancelKeyEnabled(true)
                    .createPopup()
                popup.showCenteredInCurrentWindow(project)
            }
        })
        add(label)
    }
}
