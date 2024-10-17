package it.gmstyle.getit.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    indices = [Index(value = ["name"], unique = true)]
)
@Serializable
data class ShoppingList(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name:String
)
