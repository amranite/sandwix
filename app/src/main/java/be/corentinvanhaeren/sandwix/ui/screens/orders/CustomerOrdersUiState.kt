package be.corentinvanhaeren.sandwix.ui.screens.orders

data class CustomerOrdersUiState(
    val orders: List<CustomerOrderSummary> = emptyList(),
    val apiState: CustomerOrdersApiState = CustomerOrdersApiState.Loading,
    val errorMessage: String = "",
)

sealed interface CustomerOrdersApiState {
    object Loading : CustomerOrdersApiState
    object Success : CustomerOrdersApiState
    data class Error(val message: String) : CustomerOrdersApiState
}

data class CustomerOrderDetailUiState(
    val order: CustomerOrderDetail? = null,
    val apiState: CustomerOrderDetailApiState = CustomerOrderDetailApiState.Loading,
    val errorMessage: String = "",
)

sealed interface CustomerOrderDetailApiState {
    object Loading : CustomerOrderDetailApiState
    object Success : CustomerOrderDetailApiState
    data class Error(val message: String) : CustomerOrderDetailApiState
}
