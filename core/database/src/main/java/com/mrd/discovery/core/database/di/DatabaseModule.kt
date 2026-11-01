package com.mrd.discovery.core.database.di

import android.content.Context
import androidx.room.Room
import com.mrd.discovery.core.database.MrDDatabase
import com.mrd.discovery.core.database.RestaurantLocalDataSource
import com.mrd.discovery.core.database.RoomRestaurantLocalDataSource
import com.mrd.discovery.core.database.dao.RestaurantDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DatabaseBindingsModule {
    @Binds
    abstract fun bindLocalDataSource(impl: RoomRestaurantLocalDataSource): RestaurantLocalDataSource
}

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MrDDatabase =
        Room.databaseBuilder(context, MrDDatabase::class.java, "mrd_restaurants.db").build()

    @Provides
    fun provideRestaurantDao(database: MrDDatabase): RestaurantDao = database.restaurantDao()
}
