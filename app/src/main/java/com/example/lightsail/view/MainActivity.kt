package com.example.lightsail.view

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.work.WorkInfo
import com.example.lightsail.R
import com.example.lightsail.databinding.ActivityMainBinding
import com.example.lightsail.viewModel.LightSailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<LightSailViewModel>()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        // Once the second task (identified by tag) status changes,
        // 1. update View accordingly showing/hiding View elements
        // 2. update Notifications accordingly showing/hiding
        viewModel.outputWorkInfos.observe(this) { it ->
            if (it.isNullOrEmpty()) {
                return@observe
            }

            val workInfo = it[0]

            if (workInfo.state.isFinished) {
                showWorkFinished()
            } else {
                showWorkInProgress()
            }
        }
        createChannel(LIGHT_SAIL_SERVICE_CHANNEL_ID, LIGHT_SAIL_NOTIFICATION_CHANNEL_NAME)


    }

    private fun createChannel(
        channelId: String,
        channelName: String
    ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(channelId, channelName, importance)
            mChannel.description = "LightSail Notification Channel"
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }


    }

    private fun showWorkInProgress() {
        viewModel.isServiceRunning.value = true
        viewModel.isCheckBoxEnabled.value = true
        // Restart automatic running service enable checkbox
        binding.enableServiceCheckBox.isChecked = true
        binding.progressbar.visibility = View.VISIBLE
        binding.statusText.text = "Enable checkbox to start service"
        showNotification()

    }

    private fun showNotification() {
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(

                    this, 0, notificationIntent,

                    PendingIntent.FLAG_IMMUTABLE
                )
            }

        val notification: Notification =
            NotificationCompat.Builder(this, LIGHT_SAIL_SERVICE_CHANNEL_ID)
                .setContentTitle(LIGHT_SAIL_FOREGROUND_NOTIFICATION_TITLE)
                .setContentText(LIGHT_SAIL_FOREGROUND_INTENT_SERVICE_TEXT)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setTicker(LIGHT_SAIL_FOREGROUND_NOTIFICATION_TITLE)
                .setChannelId(LIGHT_SAIL_SERVICE_CHANNEL_ID)
                .build()

        with(NotificationManagerCompat.from(this)) {
            notify(LIGHT_SAIL_NOTIFICATION_ID, notification)
        }

    }

    private fun showWorkFinished() {
        viewModel.isServiceRunning.value = false
        viewModel.isCheckBoxEnabled.value = false
        binding.enableServiceCheckBox.isChecked = false
        binding.progressbar.visibility = View.GONE
        binding.statusText.text = "Service Running"
        closeNotification()

    }

    private fun closeNotification() {

        with(NotificationManagerCompat.from(this)) {
            cancel(LIGHT_SAIL_NOTIFICATION_ID)
        }
    }

    companion object {

        const val LIGHT_SAIL_SERVICE_CHANNEL_ID = "LIGHT SAIL CHANNEL_ID_42"
        const val LIGHT_SAIL_FOREGROUND_NOTIFICATION_TITLE = "LightSail Foreground Services"

        const val LIGHT_SAIL_FOREGROUND_INTENT_SERVICE_TEXT = "My Foreground Intent Service"
        const val LIGHT_SAIL_NOTIFICATION_CHANNEL_NAME = "My Service Channel"

        const val LIGHT_SAIL_NOTIFICATION_ID = 42


    }

}