import io.ktor.server.netty.*
import io.ktor.server.application.*
import model.ShoppingListItem

val shoppingList = mutableListOf(
    ShoppingListItem("Cucumbers 🥒", 1),
    ShoppingListItem("Tomatoes 🍅", 2),
    ShoppingListItem("Orange Juice 🍊", 3)
)

fun main(args: Array<String>): Unit = EngineMain.main(args)



fun Application.module() {
    // main calls this func
    configureRouting()
    configurePlugins()
}
