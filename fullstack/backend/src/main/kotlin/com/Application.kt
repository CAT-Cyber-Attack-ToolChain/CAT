package com

import com.plugins.configurePlugins
import com.plugins.configureRouting
import io.ktor.server.application.*


fun main(args: Array<String>): Unit =
        io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
  configurePlugins()
  configureRouting()
}
