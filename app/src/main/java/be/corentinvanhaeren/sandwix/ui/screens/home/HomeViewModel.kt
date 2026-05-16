package be.corentinvanhaeren.sandwix.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.corentinvanhaeren.sandwix.network.SandwixApi
import be.corentinvanhaeren.sandwix.network.SandwixApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class HomeViewModel(
    private val apiService: SandwixApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        getBroodjes()
    }

    fun onQueryUpdate(query: String) {
        _uiState.update {
            it.copy(query = query)
        }
    }

    fun getBroodjes() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    apiState = HomeApiState.Loading,
                    errorMessage = ""
                )
            }

            try {
                val response = apiService.getBroodjes()

                if (response.status == 200) {
                    _uiState.update {
                        it.copy(
                            broodjes = response.data,
                            apiState = HomeApiState.Success,
                            errorMessage = ""
                        )
                    }
                } else {
                    val message = "Broodjes konden niet opgehaald worden."

                    _uiState.update {
                        it.copy(
                            apiState = HomeApiState.Error(message),
                            errorMessage = message
                        )
                    }
                }

            }  catch (e: Exception) {
                val message = "Er ging iets mis bij het ophalen van de broodjes."

                _uiState.update {
                    it.copy(
                        apiState = HomeApiState.Error(message),
                        errorMessage = message
                    )
                }

                Log.e("HomeViewModel", e.toString())
            }
        }
    }
}