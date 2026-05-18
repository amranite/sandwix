package be.corentinvanhaeren.sandwix.ui.screens.detail

import be.corentinvanhaeren.sandwix.model.Sandwich

data class SandwichDetailUiState(
    val sandwich: Sandwich? = null,
    val apiState: SandwichDetailApiState = SandwichDetailApiState.Loading,
    val errorMessage: String = "",
)

sealed interface SandwichDetailApiState {
    object Loading : SandwichDetailApiState
    object Success : SandwichDetailApiState
    data class Error(val message: String) : SandwichDetailApiState
}
