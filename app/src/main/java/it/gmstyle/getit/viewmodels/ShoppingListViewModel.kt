package it.gmstyle.getit.viewmodels

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.gmstyle.getit.data.entities.ListItem
import it.gmstyle.getit.data.entities.ShoppingList
import it.gmstyle.getit.data.entities.ShoppingListWithItems
import it.gmstyle.getit.data.repositories.ShoppingListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ShoppingListViewModel(private val repository: ShoppingListRepository) : ViewModel() {

    val listName = mutableStateOf("")
    var itemName = mutableStateOf("")
    val list = MutableStateFlow(ShoppingListWithItems(ShoppingList(0, ""), emptyList()))
    val items = MutableStateFlow(emptyList<ListItem>())
    val newListId = mutableIntStateOf(0)

    fun getShoppingListWithItems(listId: Int) {
        viewModelScope.launch {
            repository.getListById(listId)
                .collect {
                    list.value = it
                    listName.value = it.list.name
                    items.value = it.items
                }
        }
    }

    fun saveList(list: ShoppingList) {
        viewModelScope.launch {
            val id = repository.insertList(list)
            newListId.intValue = id.toInt()
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