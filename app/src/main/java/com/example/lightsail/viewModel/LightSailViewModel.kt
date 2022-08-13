package com.example.lightsail.viewModel

import android.app.Application
import android.view.View
import android.widget.CheckBox
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.lightsail.database.DbConstants
import com.example.lightsail.database.ServiceTracker
import com.example.lightsail.database.ServiceTrackerDao
import com.example.lightsail.work.CounterIncrementWorker
import com.example.lightsail.work.InitializeCountWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LightSailViewModel @Inject constructor
    (
    private val serviceTrackerDao: ServiceTrackerDao,
    application: Application
) : ViewModel() {

    companion object {
        private val LIGHT_SAIL_UNIQUE_ID = "LIGHT_SAIL_UNIQUE_ID"
        private val LIGHT_SAIL_TAG_ID = "LIGHT_SAIL_TAG_ID"
    }

    val isCheckBoxEnabled = MutableLiveData<Boolean>()
    val isServiceRunning = MutableLiveData<Boolean>()

    private val workManager = WorkManager.getInstance(application)

    val outputWorkInfos: LiveData<MutableList<WorkInfo>> =
        workManager.getWorkInfosByTagLiveData(LIGHT_SAIL_TAG_ID)

    fun requestServiceStatusUpdate(checkbox: View) {
        val statusRequestToRunService: Boolean = (checkbox as CheckBox).isChecked

        viewModelScope.launch(Dispatchers.IO) {
            serviceTrackerDao.upsertService(
                ServiceTracker(
                    DbConstants.LIGHT_SAIL_ID,
                    statusRequestToRunService
                )
            )
        }

        if (statusRequestToRunService) {
            // Act on request to start service from View
            // WorkManager used to create two chained tasks
            // Task is Unique, second task has a tag
            var continuation = workManager.beginUniqueWork(
                LIGHT_SAIL_UNIQUE_ID,
                ExistingWorkPolicy.KEEP,
                OneTimeWorkRequest.from(InitializeCountWorker::class.java)
            )

            val incrementWorker = OneTimeWorkRequest.Builder(CounterIncrementWorker::class.java)
                .addTag(LIGHT_SAIL_TAG_ID)
                .build()

            continuation = continuation.then(incrementWorker)

            continuation.enqueue()
        } else {
            // Act on request to stop service from view
            // Stop is requested by workManager to worker and is not immediate
            workManager.cancelUniqueWork(LIGHT_SAIL_UNIQUE_ID)
        }


    }

}