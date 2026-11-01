package com.mrd.discovery.core.data.di

import com.mrd.discovery.core.data.OfflineFirstRestaurantRepository
import com.mrd.discovery.core.domain.repository.RestaurantRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {
    @Binds
    abstract fun bindRestaurantRepository(impl: OfflineFirstRestaurantRepository): RestaurantRepository
}
