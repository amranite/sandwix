package be.corentinvanhaeren.sandwix.ui.screens.register

data class RegisterUiState(
    val naam: String = "",
    val email: String = "",
    val telefoonnummer: String = "",
    val wachtwoord: String = "",
    val bevestigWachtwoord: String = "",

    val apiState: RegisterApiState = RegisterApiState.Idle,
    val registerFailed: Boolean = false,
    val errorMessage: String = ""
)

sealed interface RegisterApiState {
    object Idle : RegisterApiState
    object Loading : RegisterApiState
    object Success : RegisterApiState
    data class Error(val message: String) : RegisterApiState
}