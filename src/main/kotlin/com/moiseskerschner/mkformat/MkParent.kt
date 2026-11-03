package com.moiseskerschner.mkformat

/**
 * A non-blank line is a "parent" if its content ends with ':' (trailing
 * whitespace ignored) or if it ends with exactly one trailing space.
 */
object MkParent {
    fun isParent(line: CharSequence): Boolean {
        val raw = line.trimEnd('\n', '\r')
        if (raw.isBlank()) return false
        return raw.trimEnd().endsWith(":") || (raw.endsWith(" ") && !raw.endsWith("  "))
    }

    /** The parent's label: the line trimmed, without the trailing ':' marker. */
    fun label(line: CharSequence): String {
        val trimmed = line.trim()
        return (if (trimmed.endsWith(":")) trimmed.dropLast(1) else trimmed).trim().toString()
    }
}
