package pa.chan.domain.contracts

interface TranscriptionScheduler {

    fun scheduleTranscription(sessionId: Long)

}