package com

import com.ktor.Components.configureObjects
import com.ktor.configurePlugins
import com.ktor.configureRouting
import io.ktor.server.application.*

fun main(args: Array<String>): Unit =
        io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
  configureObjects()
  configurePlugins()
  configureRouting()

}
