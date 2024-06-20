package it.gmstyle.getit.compose.screens.listsscreen.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import it.gmstyle.getit.data.entities.ListItem
import it.gmstyle.getit.data.entities.ShoppingListWithItems

@Composable
fun ShoppingListSticker(
    shoppingList: ShoppingListWithItems,
    onViewList: () -> Unit,
    onDeleteList: () -> Unit
) {

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onViewList() },
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = shoppingList.list.name,
                    style = MaterialTheme.typography.headlineSmall
                )
                // prendi tutti gli items non completati
                val items = shoppingList.items.filter { !it.completed }
                BuildFirstTenItemsWithEllipsis(items)
            }
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onDeleteList) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

@Composable
private fun BuildFirstTenItemsWithEllipsis(items: List<ListItem>) {
    if (items.size <= 10) {
        items.forEach {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Checkbox(
                    enabled = false,
                    checked = it.completed,
                    onCheckedChange = null)
                Text(
                    text = it.name,
                    color = Color.Gray
                )
            }
        }
    } else {
        for (i in 0 until 10) {
            Text(
                text = items[i].name,
                color = Color.Gray
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "",
                tint = Color.Gray
            )
            Text(
                "${items.size - 10} more elements",
                color = Color.Gray
            )

        }
    }
}