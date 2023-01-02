package com.plugins

import com.attackAgent.AttackAgent
import com.attackAgent.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

import com.attackAgent.CustomAttackAgent

fun Route.SimulationRouting() {

    route("/simulation/random") {
        get {
            call.respond(attackAndGetPath(RandomAttackAgent()))
        }
    }

    route("/simulation/real") {
        get {
            call.respond(attackAndGetPath(RealAttackAgent()))
        }
    }

    route("/simulation/wannacry") {
        get {
            call.respond(attackAndGetPath(WannacryAttackAgent()))
        }
    }

    route("/simulation/revil") {
        get {
            call.respond(attackAndGetPath(REvilAttackAgent()))
        }
    }

    route("/simulation/t9000") {
        get {
            call.respond(attackAndGetPath(T9000AttackAgent()))
        }
    }

    route("/simulation/synack") {
        get {
            call.respond(attackAndGetPath(SynAckAttackAgent()))
        }
    }

    route("/simulation/wiper") {
        get {
            call.respond(attackAndGetPath(WiperAttackAgent()))
        }
    }

    route("/simulation/custom") {
        get {
            call.respond(attackAndGetPath(CustomAttackAgent.AGENT))
        }
    }
}

private fun attackAndGetPath(agent: AttackAgent): MutableList<Pair<String, String>> {
    val response = AttackAgent.getAttackResponse(agent)
    val success = response.second
    val path = response.first
    if (success)
        return path
    return mutableListOf()
}
