package it.gmstyle.getit.data.entities

import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.serialization.Serializable

@Serializable
data class ShoppingListWithItems(
    @Embedded val list: ShoppingList,
    @Relation(
        parentColumn = "id",
        entityColumn = "listId"
    )
    val items: List<ListItem>
)
