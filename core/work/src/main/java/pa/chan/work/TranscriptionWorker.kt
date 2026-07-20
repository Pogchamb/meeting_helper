package pa.chan.work

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import pa.chan.domain.AudioFileReader
import pa.chan.domain.Transcriber
import pa.chan.domain.enums.RecordSessionStatus
import pa.chan.domain.repository.RecordRepository


@HiltWorker
class TranscriptionWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val recordRepository: RecordRepository,
    private val transcriber: Transcriber,
    private val audioFileReader: AudioFileReader
) : CoroutineWorker(
    appContext, params
) {
    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notification = NotificationCompat.Builder(applicationContext, "transcription_channel")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setContentTitle("Обработка аудио")
            .setContentText("Распознаю текст...")
            .build()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                2,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(2, notification)
        }
    }

    override suspend fun doWork(): Result {
        val sessionId = inputData.getLong("SESSION_ID", -1L)
        Log.d("TranscriptionWorker", "START doWork for ID: $sessionId")

        try {
            setForeground(getForegroundInfo())
            Log.d("TranscriptionWorker", "Successfully set to Foreground")
        } catch (e: Exception) {
            Log.e("TranscriptionWorker", "Failed to set Foreground", e)
        }



        try {
            Log.d("TranscriptionWorker", "1. Getting session from DB...")
            val recordSessionModel = recordRepository.getSessionById(sessionId) ?: return Result.failure()

            Log.d("TranscriptionWorker", "2. Updating status to IN_PROGRESS...")
            recordRepository.updateStatus(sessionId, RecordSessionStatus.IN_PROGRESS)


            Log.d("TranscriptionWorker", "3. Reading PCM file...")
            val fullText = StringBuilder()
            audioFileReader.readPcmFileInChunks(recordSessionModel.pathToFile).collect { chunk ->
                Log.d("TranscriptionWorker", "Received chunk! Size: ${chunk.size}")
                Log.d("TranscriptionWorker", "4. Calling WhisperEngine.transcribe chunk...")
                val chunkText = transcriber.transcribe(chunk)
                fullText.append(chunkText).append(" ")
            }
            Log.d("TranscriptionWorker", "4.1 Transcription finished! Text length: ${fullText.length} Text: ${fullText.toString()}")

            Log.d("TranscriptionWorker", "5. Updating DB with text...")
            recordRepository.updateTextAndStatus(sessionId, RecordSessionStatus.COMPLETED, fullText.toString())

            Log.d("TranscriptionWorker", "SUCCESS")
            return Result.success()
        } catch (e: Exception) {
            Log.e("TranscriptionWorker", "FAILED", e)
            recordRepository.updateStatus(sessionId, RecordSessionStatus.ERROR)
            return Result.failure()
        }

    }
}