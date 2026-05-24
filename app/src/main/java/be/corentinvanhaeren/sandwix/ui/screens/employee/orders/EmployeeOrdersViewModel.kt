package be.corentinvanhaeren.sandwix.ui.screens.employee.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.corentinvanhaeren.sandwix.network.SandwixApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmployeeOrdersViewModel(
    private val apiService: SandwixApiService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployeeOrdersUiState())
    val uiState: StateFlow<EmployeeOrdersUiState> = _uiState.asStateFlow()

    init {
        getBestellingenVandaag()
    }

    fun getBestellingenVandaag() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    apiState = EmployeeOrdersApiState.Loading,
                    errorMessage = "",
                )
            }

            try {
                val response = apiService.getBestellingenVandaag()

                val isSuccess = response.status in 200..299 ||
                        response.code in 200..299

                if (isSuccess) {
                    _uiState.update {
                        it.copy(
                            orders = response.data,
                            apiState = EmployeeOrdersApiState.Success,
                            errorMessage = "",
                        )
                    }
                } else {
                    val message = "Bestellingen konden niet opgehaald worden."

                    _uiState.update {
                        it.copy(
                            apiState = EmployeeOrdersApiState.Error(message),
                            errorMessage = message,
                        )
                    }
                }
            } catch (e: Exception) {
                val message = e.localizedMessage
                    ?: "Bestellingen konden niet opgehaald worden."

                _uiState.update {
                    it.copy(
                        apiState = EmployeeOrdersApiState.Error(message),
                        errorMessage = message,
                    )
                }
            }
        }
    }
}