package it.gmstyle.getit.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import it.gmstyle.getit.data.entities.ListItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ListItemDao {

    @Query("SELECT * FROM ListItem where listId = :listId")
    fun getAllByListId(listId: Int): Flow<List<ListItem>>

    @Insert
    suspend fun insert(listItem: ListItem): Long

    @Delete
    suspend fun delete(listItem: ListItem)

    @Update
    suspend fun update(listItem: ListItem)

    // delete all items from a list
    @Query("DELETE FROM ListItem where listId = :listId")
    suspend fun deleteAllByListId(listId: Int)
}