package it.gmstyle.getit.viewmodels.shoppinglist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.gmstyle.getit.data.entities.ListItem
import it.gmstyle.getit.data.entities.ShoppingList
import it.gmstyle.getit.data.repositories.ShoppingListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ShoppingListViewModel(private val repository: ShoppingListRepository) : ViewModel() {

    private val _state = MutableStateFlow<ShoppingListState>(ShoppingListState.Initial)
    val state get() = _state.asStateFlow()

    fun getShoppingListWithItems(listId: Int) {
        viewModelScope.launch {
            repository.getListById(listId)
                .onStart {
                    _state.value = ShoppingListState.Loading
                }.catch {
                    _state.value =
                        ShoppingListState.Error(it.message ?: "An unexpected error occurred")
                }
                .collect {
                    _state.value = ShoppingListState.Success(it)
                }
        }
    }

    fun saveList(list: ShoppingList) {
        var newListId = 0
        viewModelScope.launch {
            val id = repository.insertList(list)
            newListId = id.toInt()
        }.invokeOnCompletion {
            getShoppingListWithItems(newListId)
        }
    }

    fun updateList(list: ShoppingList) {
        viewModelScope.launch {
            repository.updateList(list)
        }
    }

    fun saveItem(item: ListItem) {
        viewModelScope.launch {
            repository.insertItem(item)
        }.invokeOnCompletion {
            getShoppingListWithItems(item.listId)
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