package pa.chan.domain.contracts

interface Transcriber {

    suspend fun transcribe(audioChunk: FloatArray) : String

}