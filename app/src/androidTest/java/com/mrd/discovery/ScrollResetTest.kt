package com.mrd.discovery

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScrollResetTest {
    @get:Rule
    val composeRule = createEmptyComposeRule()

    private var scenario: ActivityScenario<MainActivity>? = null

    @After
    fun tearDown() {
        scenario?.close()
    }

    @Test
    fun aNewSearchStartsAtTheTopOfTheResults() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        composeRule.waitForNode(hasScrollToIndexAction())
        composeRule.waitForNode(CountHeader)
        composeRule.onNode(hasScrollToIndexAction()).performScrollToIndex(DEEP_INDEX)
        composeRule.waitForIdle()
        // The header is the first list item, so it is only composed while the top is on screen.
        composeRule.onAllNodes(CountHeader).assertCountEquals(0)

        composeRule.onNode(hasSetTextAction()).performTextInput("s")
        composeRule.waitForIdle()

        composeRule.waitForNode(CountHeader)
    }

    private companion object {
        const val DEEP_INDEX = 40
    }
}
