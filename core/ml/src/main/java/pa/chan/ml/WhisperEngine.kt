package pa.chan.ml

import android.content.res.AssetManager
import java.util.concurrent.atomic.AtomicLong

class WhisperEngine {
    private var contextPtr: AtomicLong = AtomicLong(0L)

    companion object {
        init {
            System.loadLibrary("whisper_native")
        }
    }

    fun isInitialized(): Boolean = contextPtr.get() != 0L

    fun initModel(assetManager: AssetManager, modelPath: String, cpuCores: Int) {
        if (isInitialized()) return

        val ptr = nativeInitModel(assetManager, modelPath, cpuCores)

        if (ptr == 0L) {
            throw RuntimeException("C++ не смог загрузить модель")
        }

        contextPtr.set(ptr)
    }

    fun transcribeChunk(audioChunk: FloatArray, cpuCores: Int): String {
        val ptr = contextPtr.get()

        if (ptr == 0L) throw IllegalStateException("Сначала вызови initModel")

        return nativeTranscribe(ptr, audioChunk, cpuCores)
    }

    fun freeModel() {
        val ptr = contextPtr.getAndSet(0L)
        if (ptr != 0L) {
            nativeFreeModel(ptr)
        }
    }


    private external fun nativeInitModel(assetManager: AssetManager, modelPath: String, cpuCores: Int): Long
    private external fun nativeTranscribe(ctxPtr: Long, audioData: FloatArray, cpuCores: Int): String
    private external fun nativeFreeModel(ctxPtr: Long)

}