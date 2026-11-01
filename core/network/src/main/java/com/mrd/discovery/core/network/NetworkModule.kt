package com.mrd.discovery.core.network

import com.mrd.discovery.core.network.fake.AssetRestaurantPayloadSource
import com.mrd.discovery.core.network.fake.FakeBackendConfig
import com.mrd.discovery.core.network.fake.FakeBackendInterceptor
import com.mrd.discovery.core.network.fake.RestaurantPayloadSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@Module
@InstallIn(SingletonComponent::class)
internal abstract class NetworkBindingsModule {
    @Binds
    abstract fun bindPayloadSource(impl: AssetRestaurantPayloadSource): RestaurantPayloadSource

    @Binds
    abstract fun bindRemoteDataSource(impl: RetrofitRestaurantRemoteDataSource): RestaurantRemoteDataSource
}

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {
    // Never contacted: FakeBackendInterceptor answers every request. Swap this and drop the
    // interceptor to point the app at a real server.
    private const val BASE_URL = "https://api.mrd-discovery.example/"

    @Provides
    @Singleton
    fun provideJson(): Json = createNetworkJson()

    @Provides
    @Singleton
    fun provideOkHttpClient(source: RestaurantPayloadSource, config: FakeBackendConfig): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(FakeBackendInterceptor(source, config))
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, json: Json): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides
    @Singleton
    fun provideRestaurantApi(retrofit: Retrofit): RestaurantApi = retrofit.create(RestaurantApi::class.java)
}
