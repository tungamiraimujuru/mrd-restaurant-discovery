package com.mrd.discovery.feature.discovery

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mrd.discovery.core.designsystem.theme.MrDTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private class Events {
    val queries = mutableListOf<String>()
    var openNowToggles = 0
    var refreshes = 0
    val clicks = mutableListOf<String>()
    val favorites = mutableListOf<Pair<String, Boolean>>()
}

/** Drives the stateless screen with fixed states, so every state and every event is covered directly. */
@RunWith(AndroidJUnit4::class)
class DiscoveryScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    private fun text(id: Int, vararg args: Any): String = context.getString(id, *args)

    private fun show(state: DiscoveryUiState): Events {
        val events = Events()
        composeRule.setContent {
            MrDTheme {
                DiscoveryScreenContent(
                    state = state,
                    onQueryChange = { events.queries += it },
                    onOpenNowToggle = { events.openNowToggles++ },
                    onRefresh = { events.refreshes++ },
                    onRestaurantClick = { events.clicks += it },
                    onFavoriteClick = { id, favorite -> events.favorites += id to favorite },
                )
            }
        }
        return events
    }

    private val grill = previewRestaurant(id = "r-1001", name = "Cape Town Grill")
    private val sushi = previewRestaurant(id = "r-1002", name = "Sushi on Sea Point", isOpen = false)
        .copy(isFavorite = true)

    private fun loaded(offline: OfflineNotice? = null) =
        DiscoveryUiState(content = DiscoveryContent.Loaded(listOf(grill, sushi), offline))

    @Test
    fun loadingShowsTheSpinner() {
        show(DiscoveryUiState(content = DiscoveryContent.Loading))

        composeRule.onNodeWithContentDescription(text(R.string.discovery_loading)).assertIsDisplayed()
    }

    @Test
    fun loadedShowsEveryRestaurantAndTheCount() {
        show(loaded())

        composeRule.onNodeWithText("Cape Town Grill").assertIsDisplayed()
        composeRule.onNodeWithText("Sushi on Sea Point").assertIsDisplayed()
        composeRule.onNodeWithText(
            context.resources.getQuantityString(R.plurals.discovery_count, 2, 2),
        ).assertIsDisplayed()
    }

    @Test
    fun errorShowsTheMessageAndRetryAsksForARefresh() {
        val events = show(DiscoveryUiState(content = DiscoveryContent.Error(ErrorKind.Network)))

        composeRule.onNodeWithText(text(R.string.discovery_error_network)).assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.discovery_retry)).performClick()

        assertEquals(1, events.refreshes)
    }

    @Test
    fun timeoutHasItsOwnMessage() {
        show(DiscoveryUiState(content = DiscoveryContent.Error(ErrorKind.Timeout)))

        composeRule.onNodeWithText(text(R.string.discovery_error_timeout)).assertIsDisplayed()
    }

    @Test
    fun emptyResultsForASearchSuggestChangingIt() {
        show(DiscoveryUiState(query = "zzz", content = DiscoveryContent.Empty(filtersActive = true)))

        composeRule.onNodeWithText(text(R.string.discovery_empty_filtered_message)).assertIsDisplayed()
    }

    @Test
    fun anEmptyFeedSaysSoInsteadOfBlamingTheSearch() {
        show(DiscoveryUiState(content = DiscoveryContent.Empty(filtersActive = false)))

        composeRule.onNodeWithText(text(R.string.discovery_empty_feed_message)).assertIsDisplayed()
    }

    @Test
    fun offlineBannerShowsTheAgeOfTheCachedDataAndRetryRefreshes() {
        val events = show(loaded(offline = OfflineNotice(minutesSinceSync = 5)))

        composeRule.onNodeWithText(text(R.string.discovery_offline_message_minutes, 5)).assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.discovery_retry)).performClick()

        assertEquals(1, events.refreshes)
    }

    @Test
    fun cachedDataStaysVisibleWhileTheBannerIsShown() {
        show(loaded(offline = OfflineNotice(minutesSinceSync = 0)))

        composeRule.onNodeWithText(text(R.string.discovery_offline_message_now)).assertIsDisplayed()
        composeRule.onNodeWithText("Cape Town Grill").assertIsDisplayed()
    }

    @Test
    fun searchFieldShowsTheCurrentQuery() {
        show(DiscoveryUiState(query = "sushi", content = DiscoveryContent.Loading))

        composeRule.onNode(hasSetTextAction()).assertTextContains("sushi")
    }

    @Test
    fun typingReportsTheText() {
        val events = show(DiscoveryUiState(content = DiscoveryContent.Loading))

        composeRule.onNode(hasSetTextAction()).performTextInput("sushi")

        assertEquals(listOf("sushi"), events.queries)
    }

    @Test
    fun openNowChipReflectsAndReportsTheToggle() {
        val events = show(DiscoveryUiState(openNowOnly = true, content = DiscoveryContent.Loading))
        val chip = composeRule.onNodeWithText(text(R.string.discovery_filter_open_now))

        chip.assertIsSelected()
        chip.performClick()

        assertEquals(1, events.openNowToggles)
    }

    @Test
    fun tappingACardReportsItsId() {
        val events = show(loaded())

        composeRule.onNodeWithText("Cape Town Grill").performClick()

        assertEquals(listOf("r-1001"), events.clicks)
    }

    @Test
    fun theHeartAsksForTheOppositeOfTheCurrentState() {
        val events = show(loaded())

        composeRule.onNodeWithContentDescription(text(R.string.restaurant_favorite_add)).performClick()
        composeRule.onNodeWithContentDescription(text(R.string.restaurant_favorite_remove)).performClick()

        assertEquals(listOf("r-1001" to true, "r-1002" to false), events.favorites)
    }
}
