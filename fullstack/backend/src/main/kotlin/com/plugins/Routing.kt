package com.plugins

import io.ktor.server.routing.*
import io.ktor.server.application.*


fun Application.configureRouting() {

    routing {
        GraphGenRouting()
        MetricsRouting()
        SimulationRouting()
        ConfigurableAttackRouting()
    }
}
