package be.corentinvanhaeren.sandwix.ui.screens.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.corentinvanhaeren.sandwix.model.CartItem
import be.corentinvanhaeren.sandwix.model.CustomerOrder
import be.corentinvanhaeren.sandwix.model.NieuweBestelling
import be.corentinvanhaeren.sandwix.model.NieuweBestellingIngredient
import be.corentinvanhaeren.sandwix.model.NieuweBestellingItem
import be.corentinvanhaeren.sandwix.model.Openingsuur
import be.corentinvanhaeren.sandwix.model.PickupLocation
import be.corentinvanhaeren.sandwix.network.SandwixApiService
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val gebruikerId: Int,
    private val apiService: SandwixApiService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    private var openingHours = emptyList<Openingsuur>()
    private var pendingCreatedOrder: PendingCreatedOrder? = null

    init {
        loadLocations()
    }

    fun loadLocations() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    apiState = CheckoutApiState.Loading,
                    errorMessage = "",
                )
            }

            try {
                val response = apiService.getLocaties()
                val locations = response.data.map { location ->
                    PickupLocation(
                        id = location.locatieId,
                        name = location.naam,
                        address = listOf(location.adresRegel1, location.adresRegel2)
                            .filter(String::isNotBlank)
                            .joinToString(", "),
                    )
                }

                if (response.status !in 200..299 || locations.isEmpty()) {
                    showLoadError("Afhaallocaties konden niet opgehaald worden.")
                    return@launch
                }

                _uiState.update {
                    it.copy(
                        locations = locations,
                        selectedLocationId = locations.first().id,
                    )
                }
                loadOpeningHours(locations.first().id)
            } catch (exception: Exception) {
                showLoadError(
                    exception.localizedMessage
                        ?: "Afhaallocaties konden niet opgehaald worden."
                )
            }
        }
    }

    fun selectLocation(locationId: Int) {
        if (_uiState.value.selectedLocationId == locationId) return

        _uiState.update {
            it.copy(
                selectedLocationId = locationId,
                dates = emptyList(),
                selectedDate = null,
                times = emptyList(),
                selectedTime = null,
                apiState = CheckoutApiState.Loading,
                errorMessage = "",
            )
        }
        loadOpeningHours(locationId)
    }

    fun selectDate(date: String) {
        val times = timesForDate(date)
        _uiState.update {
            it.copy(
                selectedDate = date,
                times = times,
                selectedTime = times.firstOrNull(),
            )
        }
    }

    fun selectTime(time: String) {
        _uiState.update { it.copy(selectedTime = time) }
    }

    fun updateNote(note: String) {
        _uiState.update {
            it.copy(
                note = note,
                submitState = CheckoutSubmitState.Idle,
            )
        }
    }

    fun submitOrder(
        cartItems: List<CartItem>,
        onSuccess: (CustomerOrder) -> Unit,
    ) {
        pendingCreatedOrder?.let { pendingOrder ->
            loadCreatedOrder(pendingOrder, onSuccess)
            return
        }

        val state = _uiState.value
        val location = state.locations.firstOrNull { it.id == state.selectedLocationId }
        val date = state.selectedDate
        val time = state.selectedTime

        if (location == null || date == null || time == null || cartItems.isEmpty()) {
            showSubmitError("Kies een geldige afhaallocatie, datum en tijd.")
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    submitState = CheckoutSubmitState.Loading,
                    errorMessage = "",
                )
            }

            try {
                val response = apiService.nieuweBestelling(
                    NieuweBestelling(
                        gebruikerId = gebruikerId,
                        locatieId = location.id,
                        afhaalDatum = date,
                        afhaalTijd = time,
                        isBetaald = 0,
                        opmerking = state.note.ifBlank { null },
                        items = cartItems.map { it.toNieuweBestellingItem() },
                    )
                )
                val orderId = response.bestellingId

                if (response.status !in 200..299 || orderId == null) {
                    showSubmitError(response.message ?: "Bestelling kon niet geplaatst worden.")
                    return@launch
                }

                val pendingOrder = PendingCreatedOrder(
                    id = orderId,
                    location = location,
                    items = cartItems,
                )
                pendingCreatedOrder = pendingOrder
                completeCreatedOrder(pendingOrder, onSuccess)
            } catch (exception: Exception) {
                showSubmitError(
                    if (pendingCreatedOrder == null) {
                        exception.localizedMessage ?: "Bestelling kon niet geplaatst worden."
                    } else {
                        "Bestelling is geplaatst, maar de afhaalcode kon niet opgehaald worden."
                    }
                )
            }
        }
    }

    private fun loadOpeningHours(locationId: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.getOpeningsurenVanLocatie(locationId)

                if (_uiState.value.selectedLocationId != locationId) return@launch

                if (response.status !in 200..299) {
                    showLoadError("Openingsuren konden niet opgehaald worden.")
                    return@launch
                }

                openingHours = response.data
                val dates = availableDates()
                val selectedDate = dates.firstOrNull()?.value
                val times = selectedDate?.let(::timesForDate).orEmpty()

                _uiState.update {
                    it.copy(
                        dates = dates,
                        selectedDate = selectedDate,
                        times = times,
                        selectedTime = times.firstOrNull(),
                        apiState = CheckoutApiState.Success,
                        errorMessage = "",
                    )
                }
            } catch (exception: Exception) {
                showLoadError(
                    exception.localizedMessage ?: "Openingsuren konden niet opgehaald worden."
                )
            }
        }
    }

    private fun availableDates(): List<PickupDateOption> {
        val apiFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val labelFormat = SimpleDateFormat("EEE dd/MM", Locale.getDefault())
        val calendar = Calendar.getInstance()

        return buildList {
            repeat(DATE_WINDOW_DAYS) {
                if (openingHours.any { it.dag == calendar.dayCode() }) {
                    add(
                        PickupDateOption(
                            value = apiFormat.format(calendar.time),
                            label = labelFormat.format(calendar.time),
                        )
                    )
                }
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
        }
    }

    private fun timesForDate(date: String): List<String> {
        val calendar = Calendar.getInstance().apply {
            time = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(date) ?: return emptyList()
        }
        val openingHour = openingHours.firstOrNull { it.dag == calendar.dayCode() }
            ?: return emptyList()
        val start = openingHour.openTijd.toMinutes()
        val end = openingHour.sluitTijd.toMinutes()
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val now = Calendar.getInstance()
        val nowInMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)

        return buildList {
            var minutes = start
            while (minutes < end) {
                if (date != today || minutes > nowInMinutes) {
                    add("%02d:%02d".format(minutes / 60, minutes % 60))
                }
                minutes += SLOT_MINUTES
            }
        }
    }

    private fun loadCreatedOrder(
        pendingOrder: PendingCreatedOrder,
        onSuccess: (CustomerOrder) -> Unit,
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    submitState = CheckoutSubmitState.Loading,
                    errorMessage = "",
                )
            }

            try {
                completeCreatedOrder(pendingOrder, onSuccess)
            } catch (_: Exception) {
                showSubmitError("Bestelling is geplaatst, maar de afhaalcode kon niet opgehaald worden.")
            }
        }
    }

    private suspend fun completeCreatedOrder(
        pendingOrder: PendingCreatedOrder,
        onSuccess: (CustomerOrder) -> Unit,
    ) {
        val detailResponse = apiService.getBestellingDetails(pendingOrder.id)
        val details = detailResponse.data

        if (detailResponse.status !in 200..299 || details.afhaalCode.isNullOrBlank()) {
            showSubmitError("Bestelling is geplaatst, maar de afhaalcode kon niet opgehaald worden.")
            return
        }

        pendingCreatedOrder = null
        _uiState.update {
            it.copy(submitState = CheckoutSubmitState.Idle)
        }
        onSuccess(
            CustomerOrder(
                id = pendingOrder.id,
                pickupCode = details.afhaalCode,
                status = details.status,
                pickupLocation = pendingOrder.location,
                pickupTime = details.afhaalTijd.removeSuffix(":00"),
                items = pendingOrder.items,
            )
        )
    }

    private fun CartItem.toNieuweBestellingItem() = NieuweBestellingItem(
        broodjeId = sandwich.id,
        hoeveelheid = quantity,
        opmerking = note.ifBlank { null },
        ingredienten = selectedExtras
            .map { extra ->
                NieuweBestellingIngredient(
                    ingredientId = extra.id,
                    actie = "extra",
                )
            }
            .ifEmpty { null },
    )

    private fun Calendar.dayCode(): String = when (get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> "ma"
        Calendar.TUESDAY -> "di"
        Calendar.WEDNESDAY -> "wo"
        Calendar.THURSDAY -> "do"
        Calendar.FRIDAY -> "vr"
        Calendar.SATURDAY -> "za"
        else -> "zo"
    }

    private fun String.toMinutes(): Int {
        val parts = split(":")
        return parts[0].toInt() * 60 + parts[1].toInt()
    }

    private fun showLoadError(message: String) {
        _uiState.update {
            it.copy(
                apiState = CheckoutApiState.Error(message),
                errorMessage = message,
            )
        }
    }

    private fun showSubmitError(message: String) {
        _uiState.update {
            it.copy(
                submitState = CheckoutSubmitState.Error(message),
                errorMessage = message,
            )
        }
    }

    private companion object {
        const val DATE_WINDOW_DAYS = 7
        const val SLOT_MINUTES = 15
    }

    private data class PendingCreatedOrder(
        val id: Int,
        val location: PickupLocation,
        val items: List<CartItem>,
    )
}
