package pa.chan.audio

import android.Manifest
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission

internal class AudioRecorder {
    private var audioRecord: AudioRecord? = null

    companion object {
        const val SAMPLE_RATE = 16000
        const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun startRecording() {
        if (audioRecord != null) return

        val minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            minBufferSize
        )

        audioRecord?.startRecording()
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

    fun stopRecording() {
        if (audioRecord == null) return

        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }


}