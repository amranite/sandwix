package be.corentinvanhaeren.sandwix.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.corentinvanhaeren.sandwix.data.ApiResult
import be.corentinvanhaeren.sandwix.data.SandwixRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SandwichDetailViewModel(
    private val sandwichId: Int,
    private val repository: SandwixRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SandwichDetailUiState())
    val uiState: StateFlow<SandwichDetailUiState> = _uiState.asStateFlow()

    init {
        loadSandwich()
    }

    fun loadSandwich() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    apiState = SandwichDetailApiState.Loading,
                    errorMessage = "",
                )
            }

            when (val result = repository.getSandwichDetails(sandwichId)) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(
                            sandwich = result.data,
                            apiState = SandwichDetailApiState.Success,
                            errorMessage = "",
                        )
                    }
                }

                is ApiResult.Error -> {
                    val message = result.message.ifBlank {
                        "Broodje kon niet opgehaald worden."
                    }

                    _uiState.update {
                        it.copy(
                            apiState = SandwichDetailApiState.Error(message),
                            errorMessage = message,
                        )
                    }
                }
            }
        }
    }
}
