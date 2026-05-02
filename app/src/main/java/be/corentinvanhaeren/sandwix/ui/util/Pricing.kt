package be.corentinvanhaeren.sandwix.ui.util

import be.corentinvanhaeren.sandwix.model.CartItem
import be.corentinvanhaeren.sandwix.model.Extra
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

internal fun CartItem.lineTotal(): BigDecimal = (sandwich.price + selectedExtras.sumOfPrice()).multiply(BigDecimal(quantity))
internal fun List<CartItem>.totalPrice(): BigDecimal = fold(BigDecimal.ZERO) { total, item -> total + item.lineTotal() }
internal fun List<Extra>.sumOfPrice(): BigDecimal = fold(BigDecimal.ZERO) { total, extra -> total + extra.price }
internal fun formatPrice(value: BigDecimal): String = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-BE")).format(value)
