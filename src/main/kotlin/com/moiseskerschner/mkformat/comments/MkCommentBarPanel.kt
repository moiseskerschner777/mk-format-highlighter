package com.moiseskerschner.mkformat.comments

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.util.ui.JBUI
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Font
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane

class MkCommentBarPanel(count: Int, fileName: String, comments: List<MkComment>, project: Project, onRemoveComment: (String) -> Unit) : JPanel() {
    init {
        val text = if (count == 1) "1 comment stacked" else "$count comments stacked"
        val label = JLabel(text)
        label.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                showPreview(fileName, comments, project, onRemoveComment)
            }
        })
        add(label)
    }

    private fun showPreview(fileName: String, comments: List<MkComment>, project: Project, onRemoveComment: (String) -> Unit) {
        var popupRef: JBPopup? = null

        val content = buildContent(fileName, comments) { id ->
            onRemoveComment(id)
            popupRef?.cancel()
            val remaining = comments.filter { it.id != id }
            if (remaining.isNotEmpty()) {
                showPreview(fileName, remaining, project, onRemoveComment)
            }
        }

        val scrollPane = JScrollPane(content)
        scrollPane.preferredSize = Dimension(550, 300)

        val popup = JBPopupFactory.getInstance()
            .createComponentPopupBuilder(scrollPane, content)
            .setCancelOnClickOutside(true)
            .setCancelKeyEnabled(true)
            .createPopup()
        popupRef = popup
        popup.showCenteredInCurrentWindow(project)
    }

    private fun buildContent(fileName: String, comments: List<MkComment>, onRemove: (String) -> Unit): JPanel {
        val content = JPanel()
        content.layout = BoxLayout(content, BoxLayout.Y_AXIS)
        content.border = JBUI.Borders.empty(12, 12, 12, 12)
        content.alignmentX = Component.LEFT_ALIGNMENT

        val header = JLabel("file: $fileName")
        header.foreground = Color(0xF5, 0xF7, 0xFA)
        header.font = Font("Monospaced", Font.PLAIN, 12)
        header.alignmentX = Component.LEFT_ALIGNMENT
        content.add(header)

        for (comment in comments) {
            content.add(JLabel(" ").apply { alignmentX = Component.LEFT_ALIGNMENT })

            val row = JPanel()
            row.layout = BoxLayout(row, BoxLayout.X_AXIS)
            row.alignmentX = Component.LEFT_ALIGNMENT
            row.maximumSize = Dimension(Int.MAX_VALUE, 18)

            val lineLabel = JLabel("line ${comment.lineNumber}: ")
            lineLabel.foreground = Color(0xFF, 0x9E, 0x64)
            lineLabel.font = Font("Monospaced", Font.PLAIN, 12)
            row.add(lineLabel)

            val snippetLabel = JLabel("\"${comment.snippet}\"")
            snippetLabel.foreground = Color(0x7D, 0xCF, 0xFF)
            snippetLabel.font = Font("Monospaced", Font.PLAIN, 12)
            row.add(snippetLabel)

            row.add(Box.createHorizontalGlue())

            val removeBtn = JButton("x")
            removeBtn.preferredSize = Dimension(16, 16)
            removeBtn.minimumSize = Dimension(16, 16)
            removeBtn.maximumSize = Dimension(16, 16)
            removeBtn.font = Font("Monospaced", Font.PLAIN, 8)
            removeBtn.margin = java.awt.Insets(0, 0, 0, 0)
            removeBtn.addActionListener { onRemove(comment.id) }
            row.add(removeBtn)
            content.add(row)

            val requestLabel = JLabel("> ${comment.request}")
            requestLabel.foreground = Color(0x9E, 0xCE, 0x6A)
            requestLabel.font = Font("Monospaced", Font.PLAIN, 12)
            requestLabel.alignmentX = Component.LEFT_ALIGNMENT
            content.add(requestLabel)
        }

        return content
    }
}
