package com.ghostcat.deadzone.di

import android.content.Context
import com.ghostcat.deadzone.services.ConnectivityChecker
import com.ghostcat.deadzone.services.GeoLocationService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object SensorModule {

    @Provides
    fun provideConnectivityChecker(@ApplicationContext context: Context): ConnectivityChecker {
        return ConnectivityChecker(context)
    }

    @Provides
    fun provideGeoLocationService(@ApplicationContext context: Context): GeoLocationService {
        return GeoLocationService(context)
    }
}