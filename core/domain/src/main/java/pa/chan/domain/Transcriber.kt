package pa.chan.domain

interface Transcriber {

    suspend fun transcribe(audioChunk: FloatArray) : String

}