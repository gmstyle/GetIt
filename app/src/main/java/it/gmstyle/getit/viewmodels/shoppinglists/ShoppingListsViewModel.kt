package it.gmstyle.getit.viewmodels.shoppinglists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.gmstyle.getit.data.entities.ShoppingList
import it.gmstyle.getit.data.entities.ShoppingListWithItems
import it.gmstyle.getit.data.repositories.ShoppingListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ShoppingListsViewModel(private val repository: ShoppingListRepository) : ViewModel() {

    val shoppingLists: Flow<List<ShoppingListWithItems>> = repository.lists

    fun deleteListWithItems(list: ShoppingList) {
        viewModelScope.launch {
            repository.deleteAllItemsByListId(list.id)
            repository.deleteList(list)
        }
    }
}