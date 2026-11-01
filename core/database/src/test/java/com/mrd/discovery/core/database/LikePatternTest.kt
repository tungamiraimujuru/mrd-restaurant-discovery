package com.mrd.discovery.core.database

import org.junit.Assert.assertEquals
import org.junit.Test

class LikePatternTest {
    @Test
    fun plainQueryIsWrappedInWildcards() = assertEquals("%sushi%", likePattern("Sushi"))

    @Test
    fun queryIsTrimmedAndLowercased() = assertEquals("%sushi%", likePattern("  SuShI "))

    @Test
    fun likeWildcardsAreEscaped() = assertEquals("%50\\%\\_\\\\%", likePattern("50%_\\"))

    @Test
    fun emptyQueryMatchesEverything() = assertEquals("%%", likePattern(""))
}
