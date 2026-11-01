package com.mrd.discovery

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mrd.discovery.feature.details.R as DetailsR
import com.mrd.discovery.feature.discovery.R as DiscoveryR
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val TIMEOUT_MILLIS = 15_000L

/** Runs against the real app graph and the in-app mock backend, with whatever is in the cache. */
@RunWith(AndroidJUnit4::class)
class DeepLinkTest {
    @get:Rule
    val composeRule = createEmptyComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()
    private var scenario: ActivityScenario<MainActivity>? = null

    @After
    fun tearDown() {
        scenario?.close()
    }

    private fun launchLink(id: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mrd://restaurant/$id"))
            .setClass(context, MainActivity::class.java)
        scenario = ActivityScenario.launch(intent)
    }

    private fun waitForText(text: String) {
        composeRule.waitUntil(TIMEOUT_MILLIS) {
            composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun aLinkOpensTheRestaurant() {
        launchLink("r-1001")

        waitForText("Cape Town Grill")
    }

    @Test
    fun aLinkToAnUnknownRestaurantShowsNotFound() {
        launchLink("does-not-exist")

        waitForText(context.getString(DetailsR.string.details_not_found_title))
    }

    @Test
    fun upFromALinkLandsOnTheList() {
        launchLink("r-1001")
        waitForText("Cape Town Grill")

        composeRule.onNodeWithContentDescription(context.getString(DetailsR.string.details_back)).performClick()

        waitForText(context.getString(DiscoveryR.string.discovery_title))
    }
}
