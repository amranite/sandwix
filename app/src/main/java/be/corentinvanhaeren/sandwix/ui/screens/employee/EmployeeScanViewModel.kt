package be.corentinvanhaeren.sandwix.ui.screens.employee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.corentinvanhaeren.sandwix.model.AfhaalCodeRequest
import be.corentinvanhaeren.sandwix.model.BestellingDetails
import be.corentinvanhaeren.sandwix.network.SandwixApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EmployeeScanUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val scannedOrder: BestellingDetails? = null
)

class EmployeeScanViewModel(
    private val apiService: SandwixApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployeeScanUiState())
    val uiState: StateFlow<EmployeeScanUiState> = _uiState.asStateFlow()

    fun zoekBestellingMetAfhaalCode(
        afhaalCode: String,
        onSuccess: (BestellingDetails) -> Unit
    ) {
        val code = afhaalCode.trim()

        if (code.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Vul een afhaalcode in.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = "",
                    scannedOrder = null
                )
            }

            try {
                val response = apiService.scanAfhaalCode(
                    AfhaalCodeRequest(
                        afhaalCode = code
                    )
                )

                val isSuccess = response.status in 200..299 ||
                        response.code in 200..299

                if (isSuccess) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            scannedOrder = response.data,
                            errorMessage = ""
                        )
                    }

                    onSuccess(response.data)
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Bestelling niet gevonden of niet klaar."
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage
                            ?: "Bestelling niet gevonden of niet klaar."
                    )
                }
            }
        }
    }
}