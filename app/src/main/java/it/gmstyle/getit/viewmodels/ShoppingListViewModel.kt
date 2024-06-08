package it.gmstyle.getit.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.gmstyle.getit.data.entities.ListItem
import it.gmstyle.getit.data.entities.ShoppingList
import it.gmstyle.getit.data.repositories.ShoppingListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ShoppingListViewModel(private val repository: ShoppingListRepository) : ViewModel() {

    val shoppingLists: Flow<List<ShoppingList>> = repository.lists

    fun getShoppingListById(listId: Int): Flow<ShoppingList> =
        repository.getListById(listId)
    fun getItemsByListId(listId: Int): Flow<List<ListItem>> =
        repository.getItemsByListId(listId)

    fun insertList(list: ShoppingList) {
        viewModelScope.launch {
            repository.insertList(list)
        }
    }

    fun deleteList(list: ShoppingList) {
        viewModelScope.launch {
            repository.deleteList(list)
        }
    }

    fun updateList(list: ShoppingList) {
        viewModelScope.launch {
            repository.updateList(list)
        }
    }

    fun insertItem(item: ListItem) {
        viewModelScope.launch {
            repository.insertItem(item)
        }
    }

    fun deleteItem(item: ListItem) {
        viewModelScope.launch {
            repository.deleteItem(item)
        }
    }

    fun updateItem(item: ListItem) {
        viewModelScope.launch {
            repository.updateItem(item)
        }
    }
}