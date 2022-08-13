package com.example.lightsail.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ServiceTracker(
    @PrimaryKey @ColumnInfo(name = "service_id") val serviceId: String,
    val enabled: Boolean
)
