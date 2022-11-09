package com.plugins

import com.attackAgent.AttackAgent
import com.attackAgent.RandomAttackAgent
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.SimulationRoutes() {

    route("/simulation/random") {
        get {
            val random : AttackAgent = RandomAttackAgent()
            random.attack()
            call.respond(random.returnPath())
        }
    }
}
