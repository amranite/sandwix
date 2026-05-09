package be.corentinvanhaeren.sandwix.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpeningsurenResponse(
    val code: Int,
    val status: Int,
    val data: List<Openingsuur>
)

@Serializable
data class Openingsuur(
    @SerialName("openingsuur_id")
    val openingsuurId: Int,

    val dag: String,

    @SerialName("open_tijd")
    val openTijd: String,

    @SerialName("sluit_tijd")
    val sluitTijd: String
)