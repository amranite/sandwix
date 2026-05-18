package be.corentinvanhaeren.sandwix.model

import java.math.BigDecimal

data class Sandwich(
    val id: Int,
    val name: String,
    val description: String,
    val ingredients: List<String>,
    val price: BigDecimal,
    val imageUrl: String = "",
    val extras: List<Extra> = emptyList(),
)

data class Extra(
    val id: Int,
    val name: String,
    val price: BigDecimal,
)

data class CartItem(
    val sandwich: Sandwich,
    val quantity: Int = 1,
    val selectedExtras: List<Extra> = emptyList(),
    val note: String = "",
)

data class PickupLocation(
    val id: Int,
    val name: String,
    val address: String,
    val isOpen: Boolean = true,
)

data class CustomerOrder(
    val id: Int,
    val pickupCode: String,
    val status: String,
    val pickupLocation: PickupLocation,
    val pickupTime: String,
    val items: List<CartItem>,
)

val sampleExtras = listOf(
    Extra(1, "Extra cheese", BigDecimal("0.80")),
    Extra(2, "Crispy bacon", BigDecimal("1.20")),
    Extra(3, "Avocado", BigDecimal("1.50")),
    Extra(4, "Spicy mayo", BigDecimal("0.60")),
)

val sampleSandwiches = listOf(
    Sandwich(
        id = 1,
        name = "Classic Club",
        description = "Grilled chicken, bacon, tomato, lettuce and house mayo on a crisp baguette.",
        ingredients = listOf("Chicken", "Bacon", "Tomato", "Lettuce", "House mayo"),
        price = BigDecimal("6.90"),
        imageUrl = "",
        extras = sampleExtras,
    ),
    Sandwich(
        id = 2,
        name = "Caprese Crunch",
        description = "Mozzarella, tomato, basil pesto and roasted pine nuts.",
        ingredients = listOf("Mozzarella", "Tomato", "Basil pesto", "Pine nuts"),
        price = BigDecimal("5.80"),
        imageUrl = "",
        extras = sampleExtras,
    ),
    Sandwich(
        id = 3,
        name = "Tuna Melt",
        description = "Tuna salad, cheddar, pickled onion and cucumber, toasted warm.",
        ingredients = listOf("Tuna", "Cheddar", "Pickled onion", "Cucumber"),
        price = BigDecimal("6.40"),
        imageUrl = "",
        extras = sampleExtras,
    ),
    Sandwich(
        id = 4,
        name = "Garden Hummus",
        description = "Hummus, grilled vegetables, rocket and lemon dressing.",
        ingredients = listOf("Hummus", "Grilled vegetables", "Rocket", "Lemon dressing"),
        price = BigDecimal("5.50"),
        imageUrl = "",
        extras = sampleExtras,
    ),
)

val sampleLocations = listOf(
    PickupLocation(1, "Sandwix Central", "Main Street 12, Brussels"),
    PickupLocation(2, "Sandwix North", "Station Avenue 4, Antwerp"),
    PickupLocation(3, "Sandwix Campus", "Innovation Lane 8, Leuven", isOpen = false),
)

fun sampleOrders(): List<CustomerOrder> = listOf(
    CustomerOrder(
        id = 1042,
        pickupCode = "SWX-481",
        status = "Ready for pickup",
        pickupLocation = sampleLocations.first(),
        pickupTime = "12:15",
        items = listOf(
            CartItem(sampleSandwiches[0], quantity = 1, selectedExtras = listOf(sampleExtras[0])),
            CartItem(sampleSandwiches[3], quantity = 2),
        ),
    ),
    CustomerOrder(
        id = 1039,
        pickupCode = "SWX-230",
        status = "Completed",
        pickupLocation = sampleLocations[1],
        pickupTime = "Yesterday 11:45",
        items = listOf(CartItem(sampleSandwiches[2], quantity = 1, selectedExtras = listOf(sampleExtras[3]))),
    ),
)
