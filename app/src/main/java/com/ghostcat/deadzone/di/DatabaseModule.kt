package com.ghostcat.deadzone.di

import android.content.Context
import androidx.room.Room
import com.ghostcat.deadzone.database.AppDatabase
import com.ghostcat.deadzone.database.TestReportDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "test_results"
        )
            //Remove this line in production
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideTestResultsDao(database: AppDatabase): TestReportDAO {
        return database.testReportDao()
    }
}