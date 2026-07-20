package pa.chan.work.schedulers

import androidx.work.BackoffPolicy
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import pa.chan.domain.schedulers.TranscriptionScheduler
import pa.chan.work.TranscriptionWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TranscriptionSchedulerImpl @Inject constructor(
    private val workManager: WorkManager
) :
    TranscriptionScheduler {
    override fun scheduleTranscription(sessionId: Long) {

        val data: Data = workDataOf("SESSION_ID" to sessionId)

        val transcriptionWorkRequest =
            OneTimeWorkRequest.Builder(TranscriptionWorker::class.java)
                .setInputData(data)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()

        workManager.enqueueUniqueWork(
            "TRANSCRIPTION_$sessionId",
            ExistingWorkPolicy.KEEP,
            transcriptionWorkRequest
        )
    }
}