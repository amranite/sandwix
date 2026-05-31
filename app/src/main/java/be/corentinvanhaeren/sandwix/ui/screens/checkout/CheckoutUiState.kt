package be.corentinvanhaeren.sandwix.ui.screens.checkout

import be.corentinvanhaeren.sandwix.model.PickupLocation

data class CheckoutUiState(
    val locations: List<PickupLocation> = emptyList(),
    val selectedLocationId: Int? = null,
    val dates: List<PickupDateOption> = emptyList(),
    val selectedDate: String? = null,
    val times: List<String> = emptyList(),
    val selectedTime: String? = null,
    val note: String = "",
    val apiState: CheckoutApiState = CheckoutApiState.Loading,
    val submitState: CheckoutSubmitState = CheckoutSubmitState.Idle,
    val errorMessage: String = "",
)

data class PickupDateOption(
    val value: String,
    val label: String,
)

sealed interface CheckoutApiState {
    object Loading : CheckoutApiState
    object Success : CheckoutApiState
    data class Error(val message: String) : CheckoutApiState
}

sealed interface CheckoutSubmitState {
    object Idle : CheckoutSubmitState
    object Loading : CheckoutSubmitState
    data class Error(val message: String) : CheckoutSubmitState
}
