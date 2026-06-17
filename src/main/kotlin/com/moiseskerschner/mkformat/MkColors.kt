package com.moiseskerschner.mkformat

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.ui.ColorUtil
import java.awt.Font

/**
 * Defaults mirror mk-editor's DEPTH_COLORS array exactly:
 * ["#f5f7fa","#ff9e64","#7dcfff","#9ece6a","#bb9af7","#f7768e","#e0af68"]
 * These are brand-new keys, not shared Language Defaults, so editing them
 * here has zero effect on Java/Python/Angular/etc. highlighting.
 */
object MkColors {
    private fun key(name: String, hex: String): TextAttributesKey {
        val attrs = TextAttributes(ColorUtil.fromHex(hex), null, null, null, Font.PLAIN)
        return TextAttributesKey.createTextAttributesKey("MK_$name", attrs)
    }

    val DEPTH0: TextAttributesKey = key("DEPTH0", "F5F7FA")
    val DEPTH1: TextAttributesKey = key("DEPTH1", "FF9E64")
    val DEPTH2: TextAttributesKey = key("DEPTH2", "7DCFFF")
    val DEPTH3: TextAttributesKey = key("DEPTH3", "9ECE6A")
    val DEPTH4: TextAttributesKey = key("DEPTH4", "BB9AF7")
    val DEPTH5: TextAttributesKey = key("DEPTH5", "F7768E")
    val DEPTH6: TextAttributesKey = key("DEPTH6", "E0AF68")
    val DEPTH_KEYS = arrayOf(DEPTH0, DEPTH1, DEPTH2, DEPTH3, DEPTH4, DEPTH5, DEPTH6)

    // Layered on top of a depth key for parent (colon-ending) lines.
    // No foreground of its own, so it adds bold without overriding the
    // depth color underneath.
    val PARENT_BOLD: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "MK_PARENT_BOLD",
        TextAttributes(null, null, null, null, Font.BOLD)
    )
}
