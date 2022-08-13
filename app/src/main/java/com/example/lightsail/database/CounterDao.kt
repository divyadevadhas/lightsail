package com.example.lightsail.database

import androidx.room.*
import com.example.lightsail.database.DbConstants.LIGHT_SAIL_ID

@Dao
interface CounterDao {

    @Query("SELECT * FROM counter WHERE counter_id = '$LIGHT_SAIL_ID'")
    fun selectLightSailCounter(): Counter

    @Update
    fun updateCounter(counter: Counter)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCounter(counter: Counter): Long

    @Transaction
    fun upsertCounter(counter: Counter) {
        // Try inserting first
        val id = insertCounter(counter)
        // If insert fails (indicated by -1 return value), then update
        if (id == -1L) {
            updateCounter(counter)
        }
    }

}