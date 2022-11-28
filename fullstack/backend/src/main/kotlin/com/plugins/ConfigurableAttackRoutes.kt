package com.plugins

import com.attackAgent.AttackAgent
import com.attackAgent.RandomAttackAgent
import com.attackAgent.RealAttackAgent
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.request.*


fun Route.ConfigurableAttackRouting() {

    route("/attack/custom") {
            post {
                val jsonText = call.receiveText().replace("\\", "")
                println(jsonText.toString())
                call.respond("{}")
            }
        }
}