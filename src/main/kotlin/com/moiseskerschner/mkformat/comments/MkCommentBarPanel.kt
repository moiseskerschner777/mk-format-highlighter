package com.moiseskerschner.mkformat.comments

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.util.ui.JBUI
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextPane
import javax.swing.text.StyleConstants
import javax.swing.text.StyledDocument

class MkCommentBarPanel(count: Int, fileName: String, comments: List<MkComment>, project: Project) : JPanel() {
    init {
        val text = if (count == 1) "1 comment stacked" else "$count comments stacked"
        val label = JLabel(text)
        label.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                val textPane = JTextPane()
                textPane.isEditable = false
                textPane.isOpaque = false
                textPane.font = Font("Monospaced", Font.PLAIN, 12)
                textPane.border = JBUI.Borders.empty(12)

                val doc = textPane.styledDocument

                val depth0Style = doc.addStyle("depth0", null)
                StyleConstants.setForeground(depth0Style, Color(0xF5, 0xF7, 0xFA))
                val depth1Style = doc.addStyle("depth1", null)
                StyleConstants.setForeground(depth1Style, Color(0xFF, 0x9E, 0x64))
                val depth2Style = doc.addStyle("depth2", null)
                StyleConstants.setForeground(depth2Style, Color(0x7D, 0xCF, 0xFF))
                val depth3Style = doc.addStyle("depth3", null)
                StyleConstants.setForeground(depth3Style, Color(0x9E, 0xCE, 0x6A))

                doc.insertString(doc.length, "file: $fileName\n", depth0Style)
                for (comment in comments) {
                    doc.insertString(doc.length, "\n", null)
                    doc.insertString(doc.length, "line ${comment.lineNumber}: ", depth1Style)
                    doc.insertString(doc.length, "\"${comment.snippet}\"\n", depth2Style)
                    doc.insertString(doc.length, "> ${comment.request}", depth3Style)
                }

                val scrollPane = JScrollPane(textPane)
                scrollPane.preferredSize = Dimension(500, 300)
                val popup = JBPopupFactory.getInstance()
                    .createComponentPopupBuilder(scrollPane, textPane)
                    .setCancelOnClickOutside(true)
                    .setCancelKeyEnabled(true)
                    .createPopup()
                popup.showCenteredInCurrentWindow(project)
            }
        })
        add(label)
    }
}
