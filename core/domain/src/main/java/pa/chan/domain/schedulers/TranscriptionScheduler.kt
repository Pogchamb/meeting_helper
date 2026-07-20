package pa.chan.domain.schedulers

interface TranscriptionScheduler {

    fun scheduleTranscription(sessionId: Long)

}