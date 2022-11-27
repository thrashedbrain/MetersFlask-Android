package com.white.meters.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.white.meters.data.base.DetectRepository
import com.white.meters.data.base.InitRepository
import com.white.meters.data.base.MeterRepository
import com.white.meters.data.repository.DetectRepositoryImpl
import com.white.meters.data.repository.InitRepositoryImpl
import com.white.meters.data.repository.MeterRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface AppModule {

    @Binds
    @Singleton
    fun provideDetectRepository(impl: DetectRepositoryImpl): DetectRepository

    @Binds
    @Singleton
    fun provideMeterRepository(impl: MeterRepositoryImpl): MeterRepository

    @Binds
    @Singleton
    fun provideInitRepository(impl: InitRepositoryImpl): InitRepository

    companion object {
        @Provides
        @Singleton
        @HistorySharedPreferences
        fun historySharedPreferences(application: Application): SharedPreferences {
            return application.getSharedPreferences("auth_preferences", Context.MODE_PRIVATE)
        }
    }
}