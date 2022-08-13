package com.example.lightsail.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ServiceTracker::class, Counter::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun serviceDao(): ServiceTrackerDao
    abstract fun counterDao(): CounterDao
}