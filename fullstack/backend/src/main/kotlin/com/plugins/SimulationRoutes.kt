package com.plugins

import com.attackAgent.AttackAgent
import com.attackAgent.RandomAttackAgent
import com.attackAgent.RealAttackAgent
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.SimulationRouting() {

    route("/simulation/random") {
        get {
            val random : AttackAgent = RandomAttackAgent()
            random.attack()
            call.respond(random.returnPath())
        }
    }

    route("/simulation/real") {
        get {
            val random : AttackAgent = RealAttackAgent()
            random.attack()
            call.respond(random.returnPath())
        }
    }
}
