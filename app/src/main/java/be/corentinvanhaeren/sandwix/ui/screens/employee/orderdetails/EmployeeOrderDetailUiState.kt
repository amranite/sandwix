package be.corentinvanhaeren.sandwix.ui.screens.employee.orderdetails

import be.corentinvanhaeren.sandwix.model.BestellingDetails

data class EmployeeOrderDetailUiState(
    val order: BestellingDetails? = null,
    val apiState: EmployeeOrderDetailApiState = EmployeeOrderDetailApiState.Loading,
    val updateState: EmployeeStatusUpdateApiState = EmployeeStatusUpdateApiState.Idle,
    val errorMessage: String = "",
)

sealed interface EmployeeOrderDetailApiState {
    object Loading : EmployeeOrderDetailApiState
    object Success : EmployeeOrderDetailApiState
    data class Error(val message: String) : EmployeeOrderDetailApiState
}

sealed interface EmployeeStatusUpdateApiState {
    object Idle : EmployeeStatusUpdateApiState
    object Loading : EmployeeStatusUpdateApiState
    object Success : EmployeeStatusUpdateApiState
    data class Error(val message: String) : EmployeeStatusUpdateApiState
}