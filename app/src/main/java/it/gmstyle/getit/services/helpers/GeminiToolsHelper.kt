package it.gmstyle.getit.services.helpers

import android.util.Log
import com.google.ai.client.generativeai.type.Schema
import com.google.ai.client.generativeai.type.Tool
import com.google.ai.client.generativeai.type.defineFunction
import it.gmstyle.getit.data.entities.ListItem
import it.gmstyle.getit.data.entities.ShoppingList
import it.gmstyle.getit.data.repositories.ShoppingListRepository
import org.json.JSONObject

class GeminiToolsHelper(
    private val shoppingListRepository: ShoppingListRepository
) {
    /// FUNZIONI CHE VENGONO ESEGUITE DALLE FUNZIONI DI SUPPORTO DEL MODELLO GENERATIVO
    // Crea una lista della spesa con il nome specificato
    private suspend fun createList(listName: String): JSONObject {
        val listToCreate = ShoppingList(name = listName)
        val listId = shoppingListRepository.insertList(listToCreate)
        return JSONObject().put("listId", listId.toString())
    }

    // Aggiunge un elemento alla lista della spesa con l'ID specificato
    private suspend fun addItemToList(listId: String, name: String): JSONObject {
        val item = ListItem(listId = listId.toInt(), name = name)
        val itemId = shoppingListRepository.insertItem(item)
        return JSONObject().put("itemId", itemId)
    }
    // Aggiunge più elementi alla lista della spesa con l'ID specificato
    private suspend fun addItemsToList(listId: String, names: String): JSONObject {
        val savedItemsIds = mutableListOf<Int>()
        val list = names.split(",")
        list.forEach { name ->
            val json = addItemToList(listId, name)
            savedItemsIds.add(json.getInt("itemId"))
        }
        return JSONObject().put("itemIds", savedItemsIds)
    }

    // Elimina un elemento dalla lista della spesa con l'ID specificato
    private suspend fun deleteItemFromList(listId: String, name: String): JSONObject {
        val list = shoppingListRepository.getListById(listId.toInt())
        val item = list.items.find { it.name == name }
        if (item == null) {
            return JSONObject().put("message", "Elemento non trovato nella lista della spesa")
        }
        shoppingListRepository.deleteItem(item)
        return JSONObject().put("message", "Elemento eliminato con successo")
    }
    // Elimina tutti gli elementi dalla lista della spesa con l'ID specificato
    private suspend fun deleteItemsFromList(listId: String, names: String): JSONObject {
        val list = names.split(",")
        list.forEach { name ->
            deleteItemFromList(listId, name)
        }
        return JSONObject().put("message", "Elementi eliminati con successo")
    }

    /// FUNZIONI DI SUPPORTO PER IL MODELLO GENERATIVO
    // Definizione della funzione "createList" per il modello generativo
    private val createListTool = defineFunction(
        name = "createList",
        description = "Crea una lista della spesa su richiesta dell'utente",
        Schema.str("listName", "Il nome della lista della spesa"),
    ){ listName ->
        createList(listName)
    }
    // Definizione della funzione "addItemsToList" per il modello generativo
    private val addItemsToListTool = defineFunction(
        name = "addItemsToList",
        description = "Aggiunge più elementi a una lista della spesa esistente. L'ID della lista della spesa è ottenuto dalla funzione 'createList' ed è contenuto nel campo 'listId' del JSON restituito.",
        Schema.str("listId", "L'ID della lista della spesa, ottenuto dal campo 'listId' del JSON restituito dalla funzione 'createList'"),
        Schema.str("names", "I nomi degli elementi da aggiungere alla lista della spesa separati da virgola"),
    ){ listId, names ->
        addItemsToList(listId, names)
    }
    // Definizione della funzione "deleteAllItemsFromList" per il modello generativo
    private val deleteItemsFromListTool = defineFunction(
        name = "deleteItemsFromList",
        description = "Elimina più elementi dalla lista della spesa esistente. L'ID della lista della spesa è ottenuto dalla funzione 'createList' ed è contenuto nel campo 'listId' del JSON restituito.",
        Schema.str("listId", "L'ID della lista della spesa, ottenuto dal campo 'listId' del JSON restituito dalla funzione 'createList'"),
        Schema.str("names", "I nomi degli elementi da eliminare dalla lista della spesa separati da virgola"),
    ){ listId, names ->
        deleteItemsFromList(listId, names)
    }

    private val _tool = Tool(listOf(
        createListTool,
        addItemsToListTool,
        deleteItemsFromListTool
    ))

    val tools get() = listOf(_tool)

}