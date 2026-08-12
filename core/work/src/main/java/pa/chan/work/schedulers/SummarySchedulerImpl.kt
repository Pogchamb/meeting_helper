package pa.chan.work.schedulers

import androidx.work.BackoffPolicy
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import pa.chan.domain.contracts.SummaryScheduler
import pa.chan.work.SummaryWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SummarySchedulerImpl @Inject constructor(
    private val workManager: WorkManager
) : SummaryScheduler {
    override fun scheduleSummary(sessionId: Long) {
        val data: Data = workDataOf("SESSION_ID" to sessionId)

        val constraints = androidx.work.Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val summaryWorkRequest = OneTimeWorkRequest.Builder(SummaryWorker::class.java)
            .setInputData(data)
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        workManager.enqueueUniqueWork(
            "SUMMARY_$sessionId",
            ExistingWorkPolicy.KEEP,
            summaryWorkRequest
        )
    }
}