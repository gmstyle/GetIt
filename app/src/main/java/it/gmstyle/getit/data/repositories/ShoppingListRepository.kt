package it.gmstyle.getit.data.repositories

import it.gmstyle.getit.data.dao.ListItemDao
import it.gmstyle.getit.data.dao.ShoppingListDao
import it.gmstyle.getit.data.entities.ListItem
import it.gmstyle.getit.data.entities.ShoppingList
import kotlinx.coroutines.flow.Flow

class ShoppingListRepository(
    private val listDao: ShoppingListDao, private val itemDao: ListItemDao) {

    val lists: Flow<List<ShoppingList>> = listDao.getAll()

    fun getListById(listId: Int): Flow<ShoppingList> = listDao.getById(listId)
    fun getItemsByListId(listId: Int): Flow<List<ListItem>> = itemDao.getAllByListId(listId)
    suspend fun insertList(shoppingList: ShoppingList): Long = listDao.insert(shoppingList)

    suspend fun deleteList(shoppingList: ShoppingList) = listDao.delete(shoppingList)

    suspend fun updateList(shoppingList: ShoppingList) = listDao.update(shoppingList)

    suspend fun insertItem(listItem: ListItem): Long = itemDao.insert(listItem)

    suspend fun deleteItem(listItem: ListItem) = itemDao.delete(listItem)

    suspend fun updateItem(listItem: ListItem) = itemDao.update(listItem)
}