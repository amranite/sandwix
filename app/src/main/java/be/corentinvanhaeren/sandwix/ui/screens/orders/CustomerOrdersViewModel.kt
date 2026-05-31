package be.corentinvanhaeren.sandwix.ui.screens.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.corentinvanhaeren.sandwix.network.SandwixApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CustomerOrdersViewModel(
    private val gebruikerId: Int,
    private val apiService: SandwixApiService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CustomerOrdersUiState())
    val uiState: StateFlow<CustomerOrdersUiState> = _uiState.asStateFlow()

    init {
        loadOrders()
    }

    fun loadOrders() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    apiState = CustomerOrdersApiState.Loading,
                    errorMessage = "",
                )
            }

            try {
                val response = apiService.getBestellingenVanGebruiker(gebruikerId)
                if (response.status !in 200..299) {
                    showError("Bestellingen konden niet opgehaald worden.")
                    return@launch
                }

                val locations = runCatching {
                    apiService.getLocaties().data.toCustomerPickupLocations()
                }.getOrDefault(emptyMap())

                _uiState.update {
                    it.copy(
                        orders = response.data.map { order ->
                            order.toCustomerOrderSummary(locations)
                        },
                        apiState = CustomerOrdersApiState.Success,
                        errorMessage = "",
                    )
                }
            } catch (exception: Exception) {
                showError(
                    exception.localizedMessage ?: "Bestellingen konden niet opgehaald worden."
                )
            }
        }
    }

    private fun showError(message: String) {
        _uiState.update {
            it.copy(
                apiState = CustomerOrdersApiState.Error(message),
                errorMessage = message,
            )
        }
    }
}
