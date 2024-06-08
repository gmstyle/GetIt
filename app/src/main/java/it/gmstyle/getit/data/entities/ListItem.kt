package it.gmstyle.getit.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class ListItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val completed: Boolean = false,
    val listId: Int,
)
