package it.gmstyle.getit.data.repositories

import it.gmstyle.getit.data.dao.ListItemDao
import it.gmstyle.getit.data.dao.ShoppingListDao
import it.gmstyle.getit.data.entities.ListItem
import it.gmstyle.getit.data.entities.ShoppingList
import it.gmstyle.getit.data.entities.ShoppingListWithItems
import kotlinx.coroutines.flow.Flow

class ShoppingListRepository(
    private val listDao: ShoppingListDao, private val itemDao: ListItemDao) {

    val listsFlow: Flow<List<ShoppingListWithItems>> = listDao.getAllWithItemsFlow()
    suspend fun getLists(): List<ShoppingListWithItems> = listDao.getAllWithItems()
    fun getListByIdFlow(listId: Int): Flow<ShoppingListWithItems> = listDao.getWithItemsByListIdFlow(listId)
    suspend fun getListById(listId: Int): ShoppingListWithItems = listDao.getWithItemsByListId(listId)

    suspend fun insertList(shoppingList: ShoppingList): Long = listDao.insert(shoppingList)

    suspend fun deleteList(shoppingList: ShoppingList) = listDao.delete(shoppingList)

    suspend fun updateList(shoppingList: ShoppingList) = listDao.update(shoppingList)

    suspend fun insertItem(listItem: ListItem): Long = itemDao.insert(listItem)

    suspend fun deleteItem(listItem: ListItem) = itemDao.delete(listItem)

    suspend fun deleteAllItemsByListId(listId: Int) = itemDao.deleteAllByListId(listId)

    suspend fun updateItem(listItem: ListItem) = itemDao.update(listItem)
    suspend fun getListByName(name: String): ShoppingListWithItems? = listDao.getWithItemsByListName(name)
    suspend fun getItemByListIdAndId(listId: Int, id: Int): ListItem? = itemDao.getByListIdAndId(listId, id)
}