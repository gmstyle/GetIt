package it.gmstyle.getit.data.repositories

import it.gmstyle.getit.data.dao.ShoppingItemDao
import it.gmstyle.getit.data.dao.ShoppingListDao
import it.gmstyle.getit.data.entities.ShoppingItem
import it.gmstyle.getit.data.entities.ShoppingList
import kotlinx.coroutines.flow.Flow

class ShoppingListRepository(
    private val listDao: ShoppingListDao, private val itemDao: ShoppingItemDao) {

    val lists: Flow<List<ShoppingList>> = listDao.getAll()

    fun getListById(listId: Int): Flow<ShoppingList> = listDao.getById(listId)
    fun getItemsByListId(listId: Int): Flow<List<ShoppingItem>> = itemDao.getAllByListId(listId)
    suspend fun insertList(shoppingList: ShoppingList): Long = listDao.insert(shoppingList)

    suspend fun deleteList(shoppingList: ShoppingList) = listDao.delete(shoppingList)

    suspend fun updateList(shoppingList: ShoppingList) = listDao.update(shoppingList)

    suspend fun insertItem(shoppingItem: ShoppingItem): Long = itemDao.insert(shoppingItem)

    suspend fun deleteItem(shoppingItem: ShoppingItem) = itemDao.delete(shoppingItem)

    suspend fun updateItem(shoppingItem: ShoppingItem) = itemDao.update(shoppingItem)
}