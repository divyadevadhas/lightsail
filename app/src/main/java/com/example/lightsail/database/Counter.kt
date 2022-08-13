package com.example.lightsail.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Counter(
    @PrimaryKey @ColumnInfo(name = "counter_id") val counterId: String,
    var currentValue: Int = 0,
    var maxValue: Int = 5
)
