package com.example.lightsail.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.lightsail.database.Counter
import com.example.lightsail.database.CounterDao
import com.example.lightsail.database.DbConstants.LIGHT_SAIL_ID
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class InitializeCountWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted val params: WorkerParameters,
    private val counterDao: CounterDao
) : Worker(context, params) {
    override fun doWork(): Result {
        val currentValue = 0
        val maxValue = 50
        try {
            // Initialize the work with a zero counter start value and desired max value
            counterDao.upsertCounter(Counter(LIGHT_SAIL_ID, currentValue, maxValue))
        } catch (e: Throwable) {
            // Database interaction fails then this task fails
            return Result.failure()
        }
        return Result.success()
    }

}