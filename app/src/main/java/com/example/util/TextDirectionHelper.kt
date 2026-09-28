package com.example.util

import androidx.compose.ui.unit.LayoutDirection

/**
 * Utility to detect Persian/Arabic scripts and dynamically assign
 * Right-to-Left (RTL) for Persian surveys/questions and Left-to-Right (LTR) for English.
 */
object TextDirectionHelper {
    // Regex matching Persian, Arabic, Kurdish, Urdu Unicode blocks
    private val RTL_CHAR_REGEX = Regex("[\u0600-\u06FF\u0750-\u077F\u08A0-\u08FF\uFB50-\uFDFF\uFE70-\uFEFF]")

    /**
     * Returns true if the string contains Persian/Arabic characters.
     */
    fun isRtlText(text: String?): Boolean {
        if (text.isNullOrBlank()) return false
        return RTL_CHAR_REGEX.containsMatchIn(text)
    }

    /**
     * Determines LayoutDirection: Rtl for Persian text, Ltr for English/Latin text.
     */
    fun getLayoutDirection(vararg texts: String?): LayoutDirection {
        val hasRtl = texts.any { isRtlText(it) }
        return if (hasRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
    }
}
