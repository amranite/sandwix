package be.corentinvanhaeren.sandwix.network

import be.corentinvanhaeren.sandwix.model.BestellingAanmakenResponse
import be.corentinvanhaeren.sandwix.model.BestellingDetailsResponse
import be.corentinvanhaeren.sandwix.model.BestellingenGebruikerResponse
import be.corentinvanhaeren.sandwix.model.BroodjeDetailsResponse
import be.corentinvanhaeren.sandwix.model.BroodjesResponse
import be.corentinvanhaeren.sandwix.model.GebruikerAanmakenResponse
import be.corentinvanhaeren.sandwix.model.LocatiesResponse
import be.corentinvanhaeren.sandwix.model.Login
import be.corentinvanhaeren.sandwix.model.LoginResponse
import be.corentinvanhaeren.sandwix.model.LogoutResponse
import be.corentinvanhaeren.sandwix.model.NieuweBestelling
import be.corentinvanhaeren.sandwix.model.NieuweGebruiker
import be.corentinvanhaeren.sandwix.model.OpeningsurenResponse
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

private const val baseUrl = "https://corentinvanhaeren.be/project/api/"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(baseUrl)
    .build()

interface SandwixApiService {

    // broodje
    @GET("broodjes")
    suspend fun getBroodjes(): BroodjesResponse

    @GET("broodjes/{broodjeId}")
    suspend fun getBroodjeDetails(
        @Path("broodjeId") broodjeId: Int
    ): BroodjeDetailsResponse

    // bestelling
    @GET("bestellingen/gebruiker/{gebruiker_id}")
    suspend fun getBestellingenVanGebruiker(
        @Path("gebruiker_id") gebruikerId: Int,
        @Header("Authorization") authorization: String
    ): BestellingenGebruikerResponse

    @GET("bestellingen/{bestelling_id}")
    suspend fun getBestellingDetails(
        @Path("bestelling_id") bestellingId: Int,
        @Header("Authorization") authorization: String
    ): BestellingDetailsResponse

    @POST("bestellingen/full")
    suspend fun nieuweBestelling(
        @Header("Authorization") authorization: String,
        @Body bestelling: NieuweBestelling
    ): BestellingAanmakenResponse

    // locatie
    @GET("locaties")
    suspend fun getLocaties(
        @Header("Authorization") authorization: String
    ): LocatiesResponse

    // openingsuur
    @GET("locaties/{locatie_id}/openingsuren")
    suspend fun getOpeningsurenVanLocatie(
        @Path("locatie_id") locatieId: Int,
        @Header("Authorization") authorization: String
    ): OpeningsurenResponse

    // gebruiker
    @POST("auth/login")
    suspend fun login(
        @Body loginRequest: Login
    ): LoginResponse

    @DELETE("auth/logout")
    suspend fun logout(
        @Header("Authorization") authorization: String
    ): LogoutResponse

    @POST("gebruikers")
    suspend fun maakGebruiker(
        @Header("Authorization") authorization: String,
        @Body gebruiker: NieuweGebruiker
    ): GebruikerAanmakenResponse
}

object SandwixApi {
    val retroFitService : SandwixApiService by lazy {
        retrofit.create(SandwixApiService::class.java)
    }
}