package pa.chan.ml

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import pa.chan.domain.Transcriber
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WhisperEngine @Inject constructor(@ApplicationContext context: Context): Transcriber {
    private var contextPtr: AtomicLong = AtomicLong(0L)

    private val assetManager = context.assets
    private val mutex = Mutex()

    companion object {
        init {
            System.loadLibrary("whisper_native")
        }
    }

    fun isInitialized(): Boolean = contextPtr.get() != 0L

    fun initModel(assetManager: AssetManager, modelPath: String, cpuCores: Int) {
        if (isInitialized()) return

        try {
            val files = assetManager.list("")
            Log.d("WhisperEngine", "Файлы в assets: ${files?.joinToString()}")
            val inputStream = assetManager.open(modelPath)
            Log.d("WhisperEngine", "Kotlin видит файл! Размер: ${inputStream.available()} байт")
            inputStream.close()
        } catch (e: Exception) {
            Log.e("WhisperEngine", "Kotlin НЕ видит файл!", e)
        }

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

    override suspend fun transcribe(audioChunk: FloatArray): String {
        val cores = Runtime.getRuntime().availableProcessors().coerceAtMost(6)
        val modelPath = "models/ggml-small-q5_1.bin"

        mutex.withLock {
            if (!isInitialized()) {
                initModel(assetManager, modelPath, cores)
            }

            return withContext(Dispatchers.Default) {
                transcribeChunk(audioChunk, cores)
            }
        }
    }

    protected fun finalize() {
        freeModel()
    }

    private external fun nativeInitModel(assetManager: AssetManager, modelPath: String, cpuCores: Int): Long
    private external fun nativeTranscribe(ctxPtr: Long, audioData: FloatArray, cpuCores: Int): String
    private external fun nativeFreeModel(ctxPtr: Long)

}