package be.corentinvanhaeren.sandwix.data

import be.corentinvanhaeren.sandwix.model.Login
import be.corentinvanhaeren.sandwix.model.AuthSession
import be.corentinvanhaeren.sandwix.model.Sandwich
import be.corentinvanhaeren.sandwix.network.SandwixApi
import be.corentinvanhaeren.sandwix.network.SandwixApiService
import be.corentinvanhaeren.sandwix.network.SandwixJson
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import retrofit2.HttpException
import java.io.IOException

class SandwixRepository(
    private val apiService: SandwixApiService,
) {
    suspend fun login(email: String, password: String): ApiResult<AuthSession> = safeApiCall {
        apiService.login(Login(email = email, wachtwoord = password)).toAuthSession()
    }

    suspend fun getSandwiches(): ApiResult<List<Sandwich>> = safeApiCall {
        apiService.getBroodjes().data.map { broodje -> broodje.toSandwich() }
    }

    suspend fun getSandwichDetails(id: Int): ApiResult<Sandwich> = safeApiCall {
        apiService.getBroodjeDetails(id).data.toSandwich()
    }

    fun authHeader(token: String): String = "Bearer $token"

    private suspend fun <T> safeApiCall(block: suspend () -> T): ApiResult<T> = try {
        ApiResult.Success(block())
    } catch (exception: HttpException) {
        ApiResult.Error(
            message = exception.apiErrorMessage(),
            statusCode = exception.code(),
            cause = exception,
        )
    } catch (exception: IOException) {
        ApiResult.Error(
            message = "Network error. Check your connection and try again.",
            cause = exception,
        )
    } catch (exception: Exception) {
        ApiResult.Error(
            message = exception.message ?: "Unexpected API error.",
            cause = exception,
        )
    }
}

@Serializable
private data class ApiErrorBody(
    val status: Int? = null,
    val message: String? = null,
)

internal fun HttpException.apiErrorMessage(): String {
    val errorJson = response()?.errorBody()?.string() ?: return message()
    return runCatching {
        SandwixJson.decodeFromString<ApiErrorBody>(errorJson).message
    }.getOrNull().takeUnless { it.isNullOrBlank() } ?: message()
}
