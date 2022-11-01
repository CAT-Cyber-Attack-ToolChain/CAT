package routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.example.model.*
import com.example.controller.*
import com.example.shoppingList

fun Route.ShoppingRouting() {
    val example = """[{ "data": { "id": "one", "label": "Node 1" }}, 
        { "data": { "id": "two", "label": "Node 2" }},
        { "data": { "source": "one", "target": "two", "label": "Edge from Node1 to Node2" } }]"""

    val cur = System.getProperty("user.dir")
    val mulvalInput = MulvalInput("$cur/../../mulval/testcases/3host/input.P")
    val mulvalOutput = AttackGraphOutput("$cur/../../output")
    println("OUTPUT_PATH: $cur/../../output")

    val mulvalController = MulvalController(mulvalInput, mulvalOutput)

    route(ShoppingListItem.path) {
        get {
            // generate the attack graph using mulVAL
            mulvalController.generateGraph()
            val neo4JController = Neo4JController(mulvalOutput)
            // upload the graph to Neo4j
            if (mulvalController.getGenerated()) {
                neo4JController.update()
            }
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
