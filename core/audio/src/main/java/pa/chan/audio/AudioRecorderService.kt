package pa.chan.audio

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat

class AudioRecorderService : Service() {
    private var audioRecorder: AudioRecorder? = null


    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }



    override fun onCreate() {
        super.onCreate()
        val importance = NotificationManager.IMPORTANCE_LOW
        val channelId = "AudioRecordNotifyID"
        val channelName = "AudioRecordNotify"


        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        audioRecorder = AudioRecorder()

        audioRecorder?.startRecording()

        return START_NOT_STICKY

    }
}