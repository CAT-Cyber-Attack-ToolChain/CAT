package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.example.model.ShoppingListItem
import com.example.shoppingList

fun Route.ShoppingRouting() {
    val example = """[{ "data": { "id": "one", "label": "Node 1" }, "position": { "x": 30, "y": 30 } }, 
        { "data": { "id": "two", "label": "Node 2" }, "position": { "x": 100, "y": 50 } },
        { "data": { "source": "one", "target": "two", "label": "Edge from Node1 to Node2" } }]"""

    route(ShoppingListItem.path) {
        get {
            call.respond(example)
        }
        post {
            shoppingList += call.receive<ShoppingListItem>()
            call.respond(HttpStatusCode.OK)
        }
        delete("/{id}") {
            val id = call.parameters["id"]?.toInt() ?: error("Invalid delete request")
            shoppingList.removeIf { it.id == id }
            call.respond(HttpStatusCode.OK)
        }
    }
}
