package be.corentinvanhaeren.sandwix.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// haal de bestellingen op van een bepaalde gebruiker
@Serializable
data class BestellingenGebruikerResponse(
    val code: Int,
    val status: Int,
    val data: List<BestellingOverzicht>
)

@Serializable
data class BestellingOverzicht(
    @SerialName("bestelling_id")
    val bestellingId: Int,

    @SerialName("gebruiker_id")
    val gebruikerId: Int,

    @SerialName("locatie_id")
    val locatieId: Int,

    val status: String,

    val totaalbedrag: String,

    @SerialName("is_betaald")
    val isBetaald: Int,

    val opmerking: String? = null,

    @SerialName("afhaal_datum")
    val afhaalDatum: String,

    @SerialName("afhaal_tijd")
    val afhaalTijd: String,

    @SerialName("afhaal_code")
    val afhaalCode: String? = null,

    @SerialName("bestelling_ontvangen_op")
    val bestellingOntvangenOp: String? = null
)


// haalt alle info van een specifieke bestelling op
@Serializable
data class BestellingDetailsResponse(
    val code: Int,
    val status: Int,
    val data: BestellingDetails
)

@Serializable
data class BestellingDetails(
    @SerialName("bestelling_id")
    val bestellingId: Int,

    @SerialName("gebruiker_id")
    val gebruikerId: Int,

    @SerialName("locatie_id")
    val locatieId: Int,

    val status: String,

    // In detail komt dit als getal terug: 10.5
    val totaalbedrag: Double,

    @SerialName("is_betaald")
    val isBetaald: Int,

    val opmerking: String? = null,

    @SerialName("afhaal_datum")
    val afhaalDatum: String,

    @SerialName("afhaal_tijd")
    val afhaalTijd: String,

    @SerialName("afhaal_code")
    val afhaalCode: String? = null,

    @SerialName("bestelling_ontvangen_op")
    val bestellingOntvangenOp: String? = null,

    val items: List<BestellingItem>
)

@Serializable
data class BestellingItem(
    @SerialName("item_id")
    val itemId: Int,

    @SerialName("broodje_id")
    val broodjeId: Int,

    @SerialName("broodje_naam")
    val broodjeNaam: String,

    val hoeveelheid: Int,

    @SerialName("prijs_per_item")
    val prijsPerItem: Double,

    val opmerking: String? = null,

    val ingredienten: List<BestellingItemIngredient>? = null
)

@Serializable
data class BestellingItemIngredient(
    @SerialName("ingredient_id")
    val ingredientId: Int,

    val naam: String,

    val actie: String,

    @SerialName("extra_prijs")
    val extraPrijs: Double
)

// bestelling aanmaken/toevoegen
@Serializable
data class BestellingAanmakenResponse(
    val code: Int? = null,
    val status: Int? = null,
    val message: String? = null,
    val data: String? = null,

    @SerialName("bestelling_id")
    val bestellingId: Int? = null,

    val totaalbedrag: Double? = null
)

@Serializable
data class NieuweBestelling(
    @SerialName("gebruiker_id")
    val gebruikerId: Int,

    @SerialName("locatie_id")
    val locatieId: Int,

    @SerialName("afhaal_datum")
    val afhaalDatum: String,

    @SerialName("afhaal_tijd")
    val afhaalTijd: String,

    @SerialName("is_betaald")
    val isBetaald: Int,

    val opmerking: String? = null,

    val items: List<NieuweBestellingItem>
)

@Serializable
data class NieuweBestellingItem(
    @SerialName("broodje_id")
    val broodjeId: Int,

    val hoeveelheid: Int,

    val opmerking: String? = null,

    val ingredienten: List<NieuweBestellingIngredient>? = null
)

@Serializable
data class NieuweBestellingIngredient(
    @SerialName("ingredient_id")
    val ingredientId: Int,

    val actie: String
)
