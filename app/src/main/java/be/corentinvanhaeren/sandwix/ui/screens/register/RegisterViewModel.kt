package be.corentinvanhaeren.sandwix.ui.screens.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.corentinvanhaeren.sandwix.model.NieuweGebruiker
import be.corentinvanhaeren.sandwix.network.SandwixApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val apiService: SandwixApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNaamUpdate(naam: String) {
        _uiState.update {
            it.copy(
                naam = naam,
                registerFailed = false,
                errorMessage = "",
                apiState = RegisterApiState.Idle
            )
        }
    }

    fun onEmailUpdate(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                registerFailed = false,
                errorMessage = "",
                apiState = RegisterApiState.Idle
            )
        }
    }

    fun onTelefoonnummerUpdate(telefoonnummer: String) {
        _uiState.update {
            it.copy(
                telefoonnummer = telefoonnummer,
                registerFailed = false,
                errorMessage = "",
                apiState = RegisterApiState.Idle
            )
        }
    }

    fun onWachtwoordUpdate(wachtwoord: String) {
        _uiState.update {
            it.copy(
                wachtwoord = wachtwoord,
                registerFailed = false,
                errorMessage = "",
                apiState = RegisterApiState.Idle
            )
        }
    }

    fun onBevestigWachtwoordUpdate(bevestigWachtwoord: String) {
        _uiState.update {
            it.copy(
                bevestigWachtwoord = bevestigWachtwoord,
                registerFailed = false,
                errorMessage = "",
                apiState = RegisterApiState.Idle
            )
        }
    }

    fun register(
        onRegisterSuccess: () -> Unit
    ) {
        val state = uiState.value

        if (
            state.naam.isBlank() ||
            state.email.isBlank() ||
            state.telefoonnummer.isBlank() ||
            state.wachtwoord.isBlank() ||
            state.bevestigWachtwoord.isBlank()
        ) {
            val message = "Vul alle velden in."

            _uiState.update {
                it.copy(
                    registerFailed = true,
                    errorMessage = message,
                    apiState = RegisterApiState.Error(message)
                )
            }
            return
        }

        if (state.wachtwoord != state.bevestigWachtwoord) {
            val message = "De wachtwoorden komen niet overeen."

            _uiState.update {
                it.copy(
                    registerFailed = true,
                    errorMessage = message,
                    apiState = RegisterApiState.Error(message)
                )
            }
            return
        }

        val nieuweGebruiker = NieuweGebruiker(
            naam = state.naam,
            email = state.email,
            telefoonnummer = state.telefoonnummer,
            wachtwoord = state.wachtwoord
        )

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    apiState = RegisterApiState.Loading,
                    registerFailed = false,
                    errorMessage = ""
                )
            }

            try {
                val response = apiService.maakGebruiker(
                    gebruiker = nieuweGebruiker
                )

                if (response.status == 200) {
                    _uiState.update {
                        it.copy(
                            registerFailed = false,
                            errorMessage = "",
                            apiState = RegisterApiState.Success
                        )
                    }

                    onRegisterSuccess()
                } else {
                    val message = response.message.ifBlank {
                        "Registreren mislukt."
                    }

                    _uiState.update {
                        it.copy(
                            registerFailed = true,
                            errorMessage = message,
                            apiState = RegisterApiState.Error(message)
                        )
                    }
                }

            } catch (e: Exception) {
                val message = "Er ging iets mis bij het registreren."

                _uiState.update {
                    it.copy(
                        registerFailed = true,
                        errorMessage = message,
                        apiState = RegisterApiState.Error(message)
                    )
                }

                Log.e("RegisterViewModel", "Fout bij registreren", e)
            }
        }
    }
}