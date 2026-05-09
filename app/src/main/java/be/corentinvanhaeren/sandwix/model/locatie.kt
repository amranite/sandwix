package be.corentinvanhaeren.sandwix.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocatiesResponse(
    val code: Int,
    val status: Int,
    val data: List<Locatie>
)

@Serializable
data class Locatie(
    @SerialName("locatie_id")
    val locatieId: Int,

    val naam: String,

    @SerialName("adres_regel_1")
    val adresRegel1: String,

    @SerialName("adres_regel_2")
    val adresRegel2: String,

    val telefoonnummer: String
)