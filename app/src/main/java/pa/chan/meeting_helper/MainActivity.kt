package pa.chan.meeting_helper

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import pa.chan.audio.AudioRecorderService

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

        val btnStartStop = findViewById<MaterialButton>(R.id.btn_start_stop)
        var currentState = RecordState.STOP_RECORD

        val launcher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    if (currentState == RecordState.STOP_RECORD) {
                        currentState = RecordState.RECORD
                        btnStartStop.text = getString(R.string.stop)

                        val intent = Intent(this, AudioRecorderService::class.java)
                        startForegroundService(intent)
                    } else if (currentState == RecordState.RECORD) {
                        currentState = RecordState.STOP_RECORD
                        btnStartStop.text = getString(R.string.start)

                        val intent = Intent(this, AudioRecorderService::class.java)
                        stopService(intent)
                    }
                } else {
                    Toast.makeText(this, R.string.audio_permission_toast, Toast.LENGTH_SHORT).show()
                }
            }

        btnStartStop.setOnClickListener {
            launcher.launch(Manifest.permission.RECORD_AUDIO)
        }



//        try {
//            Log.d("WHISPER_TEST", "ИНИЦИАЛИЗИРУЕМ WhisperEngine")
//            val engine = WhisperEngine()
//
//            engine.initModel(
//                assetManager = this.assets,
//                modelPath = "models/ggml-small-q5_1.bin",
//                cpuCores = Runtime.getRuntime().availableProcessors()
//            )
//            Log.d("Whisper_TEST", "МОДЕЛЬ УСПЕШНО ЗАГРУЖЕНА БЕЗ РАСПАКОВКИ НА ДИСК!")
//            engine.freeModel()
//            Log.d("Whisper_TEST", "Память очишена")
//        } catch (e: Exception) {
//            Log.e("WHISPER_TEST", "Error: ${e.message}")
//        }

    }
}