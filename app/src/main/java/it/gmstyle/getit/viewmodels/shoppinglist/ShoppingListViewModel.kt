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

    private val _uiState = MutableStateFlow<ShoppingListUiState>(ShoppingListUiState.Initial)
    val uiState get() = _uiState.asStateFlow()

    fun getShoppingListWithItems(listId: Int) {
        viewModelScope.launch {
            repository.getListByIdFlow(listId)
                .onStart {
                    _uiState.value = ShoppingListUiState.Loading
                }.catch {
                    _uiState.value =
                        ShoppingListUiState.Error(it.message ?: "An unexpected error occurred")
                }
                .collect {
                    _uiState.value = ShoppingListUiState.Success(it)
                }
        }
    }

    fun saveList(list: ShoppingList) {
        var newListId = 0
        viewModelScope.launch {
            val result = repository.insertList(list)
            if(result.isFailure){
                _uiState.value = ShoppingListUiState.Error("List name already exists, please choose another name")
            } else {
                newListId = result.getOrNull()?.toInt() ?: 0
            }
        }.invokeOnCompletion {
            if (newListId != 0) {
                getShoppingListWithItems(newListId)
            }
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