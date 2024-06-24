package it.gmstyle.getit.services

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.Schema
import com.google.ai.client.generativeai.type.Tool
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.defineFunction
import com.google.ai.client.generativeai.type.generationConfig
import it.gmstyle.getit.BuildConfig
import it.gmstyle.getit.data.entities.ListItem
import it.gmstyle.getit.data.entities.ShoppingList
import it.gmstyle.getit.data.repositories.ShoppingListRepository
import org.json.JSONObject

class ChatService(
    private val shoppingListRepository: ShoppingListRepository
) {

    // Crea una lista della spesa con il nome specificato
    private suspend fun createList(listName: String): JSONObject {
        val listToCreate = ShoppingList(name = listName)
        val listId = shoppingListRepository.insertList(listToCreate)
        return JSONObject().put("listId", listId.toString())
    }
    // Definizione della funzione "createList" per il modello generativo
    private val createListTool = defineFunction(
        name = "createList",
        description = "Crea una lista della spesa su richiesta dell'utente",
        Schema.str("listName", "Il nome della lista della spesa"),
    ){ listName ->
        createList(listName)
    }

    // Aggiunge un elemento alla lista della spesa con l'ID specificato
    private suspend fun addItemToList(listId: String, name: String): JSONObject {
        val item = ListItem(listId = listId.toInt(), name = name)
        val itemId = shoppingListRepository.insertItem(item)
        return JSONObject().put("itemId", itemId)
    }
    // Definizione della funzione "addItemsToList" per il modello generativo
    private suspend fun addItemsToList(listId: String, names: String): JSONObject {
        val savedItemsIds = mutableListOf<Int>()
        val list = names.split(",")
      list.forEach { name ->
          val json = addItemToList(listId, name)
            savedItemsIds.add(json.getInt("itemId"))
      }
        return JSONObject().put("itemIds", savedItemsIds)
    }

    private val addItemsToListTool = defineFunction(
        name = "addItemsToList",
        description = "Aggiunge più elementi a una lista della spesa esistente. L'ID della lista della spesa è ottenuto dalla funzione 'createList' ed è contenuto nel campo 'listId' del JSON restituito.",
        Schema.str("listId", "L'ID della lista della spesa, ottenuto dal campo 'listId' del JSON restituito dalla funzione 'createList'"),
        Schema.str("names", "I nomi degli elementi da aggiungere alla lista della spesa separati da virgola"),
    ){ listId, names ->
        addItemsToList(listId, names)
    }

    private val _generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.geminiApiKey,
        generationConfig = generationConfig {
            temperature = 0.7f
            topK = 64
            topP = 0.95f
            maxOutputTokens = 8192
        },
        tools = listOf(
            Tool(listOf(
                createListTool,
                addItemsToListTool
            )),

        ),
        systemInstruction = content {
            text("Sei un assistente per la gestione delle liste della spesa e sei parte integrante dell' applicazione mobile android che si chiama \"Get it!\". L'utente ha bisogno di creare una lista della spesa per cucinare un piatto specifico o un menu a base specifica di qualcosa, ad esempio di sole verdure piuttosto che di carne o di pesce, quindi tu sei in grado di aiutarlo a comporre la lista della spesa predisponendo gli ingredienti necessari da acquistare. L'utente, invece, potrebbe anche semplicemente voler usare le funzionalità dell'applicazione per creare e modificare la lista della spesa settimanale che ha bisogno di fare per la casa e la famiglia. Dovresti poter essere in grado di aiutarlo magari con dei reminder o delle notifiche locali. Non essere molto prolisso nelle risposte, cerca di essere conciso e comprendioso allo stesso tempo.")        }
    )
    private val chatHistory = listOf(
        content("user") {
            text("ciao, ho bisogno di creare una lista della spesa per gli acquisti settimanali per la casa e la famiglia")
        },
        content("model") {
            text("Ciao! Sono felice di aiutarti a creare la tua lista della spesa settimanale. 😊\n\nPer iniziare, dimmi:\n\n* **Quanti persone siete in famiglia?** Questo mi aiuterà a stimare le quantità di cibo necessarie.\n* **Quali sono le vostre preferenze alimentari?** Ad esempio, siete vegetariani, vegani, avete allergie o intolleranze? \n* **Cosa avete già in dispensa e nel frigorifero?** Questo mi aiuterà a evitare di farti comprare cose che hai già.\n* **Ci sono piatti specifici che vorresti cucinare questa settimana?** Se sì, quali?\n* **Hai bisogno di acquistare prodotti freschi, prodotti da forno, prodotti da forno, bevande, snack, prodotti per la pulizia o altri articoli?**\n\nFornendomi queste informazioni, posso aiutarti a creare una lista della spesa completa e personalizzata per la tua famiglia. \n\nInoltre, posso impostare dei reminder per te, in modo che tu non dimentichi di acquistare nulla! \n")
        },
        content("user") {
            text("siamo in 3, due adulti ed una bambina di 2 anni. mangiamo tutto senza problemi. ho bisogno di alimenti basilari e prodotti per l'igiene e la pulizia del bagno")
        },
        content("model") {
            text("Perfetto! 👨‍👩‍👧  Allora iniziamo a creare la tua lista della spesa settimanale. \n\n**Alimenti basilari:**\n\n* **Frutta e verdura:** \n    * Mele (2 kg)\n    * Banane (1 kg)\n    * Aranci (1 kg)\n    * Pomodori (1 kg)\n    * Cetrioli (1 kg)\n    * Lattuga (1 cespo)\n    * Spinaci (1 confezione)\n    * Carote (1 kg)\n    * Patate (2 kg)\n    * Cipolle (1 kg)\n    * Aglio (1 spicchio)\n* **Pane e cereali:**\n    * Pane (1 pagnotta)\n    * Riso (1 kg)\n    * Pasta (1 kg)\n* **Latticini:**\n    * Latte (1 litro)\n    * Yogurt (2 confezioni)\n    * Formaggio (200 gr)\n* **Carne e pesce:**\n    * Pollo (1 kg)\n    * Manzo (500 gr)\n    * Salmone (2 confezioni)\n* **Uova:** \n    * Uova (10)\n* **Altri:**\n    * Olio d'oliva (1 litro)\n    * Aceto (1 bottiglia)\n    * Sale e pepe (1 confezione)\n    * Zucchero (1 kg)\n    * Caffè (1 confezione)\n    * Tè (1 confezione)\n\n**Prodotti per l'igiene e la pulizia del bagno:**\n\n* Sapone per le mani (1 confezione)\n* Shampoo (1 bottiglia)\n* Balsamo (1 bottiglia)\n* Dentifricio (1 tubetto)\n* Spazzolino da denti (2)\n* Carta igienica (1 confezione)\n* Detersivo per i piatti (1 bottiglia)\n* Detersivo per il bucato (1 bottiglia)\n* Sbiancante (1 bottiglia)\n* Candeggina (1 bottiglia)\n* Spugne (1 confezione)\n\n**Consigli:**\n\n* Puoi adattare questa lista in base alle vostre preferenze e alle vostre esigenze. \n* Se hai bisogno di acquistare prodotti specifici per la bambina di 2 anni, come pannolini o alimenti per l'infanzia, aggiungili alla lista.\n* Puoi anche aggiungere snack o bevande alla lista, in base alle vostre preferenze. \n* Ricorda di controllare la data di scadenza dei prodotti che hai già in casa prima di fare la spesa.\n\n**Reminder:**\n\n* Ti posso impostare dei reminder per la tua lista della spesa, in modo che tu non dimentichi di acquistare nulla! \n* Posso anche inviarti delle notifiche locali per ricordarti di fare la spesa entro una certa data.\n\nFammi sapere se hai bisogno di aggiungere o modificare qualcosa nella lista! 😊\n")
        },
        content("user") {
            text("bene così grazie mille")
        },
        content("model") {
            text("Di niente! Sono felice di averti aiutato. 😊\n\nSe hai bisogno di aggiungere o modificare qualcosa alla lista, o se hai bisogno di aiuto per organizzare la tua spesa, non esitare a chiedermelo. \n\nPosso anche impostare dei reminder per te, in modo che tu non dimentichi di acquistare nulla! \n\nBuon shopping! 🛒\n")
        },
    )

    val generativeModel get() = _generativeModel

    private val chat = _generativeModel.startChat(chatHistory)

    suspend fun sendMessage(content: Content): GenerateContentResponse {
        return chat.sendMessage(content)
    }
}