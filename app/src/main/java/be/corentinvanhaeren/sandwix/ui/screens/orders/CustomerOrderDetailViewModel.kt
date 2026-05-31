package be.corentinvanhaeren.sandwix.ui.screens.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.corentinvanhaeren.sandwix.network.SandwixApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CustomerOrderDetailViewModel(
    private val bestellingId: Int,
    private val apiService: SandwixApiService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CustomerOrderDetailUiState())
    val uiState: StateFlow<CustomerOrderDetailUiState> = _uiState.asStateFlow()

    init {
        loadOrder()
    }

    fun loadOrder() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    apiState = CustomerOrderDetailApiState.Loading,
                    errorMessage = "",
                )
            }

            try {
                val response = apiService.getBestellingDetails(bestellingId)
                if (response.status !in 200..299) {
                    showError("Bestelling kon niet opgehaald worden.")
                    return@launch
                }

                val locations = runCatching {
                    apiService.getLocaties().data.toCustomerPickupLocations()
                }.getOrDefault(emptyMap())

                _uiState.update {
                    it.copy(
                        order = response.data.toCustomerOrderDetail(locations),
                        apiState = CustomerOrderDetailApiState.Success,
                        errorMessage = "",
                    )
                }
            } catch (exception: Exception) {
                showError(
                    exception.localizedMessage ?: "Bestelling kon niet opgehaald worden."
                )
            }
        }
    }

    private fun showError(message: String) {
        _uiState.update {
            it.copy(
                apiState = CustomerOrderDetailApiState.Error(message),
                errorMessage = message,
            )
        }
    }
}
