package com.example.lightsail.dI

import android.content.Context
import androidx.room.Room
import com.example.lightsail.database.AppDatabase
import com.example.lightsail.database.CounterDao
import com.example.lightsail.database.ServiceTrackerDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    fun provideCounterDao(database: AppDatabase): CounterDao {
        return database.counterDao()
    }

    @Provides
    fun provideServiceTrackerDao(database: AppDatabase): ServiceTrackerDao {
        return database.serviceDao()
    }

    @Provides
    fun provideAppDatabase(@ApplicationContext applicationContext: Context): AppDatabase {
        // Create database
        return Room.databaseBuilder(
            applicationContext, AppDatabase::class.java, "lightsail.db"
        ).build()
    }
}