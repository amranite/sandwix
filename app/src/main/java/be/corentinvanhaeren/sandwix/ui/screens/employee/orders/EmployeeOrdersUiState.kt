package be.corentinvanhaeren.sandwix.ui.screens.employee.orders

import be.corentinvanhaeren.sandwix.model.BestellingOverzicht

data class EmployeeOrdersUiState(
    val orders: List<BestellingOverzicht> = emptyList(),
    val apiState: EmployeeOrdersApiState = EmployeeOrdersApiState.Loading,
    val errorMessage: String = "",
)

sealed interface EmployeeOrdersApiState {
    object Loading : EmployeeOrdersApiState
    object Success : EmployeeOrdersApiState
    data class Error(val message: String) : EmployeeOrdersApiState
}