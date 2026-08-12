package pa.chan.audio

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import pa.chan.domain.repository.RecordRepository
import pa.chan.domain.contracts.TranscriptionScheduler
import javax.inject.Inject

@AndroidEntryPoint
class AudioRecorderService : Service() {
    @Inject
    lateinit var recordRepository: RecordRepository

    @Inject
    lateinit var transcriptionScheduler: TranscriptionScheduler

    private var audioRecorder: AudioRecorder? = null

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        const val ACTION_START = "START"
        const val ACTION_STOP = "STOP"
    }


    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }


    override fun onCreate() {
        super.onCreate()
        val importance = NotificationManager.IMPORTANCE_LOW
        val channelId = "AudioRecordNotifyID"
        val channelName = "AudioRecordNotify"
        audioRecorder = AudioRecorder(this)

        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel = NotificationChannel(channelId, channelName, importance)
        notificationChannel.description = "notify audio record"
        notificationChannel.setShowBadge(false)
        notificationManager.createNotificationChannel(notificationChannel)

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setContentTitle("Запись")
            .setContentText("Идет запись совещания!")
            .build()

        startForeground(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        audioRecorder?.stopRecording()

        serviceScope.cancel()

        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {


        when (intent?.action) {
            ACTION_START -> {
                audioRecorder?.startRecording()

            }

            ACTION_STOP -> {
                serviceScope.launch {
                    val path = audioRecorder?.stopRecording()
                    path?.let {
                        val id = recordRepository.savePendingRecord(it)
                        Log.d("AudioRecorderService", "Saved to DB with ID: $id")

                        transcriptionScheduler.scheduleTranscription(id)
                    }
                    stopSelf()
                }
            }
        }
        return START_NOT_STICKY
    }
}