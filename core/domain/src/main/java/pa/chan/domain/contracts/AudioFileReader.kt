package pa.chan.domain.contracts

import kotlinx.coroutines.flow.Flow
import pa.chan.domain.configs.AudioConfig

interface AudioFileReader {

    suspend fun readPcmFileInChunks(filePath: String, chunkDurationSeconds: Int = AudioConfig.CHUNK_DURATION_SECONDS): Flow<FloatArray>

}