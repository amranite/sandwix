package be.corentinvanhaeren.sandwix.model

import kotlinx.serialization.Serializable

// login
@Serializable
data class LoginResponse(
    val status: Int,
    val message: String,
    val token: String? = null,
    val id: Int? = null,
    val rol: String? = null
)

data class AuthSession(
    val token: String,
    val userId: Int,
    val role: String
)

@Serializable
data class Login(
    val email: String,
    val wachtwoord: String
)

// loguout
@Serializable
data class LogoutResponse(
    val status: Int,
    val message: String
)

// niuewe gebruiker aanmaken

@Serializable
data class GebruikerAanmakenResponse(
    val status: Int,
    val message: String,
    val data: String? = null
)

@Serializable
data class NieuweGebruiker(
    val naam: String,
    val email: String,
    val telefoonnummer: String,
    val wachtwoord: String
)
