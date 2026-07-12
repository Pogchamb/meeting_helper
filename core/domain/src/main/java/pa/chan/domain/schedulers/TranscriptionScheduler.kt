package pa.chan.domain.schedulers

interface TranscriptionScheduler {

    suspend fun scheduleTranscription(sessionId: Long)

}