package it.gmstyle.getit.data.repositories

import it.gmstyle.getit.data.dao.ListItemDao
import it.gmstyle.getit.data.dao.ShoppingListDao
import it.gmstyle.getit.data.entities.ListItem
import it.gmstyle.getit.data.entities.ShoppingList
import it.gmstyle.getit.data.entities.ShoppingListWithItems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class ShoppingListRepository(
    private val listDao: ShoppingListDao, private val itemDao: ListItemDao) {

    val lists: Flow<List<ShoppingListWithItems>> = listDao.getAllWithItems()
    fun getListById(listId: Int): Flow<ShoppingListWithItems> = listDao.getWithItemsByListId(listId)
    suspend fun insertList(shoppingList: ShoppingList): Long = listDao.insert(shoppingList)

    suspend fun deleteList(shoppingList: ShoppingList) = listDao.delete(shoppingList)

    suspend fun updateList(shoppingList: ShoppingList) = listDao.update(shoppingList)

    suspend fun insertItem(listItem: ListItem): Long = itemDao.insert(listItem)

    suspend fun deleteItem(listItem: ListItem) = itemDao.delete(listItem)

    suspend fun deleteAllItemsByListId(listId: Int) = itemDao.deleteAllByListId(listId)

    suspend fun updateItem(listItem: ListItem) = itemDao.update(listItem)
}