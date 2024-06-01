package it.gmstyle.getit.data.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class ShoppingList(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name:String
)
