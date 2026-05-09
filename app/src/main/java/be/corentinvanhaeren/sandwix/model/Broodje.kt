package be.corentinvanhaeren.sandwix.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BroodjesResponse(
    val code: Int,
    val status: Int,
    val data: List<Broodje>
)

@Serializable
data class Broodje(
    @SerialName("broodje_id")
    val broodjeId: String,

    val naam: String,

    @SerialName("basis_prijs")
    val basisPrijs: String,

    @SerialName("afbeelding_url")
    val afbeeldingUrl: String
)

@Serializable
data class BroodjeDetailsResponse(
    val code: Int,
    val status: Int,
    val data: BroodjeDetails
)

@Serializable
data class BroodjeDetails(
    @SerialName("broodje_id")
    val broodjeId: Int,

    val naam: String,

    @SerialName("basis_prijs")
    val basisPrijs: String,

    @SerialName("afbeelding_url")
    val afbeeldingUrl: String,

    val ingredienten: List<Ingredient>,

    @SerialName("extra_ingredienten")
    val extraIngredienten: List<ExtraIngredient>
)

@Serializable
data class Ingredient(
    @SerialName("ingredient_id")
    val ingredientId: Int,

    val naam: String,

    val type: String
)

@Serializable
data class ExtraIngredient(
    @SerialName("ingredient_id")
    val ingredientId: Int,

    val naam: String,

    @SerialName("extra_prijs")
    val extraPrijs: String
)
