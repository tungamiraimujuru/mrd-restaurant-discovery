package com.mrd.discovery.core.database

/** Stores a list of cuisines in one column. The separator is a control character that cannot appear in names. */
internal object CuisineCodec {
    private const val SEPARATOR = '\u001F'

    fun encode(cuisines: List<String>): String =
        cuisines.joinToString(SEPARATOR.toString()) { it.replace(SEPARATOR, ' ') }

    fun decode(raw: String): List<String> = if (raw.isEmpty()) emptyList() else raw.split(SEPARATOR)
}
