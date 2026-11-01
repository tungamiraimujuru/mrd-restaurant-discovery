package com.mrd.discovery.core.common.di

import com.mrd.discovery.core.common.dispatch.DefaultDispatcher
import com.mrd.discovery.core.common.dispatch.IoDispatcher
import com.mrd.discovery.core.common.logging.AndroidAppLogger
import com.mrd.discovery.core.common.logging.AppLogger
import com.mrd.discovery.core.common.time.Clock
import com.mrd.discovery.core.common.time.DeviceClock
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
internal abstract class CommonBindingsModule {
    @Binds
    abstract fun bindLogger(impl: AndroidAppLogger): AppLogger

    @Binds
    abstract fun bindClock(impl: DeviceClock): Clock
}

@Module
@InstallIn(SingletonComponent::class)
internal object DispatchersModule {
    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}
