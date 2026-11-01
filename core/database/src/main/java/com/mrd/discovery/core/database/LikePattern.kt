package com.mrd.discovery.core.database

import java.util.Locale

/**
 * Turns user input into a contains-match LIKE pattern. Wildcards typed by the user are escaped so
 * "50%" searches for the text "50%" instead of matching everything.
 */
internal fun likePattern(query: String): String {
    val escaped = query.trim()
        .lowercase(Locale.ROOT)
        .replace("\\", "\\\\")
        .replace("%", "\\%")
        .replace("_", "\\_")
    return "%$escaped%"
}
