package be.corentinvanhaeren.sandwix.ui.screens.employee.orderdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.corentinvanhaeren.sandwix.model.AfhalenRequest
import be.corentinvanhaeren.sandwix.model.BestellingStatusUpdateRequest
import be.corentinvanhaeren.sandwix.network.SandwixApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmployeeOrderDetailViewModel(
    private val bestellingId: Int,
    private val apiService: SandwixApiService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployeeOrderDetailUiState())
    val uiState: StateFlow<EmployeeOrderDetailUiState> = _uiState.asStateFlow()

    init {
        loadOrder()
    }

    fun loadOrder() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    apiState = EmployeeOrderDetailApiState.Loading,
                    updateState = EmployeeStatusUpdateApiState.Idle,
                    errorMessage = "",
                )
            }

            try {
                val response = apiService.getBestellingDetails(bestellingId)

                val isSuccess = response.status in 200..299 ||
                        response.code in 200..299

                if (isSuccess) {
                    _uiState.update {
                        it.copy(
                            order = response.data,
                            apiState = EmployeeOrderDetailApiState.Success,
                            errorMessage = "",
                        )
                    }
                } else {
                    val message = "Bestelling kon niet opgehaald worden."

                    _uiState.update {
                        it.copy(
                            apiState = EmployeeOrderDetailApiState.Error(message),
                            errorMessage = message,
                        )
                    }
                }
            } catch (e: Exception) {
                val message = e.localizedMessage
                    ?: "Bestelling kon niet opgehaald worden."

                _uiState.update {
                    it.copy(
                        apiState = EmployeeOrderDetailApiState.Error(message),
                        errorMessage = message,
                    )
                }
            }
        }
    }

    fun updateStatus(status: String) {
        viewModelScope.launch {
            val apiStatus = statusVoorApi(status)

            _uiState.update {
                it.copy(
                    updateState = EmployeeStatusUpdateApiState.Loading,
                    errorMessage = "",
                )
            }

            try {
                val response = apiService.updateBestellingStatus(
                    bestellingId = bestellingId,
                    request = BestellingStatusUpdateRequest(
                        status = apiStatus,
                    ),
                )

                val isSuccess = response.status in 200..299

                if (isSuccess) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            order = currentState.order?.copy(
                                status = apiStatus,
                            ),
                            updateState = EmployeeStatusUpdateApiState.Success,
                            errorMessage = "",
                        )
                    }
                } else {
                    val message = response.message
                        ?: "Status kon niet bijgewerkt worden."

                    _uiState.update {
                        it.copy(
                            updateState = EmployeeStatusUpdateApiState.Error(message),
                            errorMessage = message,
                        )
                    }
                }
            } catch (e: Exception) {
                val message = e.localizedMessage
                    ?: "Status kon niet bijgewerkt worden."

                _uiState.update {
                    it.copy(
                        updateState = EmployeeStatusUpdateApiState.Error(message),
                        errorMessage = message,
                    )
                }
            }
        }
    }

    fun clearUpdateMessage() {
        _uiState.update {
            it.copy(
                updateState = EmployeeStatusUpdateApiState.Idle,
                errorMessage = "",
            )
        }
    }

    private fun statusVoorApi(status: String): String {
        return when (status.trim().lowercase()) {
            "nieuw" -> "nieuw"
            "in bereiding", "in_bereiding" -> "in_bereiding"
            "klaar" -> "klaar"
            "afgehaald" -> "afgehaald"
            "geannuleerd" -> "geannuleerd"
            else -> status.trim().lowercase()
        }
    }

    fun markeerAlsMeegegeven() {
        val order = _uiState.value.order ?: return
        val afhaalCode = order.afhaalCode ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    updateState = EmployeeStatusUpdateApiState.Loading,
                    errorMessage = "",
                )
            }

            try {
                val response = apiService.markeerBestellingAfgehaald(
                    request = AfhalenRequest(
                        bestellingId = order.bestellingId,
                        afhaalCode = afhaalCode
                    )
                )

                val isSuccess = response.status in 200..299

                if (isSuccess) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            order = currentState.order?.copy(
                                status = response.data?.status ?: "afgehaald"
                            ),
                            updateState = EmployeeStatusUpdateApiState.Success,
                            errorMessage = "",
                        )
                    }
                } else {
                    val message = response.message
                        ?: "Bestelling kon niet als afgehaald gezet worden."

                    _uiState.update {
                        it.copy(
                            updateState = EmployeeStatusUpdateApiState.Error(message),
                            errorMessage = message,
                        )
                    }
                }
            } catch (e: Exception) {
                val message = e.localizedMessage
                    ?: "Bestelling kon niet als afgehaald gezet worden."

                _uiState.update {
                    it.copy(
                        updateState = EmployeeStatusUpdateApiState.Error(message),
                        errorMessage = message,
                    )
                }
            }
        }
    }
}
