package pa.chan.meeting_helper

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.ProcessLifecycleOwner
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), androidx.work.Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var lifecycleObserver: AppLifecycleObserver

    override val workManagerConfiguration: androidx.work.Configuration
        get() = androidx.work.Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleObserver)

        val transcribeChannelId = "transcription_channel"
        val transcribeChannelName = "Transcription"
        val transcribeChannel = NotificationChannel(
            transcribeChannelId,
            transcribeChannelName,
            NotificationManager.IMPORTANCE_LOW
        )

        val summaryChannelId = "summary_channel"
        val summaryChannelName = "Summary"
        val summaryChannel = NotificationChannel(
            summaryChannelId,
            summaryChannelName,
            NotificationManager.IMPORTANCE_LOW
        )

        getSystemService(NotificationManager::class.java).createNotificationChannels(listOf(transcribeChannel, summaryChannel))
    }
}