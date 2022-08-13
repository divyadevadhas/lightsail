package com.example.lightsail.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.lightsail.database.CounterDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CounterIncrementWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val counterDao: CounterDao
) : Worker(context, params) {
    override fun doWork(): Result {
        // Loop until task is complete or stopped/cancel or fails
        while (true) {
            try {
                // Respond to WorkManager request to cancel task - cancelUniqueWork
                if (isStopped) {
                    return Result.failure()
                }
                val lightSailCounter = counterDao.selectLightSailCounter()
                if (lightSailCounter.currentValue < lightSailCounter.maxValue) {
                    // Increment counter
                    lightSailCounter.currentValue = lightSailCounter.currentValue + 1
                    counterDao.updateCounter(lightSailCounter)
                    // On reaching max desired value return success
                    if (lightSailCounter.currentValue == lightSailCounter.maxValue) {
                        return Result.success()
                    }
                }
                sleepFiveSec()
            } catch (e: Throwable) {
                // Database interaction fails, then this job fails
                return Result.failure()
            }
        }
    }

    private fun sleepFiveSec() {
        try {
            Thread.sleep(1_000 * 2, 0)
        } catch (e: Throwable) {

        }
    }
}