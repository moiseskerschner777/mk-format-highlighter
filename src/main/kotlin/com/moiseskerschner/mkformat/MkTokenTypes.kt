package com.moiseskerschner.mkformat

import com.intellij.psi.tree.IElementType

class MkElementType(debugName: String) : IElementType(debugName, MkLanguage)

object MkTokenTypes {
    val DEPTH0 = MkElementType("MK_DEPTH0")
    val DEPTH1 = MkElementType("MK_DEPTH1")
    val DEPTH2 = MkElementType("MK_DEPTH2")
    val DEPTH3 = MkElementType("MK_DEPTH3")
    val DEPTH4 = MkElementType("MK_DEPTH4")
    val DEPTH5 = MkElementType("MK_DEPTH5")
    val DEPTH6 = MkElementType("MK_DEPTH6")

    val DEPTH0_PARENT = MkElementType("MK_DEPTH0_PARENT")
    val DEPTH1_PARENT = MkElementType("MK_DEPTH1_PARENT")
    val DEPTH2_PARENT = MkElementType("MK_DEPTH2_PARENT")
    val DEPTH3_PARENT = MkElementType("MK_DEPTH3_PARENT")
    val DEPTH4_PARENT = MkElementType("MK_DEPTH4_PARENT")
    val DEPTH5_PARENT = MkElementType("MK_DEPTH5_PARENT")
    val DEPTH6_PARENT = MkElementType("MK_DEPTH6_PARENT")

    // Index N = depth N, both arrays line up so the highlighter can do a
    // single lookup regardless of whether the line is a parent or not.
    val DEPTHS = arrayOf(DEPTH0, DEPTH1, DEPTH2, DEPTH3, DEPTH4, DEPTH5, DEPTH6)
    val DEPTHS_PARENT = arrayOf(
        DEPTH0_PARENT, DEPTH1_PARENT, DEPTH2_PARENT, DEPTH3_PARENT,
        DEPTH4_PARENT, DEPTH5_PARENT, DEPTH6_PARENT
    )
}
