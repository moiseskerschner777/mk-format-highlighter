package com.moiseskerschner.mkformat.comments

import javax.swing.JLabel
import javax.swing.JPanel

class MkCommentBarPanel(count: Int) : JPanel() {
    init {
        val text = if (count == 1) "1 comment stacked" else "$count comments stacked"
        add(JLabel(text))
    }
}
