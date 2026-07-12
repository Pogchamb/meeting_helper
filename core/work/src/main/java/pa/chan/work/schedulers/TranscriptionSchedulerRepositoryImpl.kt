package pa.chan.work.schedulers

import androidx.work.BackoffPolicy
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.workDataOf
import pa.chan.domain.repository.RecordRepository
import pa.chan.domain.schedulers.TranscriptionScheduler
import pa.chan.work.TranscriptionWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TranscriptionSchedulerImpl @Inject constructor(
    private val recordRepository: RecordRepository,
    private val workManager: WorkManager
) :
    TranscriptionScheduler {
    override suspend fun scheduleTranscription(sessionId: Long) {

        val data: Data = workDataOf("SESSION_ID" to sessionId)

        val transcriptionWorkRequest =
            OneTimeWorkRequest.Builder(TranscriptionWorker::class.java)
                .setInputData(data)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
                .build()

        workManager.enqueueUniqueWork(
            "TRANSCRIPTION_$sessionId",
            ExistingWorkPolicy.APPEND,
            transcriptionWorkRequest
        )
    }
}