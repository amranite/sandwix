package be.corentinvanhaeren.sandwix.data

import be.corentinvanhaeren.sandwix.model.AuthSession
import be.corentinvanhaeren.sandwix.model.Broodje
import be.corentinvanhaeren.sandwix.model.BroodjeDetails
import be.corentinvanhaeren.sandwix.model.Extra
import be.corentinvanhaeren.sandwix.model.LoginResponse
import be.corentinvanhaeren.sandwix.model.Sandwich
import java.math.BigDecimal

internal fun LoginResponse.toAuthSession(): AuthSession {
    return AuthSession(
        token = requireNotNull(token) { "Token ontbreekt in login response" },
        userId = requireNotNull(id) { "User id ontbreekt in login response" },
        role = requireNotNull(rol) { "Rol ontbreekt in login response" }
    )
}

internal fun Broodje.toSandwich(): Sandwich = Sandwich(
    id = broodjeId.toInt(),
    name = naam,
    description = "Tap to view ingredients and extras.",
    ingredients = emptyList(),
    price = basisPrijs.toBigDecimal(),
    imageUrl = afbeeldingUrl,
)

internal fun BroodjeDetails.toSandwich(): Sandwich = Sandwich(
    id = broodjeId,
    name = naam,
    description = ingredienten.joinToString { it.naam },
    ingredients = ingredienten.map { it.naam },
    price = basisPrijs.toBigDecimal(),
    imageUrl = afbeeldingUrl,
    extras = extraIngredienten.map { extra ->
        Extra(
            id = extra.ingredientId,
            name = extra.naam,
            price = extra.extraPrijs.toBigDecimal(),
        )
    },
)

private fun String.toBigDecimal(): BigDecimal = BigDecimal(this)
