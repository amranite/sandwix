package be.corentinvanhaeren.sandwix.ui.screens.home

import be.corentinvanhaeren.sandwix.model.Sandwich

data class HomeUiState(
    val sandwiches: List<Sandwich> = emptyList(),
    val query: String = "",
    val apiState: HomeApiState = HomeApiState.Loading,
    val errorMessage: String = ""
)

sealed interface HomeApiState {
    object Loading : HomeApiState
    object Success : HomeApiState
    data class Error(val message: String) : HomeApiState
}
