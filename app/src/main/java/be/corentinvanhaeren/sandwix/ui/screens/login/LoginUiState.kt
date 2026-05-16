package be.corentinvanhaeren.sandwix.ui.screens.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val apiState: LoginApiState = LoginApiState.Idle,
    val loginFailed: Boolean = false,
    val errorMessage: String = ""
)

sealed interface LoginApiState {
    object Idle : LoginApiState
    object Loading : LoginApiState
    object Success : LoginApiState
    data class Error(val message: String) : LoginApiState
}