package be.corentinvanhaeren.sandwix.ui.screens.orders

import be.corentinvanhaeren.sandwix.model.BestellingDetailsResponse
import be.corentinvanhaeren.sandwix.model.BestellingenGebruikerResponse
import be.corentinvanhaeren.sandwix.model.LocatiesResponse
import be.corentinvanhaeren.sandwix.network.SandwixJson
import java.math.BigDecimal
import kotlinx.serialization.decodeFromString
import org.junit.Assert.assertEquals
import org.junit.Test

class CustomerOrderModelsTest {
    @Test
    fun overview_mapsServerTotalPickupCodeAndLocation() {
        val locations = SandwixJson.decodeFromString<LocatiesResponse>(locationsJson)
            .data
            .toCustomerPickupLocations()
        val order = SandwixJson.decodeFromString<BestellingenGebruikerResponse>(overviewJson)
            .data
            .first()
            .toCustomerOrderSummary(locations)

        assertEquals(77, order.id)
        assertEquals("FE1FBB", order.pickupCode)
        assertEquals("brussel kerk", order.pickupLocation.name)
        assertEquals("12:30", order.pickupTime)
        assertEquals(BigDecimal("14.50"), order.total)
    }

    @Test
    fun detail_mapsNotesAndIngredientChanges() {
        val locations = SandwixJson.decodeFromString<LocatiesResponse>(locationsJson)
            .data
            .toCustomerPickupLocations()
        val order = SandwixJson.decodeFromString<BestellingDetailsResponse>(detailJson)
            .data
            .toCustomerOrderDetail(locations)

        assertEquals("Graag goed warm", order.note)
        assertEquals(1, order.items.size)
        assertEquals("Eentje zonder ui a.u.b.", order.items.first().note)
        assertEquals("extra", order.items.first().ingredients.first().action)
        assertEquals(BigDecimal("0.5"), order.items.first().ingredients.first().extraPrice)
    }

    private companion object {
        val locationsJson = """
            {
              "code": 1,
              "status": 200,
              "data": [
                {
                  "locatie_id": 4,
                  "naam": "brussel kerk",
                  "adres_regel_1": "kerkstraat 10",
                  "adres_regel_2": "1000 brussel",
                  "telefoonnummer": "04123456789"
                }
              ]
            }
        """.trimIndent()

        val overviewJson = """
            {
              "code": 1,
              "status": 200,
              "data": [
                {
                  "bestelling_id": 77,
                  "gebruiker_id": 1,
                  "locatie_id": 4,
                  "status": "nieuw",
                  "totaalbedrag": "14.50",
                  "is_betaald": 0,
                  "opmerking": "Graag goed warm",
                  "afhaal_datum": "2025-01-10",
                  "afhaal_tijd": "12:30:00",
                  "afhaal_code": "FE1FBB"
                }
              ]
            }
        """.trimIndent()

        val detailJson = """
            {
              "code": 1,
              "status": 200,
              "data": {
                "bestelling_id": 77,
                "gebruiker_id": 1,
                "locatie_id": 4,
                "status": "nieuw",
                "totaalbedrag": 14.5,
                "is_betaald": 0,
                "opmerking": "Graag goed warm",
                "afhaal_datum": "2025-01-10",
                "afhaal_tijd": "12:30:00",
                "afhaal_code": "FE1FBB",
                "items": [
                  {
                    "item_id": 97,
                    "broodje_id": 1,
                    "broodje_naam": "Broodje Ham Kaas",
                    "hoeveelheid": 2,
                    "prijs_per_item": 4.5,
                    "opmerking": "Eentje zonder ui a.u.b.",
                    "ingredienten": [
                      {
                        "ingredient_id": 3,
                        "naam": "ham",
                        "actie": "extra",
                        "extra_prijs": 0.5
                      }
                    ]
                  }
                ]
              }
            }
        """.trimIndent()
    }
}
