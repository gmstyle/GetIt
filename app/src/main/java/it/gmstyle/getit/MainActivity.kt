package it.gmstyle.getit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import it.gmstyle.getit.compose.navigation.ShoppingListApp
import it.gmstyle.getit.ui.theme.GetItTheme

class MainActivity : ComponentActivity() {

    private lateinit var myApplication: MyApplication
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myApplication = application as MyApplication
        enableEdgeToEdge()
        setContent {
            GetItTheme {
                ShoppingListApp()
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GetItTheme {
        Greeting("Android")
    }
}
