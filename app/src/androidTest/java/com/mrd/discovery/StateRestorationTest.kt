package com.mrd.discovery

import android.content.Context
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mrd.discovery.feature.discovery.R as DiscoveryR
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val DEEP_INDEX = 40

/** Recreating the activity is what rotation does: everything the user set must still be there. */
@RunWith(AndroidJUnit4::class)
class StateRestorationTest {
    @get:Rule
    val composeRule = createEmptyComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()
    private var scenario: ActivityScenario<MainActivity>? = null

    @After
    fun tearDown() {
        scenario?.close()
    }

    private fun launchAndWaitForList() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        composeRule.waitForNode(CountHeader)
    }

    @Test
    fun scrollPositionSurvivesRecreation() {
        launchAndWaitForList()
        composeRule.onNode(hasScrollToIndexAction()).performScrollToIndex(DEEP_INDEX)
        composeRule.waitForIdle()
        composeRule.onAllNodes(CountHeader).assertCountEquals(0)

        scenario?.recreate()
        composeRule.waitForNode(hasScrollToIndexAction())
        composeRule.waitForIdle()

        composeRule.onAllNodes(CountHeader).assertCountEquals(0)
    }

    @Test
    fun searchTextAndOpenNowSurviveRecreation() {
        launchAndWaitForList()
        val openNow = context.getString(DiscoveryR.string.discovery_filter_open_now)
        composeRule.onNode(hasSetTextAction()).performTextInput("sushi")
        composeRule.onNodeWithText(openNow).performClick()
        composeRule.waitForIdle()

        scenario?.recreate()
        composeRule.waitForNode(hasSetTextAction())
        composeRule.waitForIdle()

        composeRule.onNode(hasSetTextAction()).assertTextContains("sushi")
        composeRule.onNodeWithText(openNow).assertIsSelected()
    }
}
