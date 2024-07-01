package it.gmstyle.getit.services.helpers

import android.util.Log
import com.google.ai.client.generativeai.type.Schema
import com.google.ai.client.generativeai.type.Tool
import com.google.ai.client.generativeai.type.defineFunction
import it.gmstyle.getit.data.entities.ListItem
import it.gmstyle.getit.data.entities.ShoppingList
import it.gmstyle.getit.data.entities.ShoppingListWithItems
import it.gmstyle.getit.data.repositories.ShoppingListRepository
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import org.json.JSONArray
import org.json.JSONObject

class GeminiToolsHelper(
    private val shoppingListRepository: ShoppingListRepository
) {
    private val TAG = "GeminiToolsHelper"
    /// FUNZIONI CHE VENGONO ESEGUITE DALLE FUNZIONI DI SUPPORTO DEL MODELLO GENERATIVO
    // Crea una lista della spesa con il nome specificato
    private suspend fun createList(listName: String): JSONObject {
        val listToCreate = ShoppingList(name = listName.trim())
        val listId = shoppingListRepository.insertList(listToCreate)
        val jsonResult = JSONObject().put("listId", listId.toString())
        Log.d(TAG, "createList: $jsonResult")
        return jsonResult
    }

    // Aggiunge un elemento alla lista della spesa con l'ID specificato
    private suspend fun addItemToList(listId: String, name: String): JSONObject {
        val item = ListItem(listId = listId.toInt(), name = name)
        val itemId = shoppingListRepository.insertItem(item)
        val jsonresult = JSONObject().put("itemId", itemId)
        Log.d(TAG, "addItemToList: $jsonresult")
        return jsonresult
    }
    // Aggiunge più elementi alla lista della spesa con l'ID specificato
    private suspend fun addItemsToList(listId: String, names: String): JSONObject {
        val savedItemsIds = mutableListOf<Int>()
        val list = names.split(",")
        list.forEach { name ->
            val json = addItemToList(listId, name.trim())
            savedItemsIds.add(json.getInt("itemId"))
        }
        val jsonResult = JSONObject().put("itemIds", savedItemsIds)
        Log.d(TAG, "addItemsToList: $jsonResult")
        return jsonResult
    }

    // Elimina un elemento dalla lista della spesa con l'ID specificato
    private suspend fun deleteItemFromList(listId: String, name: String): JSONObject {
        val list = shoppingListRepository.getListById(listId.toInt())
        val item = list.items.find { it.name == name }
        val jsonResult = JSONObject()
        if (item == null) {
            jsonResult.put("message", "Elemento non trovato nella lista della spesa")
            return jsonResult
        }
        shoppingListRepository.deleteItem(item)
        jsonResult.put("message", "Elemento eliminato con successo")
        Log.d(TAG, "deleteItemFromList: $jsonResult")
        return jsonResult
    }
    // Elimina tutti gli elementi dalla lista della spesa con l'ID specificato
    private suspend fun deleteItemsFromList(listId: String, names: String): JSONObject {
        val list = names.split(",")
        list.forEach { name ->
            deleteItemFromList(listId, name)
        }
        val jsonResult = JSONObject().put("message", "Elementi eliminati con successo")
        Log.d(TAG, "deleteItemsFromList: $jsonResult")
        return jsonResult
    }

    private suspend fun updateItem(listId: String, itemId: String, name: String, completed: Boolean): JSONObject {
        val item = ListItem(id = itemId.toInt(), listId = listId.toInt(), name = name, completed = completed)
        shoppingListRepository.updateItem(item)
        val jsonResult = JSONObject().put("message", "Elemento aggiornato con successo")
        Log.d(TAG, "updateItem: $jsonResult")
        return jsonResult
    }

    private suspend fun updateItems(listId: String, items: String, completed: Boolean): JSONObject {
        val list = shoppingListRepository.getListById(listId.toInt())
        val itemsList = items.split(",")
        itemsList.forEach { itemName ->
            val item = list.items.find { it.name == itemName }
            if (item != null) {
                updateItem(listId, item.id.toString(), item.name, completed)
            }
        }
        val jsonResult = JSONObject().put("updatedList", Json.encodeToJsonElement<ShoppingListWithItems>(list))
        Log.d(TAG, "updateItems: $jsonResult")
        return jsonResult

    }

    private suspend fun getListByName(name: String) : JSONObject {
        val list = shoppingListRepository.getListByName(name)
            ?: return JSONObject().put("message", "Not found")
        val jsonArray = JSONArray()
        list.items.forEach { item ->
            jsonArray.put(JSONObject()
                .put("id", item.id)
                .put("name", item.name)
                .put("completed", item.completed))
        }

        val jsonResult = JSONObject()
            .put("listId", list.list.id)
            .put("listName", list.list.name)
            .put("items", jsonArray)
        Log.d(TAG, "getListByName: $jsonResult")
        return jsonResult
    }

    private suspend fun getAllLists(fake: String) : JSONObject {
        val lists = shoppingListRepository.getLists()
        val listsArray = JSONArray()
        lists.forEach { list ->
            val jsonList = JSONObject()
                .put("listId", list.list.id)
                .put("listName", list.list.name)
            val itemsArray = JSONArray()
            list.items.forEach { item ->
                itemsArray.put(JSONObject()
                    .put("id", item.id)
                    .put("name", item.name)
                    .put("completed", item.completed))
            }
            jsonList.put("listItems", itemsArray)
            listsArray.put(jsonList)
        }
        val jsonResult = JSONObject().put("lists", listsArray)
        Log.d(TAG, "getAllLists: $jsonResult")
        return jsonResult

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
    // Definizione della funzione "updateItems" per il modello generativo
    //TODO: da verificare
    private val updateItemsTool = defineFunction(
        name = "updateItems",
        description = "Aggiorna più elementi della lista della spesa esistente. L'ID della lista della spesa può arrivare dalle funzioni 'createList', 'getListByListName' o 'getAllLists'. Il campo si chiama 'listId' nel JSON restituito.",
        Schema.str("listId", "L'ID della lista della spesa, ottenuto dal campo 'listId' del JSON restituito dalla funzione 'createList' o 'getListByListName' o 'getAllLists'"),
        Schema.str("items", "Gli elementi da aggiornare nella lista della spesa separati da virgola. Ogni elemento è composto da ID, nome e completato separati da due punti"),
        Schema.bool("completed", "Se l'elemento è stato completato o meno")
    ){ listId, items, completed ->
        updateItems(listId, items, completed)
    }
    private val getListByListName = defineFunction(
        name = "getListByListName",
        description = "Recupera una lista dal suo nome. La lista restituita contiene l'ID della lista, il nome della lista e gli elementi della lista.",
        Schema.str("listName", "Il nome della lista da trovare"),
    ) { listName ->
        getListByName(listName)
    }

    private val getAllListsTool = defineFunction(
        name = "getAllLists",
        description = "Recupera tutte le liste della spesa quando l'utente te lo chiede. " +
                "Non serve alcun parametro. " +
                "Alla chiave lists restituisce un array di oggetti JSON, ognuno dei quali rappresenta una lista della spesa con i suoi elementi." +
                "Le chiavi di ogni oggetto JSON sono listId, listName e listItems. " +
                "Il parametro listId ti serve per aggiungere o eliminare elementi da una lista della spesa sfruttando le funzioni 'addItemsToList' e 'deleteItemsFromList' se l'utente te lo chiede durante la conversazione.",
        Schema.str("fake", "Parametro fake per evitare errori di validazione del modello generativo")
    ) {
        getAllLists(it)
    }

    private val _tool = Tool(listOf(
        createListTool,
        addItemsToListTool,
        deleteItemsFromListTool,
        getListByListName,
        getAllListsTool,
        updateItemsTool
    ))

    val tools get() = listOf(_tool)

}