package be.corentinvanhaeren.sandwix.ui.screens.orders

import be.corentinvanhaeren.sandwix.model.BestellingDetails
import be.corentinvanhaeren.sandwix.model.BestellingOverzicht
import be.corentinvanhaeren.sandwix.model.Locatie
import java.math.BigDecimal

data class CustomerOrderSummary(
    val id: Int,
    val pickupCode: String,
    val status: String,
    val pickupLocation: CustomerPickupLocation,
    val pickupDate: String,
    val pickupTime: String,
    val total: BigDecimal,
)

data class CustomerOrderDetail(
    val id: Int,
    val pickupCode: String,
    val status: String,
    val pickupLocation: CustomerPickupLocation,
    val pickupDate: String,
    val pickupTime: String,
    val note: String?,
    val total: BigDecimal,
    val items: List<CustomerOrderLine>,
)

data class CustomerPickupLocation(
    val name: String,
    val address: String,
)

data class CustomerOrderLine(
    val name: String,
    val quantity: Int,
    val unitPrice: BigDecimal,
    val note: String?,
    val ingredients: List<CustomerIngredientChange>,
)

data class CustomerIngredientChange(
    val name: String,
    val action: String,
    val extraPrice: BigDecimal,
)

internal fun BestellingOverzicht.toCustomerOrderSummary(
    locations: Map<Int, CustomerPickupLocation>,
) = CustomerOrderSummary(
    id = bestellingId,
    pickupCode = afhaalCode.orEmpty(),
    status = status,
    pickupLocation = locations[locatieId] ?: fallbackLocation(locatieId),
    pickupDate = afhaalDatum,
    pickupTime = afhaalTijd.withoutSeconds(),
    total = totaalbedrag.toBigDecimal(),
)

internal fun BestellingDetails.toCustomerOrderDetail(
    locations: Map<Int, CustomerPickupLocation>,
) = CustomerOrderDetail(
    id = bestellingId,
    pickupCode = afhaalCode.orEmpty(),
    status = status,
    pickupLocation = locations[locatieId] ?: fallbackLocation(locatieId),
    pickupDate = afhaalDatum,
    pickupTime = afhaalTijd.withoutSeconds(),
    note = opmerking,
    total = BigDecimal.valueOf(totaalbedrag),
    items = items.map { item ->
        CustomerOrderLine(
            name = item.broodjeNaam,
            quantity = item.hoeveelheid,
            unitPrice = BigDecimal.valueOf(item.prijsPerItem),
            note = item.opmerking,
            ingredients = item.ingredienten.orEmpty().map { ingredient ->
                CustomerIngredientChange(
                    name = ingredient.naam,
                    action = ingredient.actie,
                    extraPrice = BigDecimal.valueOf(ingredient.extraPrijs),
                )
            },
        )
    },
)

internal fun List<Locatie>.toCustomerPickupLocations(): Map<Int, CustomerPickupLocation> =
    associate { location ->
        location.locatieId to CustomerPickupLocation(
            name = location.naam,
            address = listOf(location.adresRegel1, location.adresRegel2)
                .filter(String::isNotBlank)
                .joinToString(", "),
        )
    }

private fun fallbackLocation(locationId: Int) = CustomerPickupLocation(
    name = "Locatie #$locationId",
    address = "",
)

private fun String.withoutSeconds(): String = removeSuffix(":00")
