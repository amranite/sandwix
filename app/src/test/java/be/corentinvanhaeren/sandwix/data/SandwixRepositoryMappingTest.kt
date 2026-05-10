package be.corentinvanhaeren.sandwix.data

import be.corentinvanhaeren.sandwix.model.BroodjeDetailsResponse
import be.corentinvanhaeren.sandwix.model.BroodjesResponse
import be.corentinvanhaeren.sandwix.model.LoginResponse
import be.corentinvanhaeren.sandwix.network.SandwixJson
import kotlinx.serialization.decodeFromString
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.math.BigDecimal

class SandwixRepositoryMappingTest {
    @Test
    fun loginResponse_mapsToAuthSession() {
        val json = """
            {
              "status": 200,
              "message": "Login succesvol",
              "token": "51b29d37d6babfaaa30a97a1879b22f11b24ed59392710f4684d7aa64aeada7b",
              "id": 7,
              "rol": "klant"
            }
        """.trimIndent()

        val session = SandwixJson.decodeFromString<LoginResponse>(json).toAuthSession()

        assertEquals(7, session.userId)
        assertEquals("klant", session.role)
        assertEquals("51b29d37d6babfaaa30a97a1879b22f11b24ed59392710f4684d7aa64aeada7b", session.token)
    }

    @Test
    fun broodjeListItem_mapsToCatalogSandwich() {
        val json = """
            {
              "code": 1,
              "status": 200,
              "data": [
                {
                  "broodje_id": "1",
                  "naam": "Broodje Ham Kaas",
                  "basis_prijs": "4.50",
                  "afbeelding_url": "https://corentinvanhaeren.be/project/api/uploads/1769350999_697627579abe8.jpg"
                }
              ]
            }
        """.trimIndent()

        val sandwich = SandwixJson.decodeFromString<BroodjesResponse>(json).data.first().toSandwich()

        assertEquals(1, sandwich.id)
        assertEquals("Broodje Ham Kaas", sandwich.name)
        assertEquals(BigDecimal("4.50"), sandwich.price)
        assertEquals(emptyList<String>(), sandwich.ingredients)
    }

    @Test
    fun broodjeDetails_mapsIngredientsAndExtras() {
        val json = """
            {
              "code": 1,
              "status": 200,
              "data": {
                "broodje_id": 1,
                "naam": "Broodje Ham Kaas",
                "basis_prijs": "4.50",
                "afbeelding_url": "https://corentinvanhaeren.be/project/api/uploads/1769350999_697627579abe8.jpg",
                "ingredienten": [
                  { "ingredient_id": 3, "naam": "ham", "type": "topping" },
                  { "ingredient_id": 4, "naam": "kaas", "type": "topping" },
                  { "ingredient_id": 11, "naam": "wit brood", "type": "brood" }
                ],
                "extra_ingredienten": [
                  { "ingredient_id": 3, "naam": "ham", "extra_prijs": "0.50" },
                  { "ingredient_id": 6, "naam": "spek", "extra_prijs": "1.50" }
                ]
              }
            }
        """.trimIndent()

        val sandwich = SandwixJson.decodeFromString<BroodjeDetailsResponse>(json).data.toSandwich()

        assertEquals(1, sandwich.id)
        assertEquals(listOf("ham", "kaas", "wit brood"), sandwich.ingredients)
        assertEquals(2, sandwich.extras.size)
        assertEquals("spek", sandwich.extras[1].name)
        assertEquals(BigDecimal("1.50"), sandwich.extras[1].price)
    }

    @Test
    fun repository_formatsBearerAuthorizationHeader() {
        val repository = SandwixRepository()

        assertEquals("Bearer abc123", repository.authHeader("abc123"))
    }

    @Test
    fun httpException_usesApiErrorMessageBody() {
        val body = """
            {
              "status": 401,
              "message": "Ongeldige login"
            }
        """.trimIndent().toResponseBody("application/json".toMediaType())
        val exception = HttpException(Response.error<String>(401, body))

        assertEquals("Ongeldige login", exception.apiErrorMessage())
    }
}
