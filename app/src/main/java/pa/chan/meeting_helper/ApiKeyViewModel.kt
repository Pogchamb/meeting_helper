package pa.chan.meeting_helper

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import pa.chan.domain.useCases.SaveApiKeyUseCase
import javax.inject.Inject

@HiltViewModel
class ApiKeyViewModel @Inject constructor(
    private val saveApiKeyUseCase: SaveApiKeyUseCase
) : ViewModel() {

    suspend fun saveApiKey(key: String) {

    }

}