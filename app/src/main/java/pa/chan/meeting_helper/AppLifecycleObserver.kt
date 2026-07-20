package pa.chan.meeting_helper

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import pa.chan.ml.WhisperEngine
import javax.inject.Inject

class AppLifecycleObserver @Inject constructor(private val whisperEngine: WhisperEngine): DefaultLifecycleObserver {

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        whisperEngine.freeModel()
    }
}