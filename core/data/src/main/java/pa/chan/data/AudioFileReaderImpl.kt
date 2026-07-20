package pa.chan.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import pa.chan.domain.AudioFileReader
import pa.chan.domain.configs.AudioConfig.SAMPLE_RATE
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject

class AudioFileReaderImpl @Inject constructor() : AudioFileReader {

    override suspend fun readPcmFileInChunks(
        filePath: String,
        chunkDurationSeconds: Int
    ): Flow<FloatArray> = flow {
        val file = File(filePath)
        if (!file.exists()) return@flow
        Log.d(
            "AudioFileReader",
            "File path: $filePath. Exists: ${file.exists()}. Size: ${file.length()}"
        )

        val chunkSizeSamples = SAMPLE_RATE * chunkDurationSeconds
        val chunkSizeBytes = chunkSizeSamples * 2

        FileInputStream(file).use { fis ->
            DataInputStream(fis).use { dis ->
                while (true) {
                    val bytesAvailable = fis.available()
                    if (bytesAvailable == 0) break

                    val bytesToRead = minOf(chunkSizeBytes, bytesAvailable)
                    val byteBuffer = ByteArray(bytesToRead)
                    dis.readFully(byteBuffer)

                    val wrapperBuffer = ByteBuffer.wrap(byteBuffer)
                    wrapperBuffer.order(ByteOrder.LITTLE_ENDIAN)
                    val shortBuffer = wrapperBuffer.asShortBuffer()

                    val shortArray = ShortArray(shortBuffer.limit())
                    shortBuffer.get(shortArray)

                    val floatBuffer = FloatArray(shortArray.size) { index ->
                        (shortArray[index] / 32767.0f).coerceIn(-1f..1f)
                    }
                    emit(floatBuffer)

                }
            }

        }

    }.flowOn(Dispatchers.IO)

}