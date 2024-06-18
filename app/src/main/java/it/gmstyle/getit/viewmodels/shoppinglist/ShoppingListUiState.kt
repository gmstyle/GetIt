package it.gmstyle.getit.viewmodels.shoppinglist

import it.gmstyle.getit.data.entities.ShoppingListWithItems

sealed class ShoppingListUiState {
    data object Initial : ShoppingListUiState()
    data object Loading : ShoppingListUiState()
    data class Error(val message: String) : ShoppingListUiState()
    data class Success(val listWithItems: ShoppingListWithItems) : ShoppingListUiState()

}