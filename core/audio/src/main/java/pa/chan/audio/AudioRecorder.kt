package pa.chan.audio

import android.Manifest
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

internal class AudioRecorder(private val context: Context) {

    private var recordingJob: Job? = null
    private var audioRecord: AudioRecord? = null
    private var audioRecordOutputStream: OutputStream? = null

    private var recordFile: String? = null

    companion object {
        const val SAMPLE_RATE = 16000
        const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun startRecording() {
        if (audioRecord != null) return

        val minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
        recordFile = File(context.filesDir, "record_${System.currentTimeMillis()}.pcm").absolutePath
        audioRecordOutputStream = FileOutputStream(recordFile)

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            minBufferSize
        )

        audioRecord?.startRecording()

        recordingJob = CoroutineScope(Dispatchers.IO).launch {
            writeAudioDataToDisk()
        }
    }

    suspend fun writeAudioDataToDisk() {
        while (currentCoroutineContext().isActive) {
            val audioData = ByteArray(SAMPLE_RATE * 2)
            audioRecord?.read(audioData, 0, audioData.size)
            audioRecordOutputStream?.write(audioData)
        }
    }

    fun readAudioChunk(seconds: Int): FloatArray {
        val samplesToRead = SAMPLE_RATE * seconds

        val audioData = ByteArray(samplesToRead * 2)

        audioRecord?.read(audioData, 0, audioData.size)

        val floatBuffer = FloatArray(samplesToRead)

        for (i in 0 until samplesToRead) {
            val shortVal =
                (audioData[i + 1].toInt() and 0xFF shl 8) or (audioData[i].toInt() and 0xFF)

            floatBuffer[i] = shortVal.toFloat() / 32768.0f
        }

        return floatBuffer

    }

    fun stopRecording(): String? {
        if (audioRecord == null) return null

        recordingJob?.cancel()
        recordingJob = null

        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null

        audioRecordOutputStream?.close()

        return recordFile
    }


}