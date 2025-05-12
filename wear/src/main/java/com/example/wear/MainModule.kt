package com.example.wear

import android.content.Context
import com.example.wear.data.HealthTrackingServiceConnection
import com.example.wear.data.TrackingRepository
import com.example.wear.data.TrackingRepositoryImpl
import com.example.wear.domain.StopTrackingUseCase
import com.example.wear.domain.TrackHeartRateUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MainModule {

    @Binds
    @Singleton
    abstract fun bindTrackingRepository(
        impl: TrackingRepositoryImpl
    ): TrackingRepository

    companion object {
        @Provides
        @Singleton
        fun provideApplicationScope(): CoroutineScope =
            CoroutineScope(SupervisorJob() + Dispatchers.IO)

        @OptIn(ExperimentalCoroutinesApi::class)
        @Provides
        @Singleton
        fun provideHealthTrackingServiceConnection(
            @ApplicationContext context: Context,
            scope: CoroutineScope
        ): HealthTrackingServiceConnection {
            return HealthTrackingServiceConnection(context, scope)
        }

        @Provides
        @Singleton
        fun provideTrackHeartRateUseCase(
            repository: TrackingRepository
        ): TrackHeartRateUseCase {
            return TrackHeartRateUseCase(repository)
        }

        @Provides
        @Singleton
        fun provideStopTrackingUseCase(
            repository: TrackingRepository
        ): StopTrackingUseCase {
            return StopTrackingUseCase(repository)
        }
    }
}
