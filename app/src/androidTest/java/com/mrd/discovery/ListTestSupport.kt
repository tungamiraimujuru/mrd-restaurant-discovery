package com.mrd.discovery

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeTestRule

internal const val UI_TIMEOUT_MILLIS = 15_000L

/** The "(N restaurants found)" line: it is only composed while the top of the list is on screen. */
internal val CountHeader = hasText("found", substring = true)

internal fun ComposeTestRule.waitForNode(matcher: SemanticsMatcher) {
    waitUntil(UI_TIMEOUT_MILLIS) { onAllNodes(matcher).fetchSemanticsNodes().isNotEmpty() }
}
