package com.ktor

import com.ktor.routes.ConfigurableAttackRouting
import com.ktor.routes.GraphGenRouting
import com.ktor.routes.MetricsRouting
import com.ktor.routes.SimulationRouting
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
