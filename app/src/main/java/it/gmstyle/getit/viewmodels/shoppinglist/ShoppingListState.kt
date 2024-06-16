package it.gmstyle.getit.viewmodels.shoppinglist

import it.gmstyle.getit.data.entities.ShoppingListWithItems

sealed class ShoppingListState {
    data object Initial : ShoppingListState()
    data object Loading : ShoppingListState()
    data class Error(val message: String) : ShoppingListState()
    data class Success(val listWithItems: ShoppingListWithItems) : ShoppingListState()

}