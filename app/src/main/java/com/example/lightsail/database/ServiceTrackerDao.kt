package com.example.lightsail.database

import androidx.room.*
import com.example.lightsail.database.DbConstants.LIGHT_SAIL_ID

@Dao
interface ServiceTrackerDao {

    @Query("SELECT enabled FROM ServiceTracker WHERE service_id = '$LIGHT_SAIL_ID'")
    fun isLightSailEnabled(): Boolean?

    @Update
    fun updateService(service: ServiceTracker)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertService(service: ServiceTracker): Long

    @Transaction
    fun upsertService(service: ServiceTracker) {
        val id = insertService(service)
        if (id == -1L) {
            updateService(service)
        }
    }

}