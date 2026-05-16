package be.corentinvanhaeren.sandwix.ui.screens.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.corentinvanhaeren.sandwix.model.Login
import be.corentinvanhaeren.sandwix.network.SandwixApi
import be.corentinvanhaeren.sandwix.network.SandwixApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val apiService: SandwixApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailUpdate(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                loginFailed = false,
                errorMessage = "",
                apiState = LoginApiState.Idle
            )
        }
    }

    fun onPasswordUpdate(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                loginFailed = false,
                errorMessage = "",
                apiState = LoginApiState.Idle
            )
        }
    }

    fun login(
        onLoginSuccess: (token: String, gebruikerId: Int, rol: String) -> Unit
    ) {
        val email = uiState.value.email
        val password = uiState.value.password

        if (email.isBlank() || password.isBlank()) {
            _uiState.update {
                it.copy(
                    loginFailed = true,
                    errorMessage = "Vul je email en wachtwoord in.",
                    apiState = LoginApiState.Error("Vul je email en wachtwoord in.")
                )
            }
            return
        }

        val loginRequest = Login(
            email = email,
            wachtwoord = password
        )

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    apiState = LoginApiState.Loading,
                    loginFailed = false,
                    errorMessage = ""
                )
            }

            try {
                val response = apiService.login(loginRequest)

                if (
                    response.status == 200 &&
                    response.token != null &&
                    response.id != null &&
                    response.rol != null
                )  {
                    _uiState.update {
                        it.copy(
                            loginFailed = false,
                            errorMessage = "",
                            apiState = LoginApiState.Success
                        )
                    }
                    onLoginSuccess(
                        response.token,
                        response.id,
                        response.rol
                    )
                } else {
                    val message = response.message.ifBlank {
                        "Login mislukt."
                    }

                    _uiState.update {
                        it.copy(
                            loginFailed = true,
                            errorMessage = message,
                            apiState = LoginApiState.Error(message)
                        )
                    }
                }

            } catch(e: Exception) {
                Log.e("login", e.toString())
            }
        }
    }
}