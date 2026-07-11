package com.moiseskerschner.mkformat

import com.intellij.psi.PsiElement
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy
import com.intellij.spellchecker.tokenizer.Tokenizer

/**
 * Enables spell checking inside .mk files.
 *
 * The mk-format PSI is flat: every non-blank line is a single leaf token whose
 * element type is one of the depth / depth-parent types produced by [MkLexer].
 * Those leaves are handed to the platform's plain-text tokenizer so only the
 * prose words get checked. The tokenizer's word splitter naturally skips the
 * leading indentation spaces, the trailing ':' parent marker and the newline,
 * so no structural/syntax characters are ever reported as typos. Blank-line
 * (whitespace) tokens and everything else fall through to the default strategy.
 */
class MkSpellcheckingStrategy : SpellcheckingStrategy() {
    override fun getTokenizer(element: PsiElement): Tokenizer<*> {
        val type = element.node?.elementType
        if (MkTokenTypes.DEPTHS.indexOf(type) >= 0 || MkTokenTypes.DEPTHS_PARENT.indexOf(type) >= 0) {
            return TEXT_TOKENIZER
        }
        return super.getTokenizer(element)
    }
}
