package com.mrd.discovery.core.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoomRestaurantLocalDataSourceTest {
    private lateinit var database: MrDDatabase
    private lateinit var source: RoomRestaurantLocalDataSource

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, MrDDatabase::class.java).build()
        source = RoomRestaurantLocalDataSource(database.restaurantDao())
    }

    @After
    fun tearDown() = database.close()

    private fun restaurant(id: String, name: String, rating: Double? = 4.0, isOpen: Boolean = true) = CachedRestaurant(
        id = id,
        name = name,
        cuisines = listOf("Test"),
        rating = rating,
        deliveryFeeCents = 1500,
        etaMinutes = 30,
        isOpen = isOpen,
        imageUrl = null,
    )

    private fun fill(vararg restaurants: CachedRestaurant, syncedAt: Long = 1L) =
        runBlocking { source.replaceAll(restaurants.toList(), syncedAt) }

    private fun names(query: String = "", openOnly: Boolean = false) =
        runBlocking { source.observeRestaurants(query, openOnly).first() }.map { it.name }

    @Test
    fun searchIsCaseInsensitiveAndMatchesAnywhereInTheName() {
        fill(restaurant("1", "Sushi on Sea Point"), restaurant("2", "Cape Town Grill"))

        assertEquals(listOf("Sushi on Sea Point"), names("SUSHI"))
        assertEquals(listOf("Cape Town Grill"), names("town"))
    }

    @Test
    fun searchHandlesNonAsciiLetters() {
        fill(restaurant("1", "Café Noir"), restaurant("2", "Cape Town Grill"))

        assertEquals(listOf("Café Noir"), names("CAFÉ"))
    }

    @Test
    fun likeWildcardsInTheQueryAreTreatedAsLiteralText() {
        fill(restaurant("1", "50% Off Pizza"), restaurant("2", "Pizza Place"))

        assertEquals(listOf("50% Off Pizza"), names("50%"))
        assertEquals(listOf("50% Off Pizza"), names("%"))
        assertTrue(names("_").isEmpty())
    }

    @Test
    fun openNowFilterExcludesClosedRestaurants() {
        fill(restaurant("1", "Open Place", isOpen = true), restaurant("2", "Closed Place", isOpen = false))

        assertEquals(listOf("Open Place"), names(openOnly = true))
        assertEquals(2, names(openOnly = false).size)
    }

    @Test
    fun resultsAreOrderedByRatingWithUnratedLast() {
        fill(
            restaurant("1", "Alpha", rating = 4.5),
            restaurant("2", "Bravo", rating = null),
            restaurant("3", "Charlie", rating = 4.9),
            restaurant("4", "Delta", rating = 4.5),
        )

        assertEquals(listOf("Charlie", "Alpha", "Delta", "Bravo"), names())
    }

    @Test
    fun favoritesSurviveRefreshesEvenWhenARestaurantTemporarilyLeavesTheFeed() {
        fill(restaurant("1", "Favourite Place"), restaurant("2", "Other"))
        runBlocking { source.setFavorite("1", true) }

        fill(restaurant("2", "Other"))
        assertEquals(listOf("Other"), names())

        fill(restaurant("1", "Favourite Place"), restaurant("2", "Other"))
        val favorite = runBlocking { source.observeRestaurant("1").first() }
        assertEquals(true, favorite?.isFavorite)
    }

    @Test
    fun unfavoritingClearsTheFlag() {
        fill(restaurant("1", "Place"))
        runBlocking { source.setFavorite("1", true) }
        runBlocking { source.setFavorite("1", false) }

        assertEquals(false, runBlocking { source.observeRestaurant("1").first() }?.isFavorite)
    }

    @Test
    fun replaceAllDropsRestaurantsNoLongerInTheFeed() {
        fill(restaurant("1", "Old"), restaurant("2", "Stays"))
        fill(restaurant("2", "Stays"), restaurant("3", "New"))

        assertEquals(listOf("Stays", "New").sorted(), names().sorted())
    }

    @Test
    fun lastSyncedAtIsNullUntilTheFirstFill() {
        assertNull(runBlocking { source.observeLastSyncedAt().first() })

        fill(restaurant("1", "Place"), syncedAt = 123L)

        assertEquals(123L, runBlocking { source.observeLastSyncedAt().first() })
    }

    @Test
    fun unknownRestaurantEmitsNull() {
        assertNull(runBlocking { source.observeRestaurant("missing").first() })
    }
}
