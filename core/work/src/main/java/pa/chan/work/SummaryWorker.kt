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
import pa.chan.domain.exceptions.SummaryGenerationException
import pa.chan.domain.repository.RecordRepository
import pa.chan.domain.useCases.GenerateSummaryUseCase

@HiltWorker
class SummaryWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val summaryUseCase: GenerateSummaryUseCase,
    private val recordRepository: RecordRepository
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        setForeground(getForegroundInfo())

        val sessionId = inputData.getLong("SESSION_ID", -1L)

        if (sessionId == -1L) return Result.failure()

        val recordSessionModel = recordRepository.getSessionById(sessionId)

        val transcribedText = recordSessionModel?.text

        if (transcribedText.isNullOrEmpty()) return Result.failure()

        try {
            val summary = summaryUseCase(transcribedText).summaryText
            recordRepository.updateSummary(sessionId, summary)
            return Result.success()
        } catch (e: SummaryGenerationException) {
            Log.e("Error", e.message.toString())
            return Result.retry()
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notification = NotificationCompat.Builder(applicationContext, "summary_channel")
            .setSmallIcon(android.R.drawable.edit_text)
            .setContentTitle("Обработка текста")
            .setContentText("Подготавливаем Summary")
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
}