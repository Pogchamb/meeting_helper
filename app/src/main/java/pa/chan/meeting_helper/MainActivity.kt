package pa.chan.meeting_helper

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import pa.chan.ml.WhisperEngine

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        try {
            Log.d("WHISPER_TEST", "ИНИЦИАЛИЗИРУЕМ WhisperEngine")
            val engine = WhisperEngine()

            engine.initModel(
                assetManager = this.assets,
                modelPath = "models/ggml-small-q5_1.bin",
                cpuCores = Runtime.getRuntime().availableProcessors()
            )
            Log.d("Whisper_TEST", "МОДЕЛЬ УСПЕШНО ЗАГРУЖЕНА БЕЗ РАСПАКОВКИ НА ДИСК!")
            engine.freeModel()
            Log.d("Whisper_TEST", "Память очишена")
        } catch (e: Exception) {
            Log.e("WHISPER_TEST", "Error: ${e.message}")
        }

    }
}