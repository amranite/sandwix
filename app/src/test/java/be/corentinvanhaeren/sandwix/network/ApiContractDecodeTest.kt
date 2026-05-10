package be.corentinvanhaeren.sandwix.network

import be.corentinvanhaeren.sandwix.model.BestellingAanmakenResponse
import be.corentinvanhaeren.sandwix.model.BroodjesResponse
import kotlinx.serialization.decodeFromString
import org.junit.Assert.assertEquals
import org.junit.Test

class ApiContractDecodeTest {
    @Test
    fun bestellingAanmakenResponse_decodesSwaggerSuccessShape() {
        val json = """
            {
              "status": 200,
              "data": "ok",
              "bestelling_id": 123,
              "totaalbedrag": 12.5,
              "ignored_future_field": "safe"
            }
        """.trimIndent()

        val response = SandwixJson.decodeFromString<BestellingAanmakenResponse>(json)

        assertEquals(200, response.status)
        assertEquals("ok", response.data)
        assertEquals(123, response.bestellingId)
        assertEquals(12.5, response.totaalbedrag!!, 0.0)
    }

    @Test
    fun broodjesResponse_decodesStringBroodjeIdsFromLiveApiShape() {
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

        val response = SandwixJson.decodeFromString<BroodjesResponse>(json)

        assertEquals("1", response.data.first().broodjeId)
    }
}
