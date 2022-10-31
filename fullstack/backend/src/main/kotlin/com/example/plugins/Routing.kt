package com.example.plugins

import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import routes.ShoppingRouting

fun Application.configureRouting() {

    routing {
        ShoppingRouting()
    }
}
