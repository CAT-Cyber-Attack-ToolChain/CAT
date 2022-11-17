package com

import com.graph.TopologyGraph
import com.model.MulvalInput
import io.ktor.server.application.*
import com.plugins.configurePlugins
import com.plugins.configureRouting


fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    TopologyGraph.build(MulvalInput("./../../mulval/testcases/3host/input.P"))
    configurePlugins()
    configureRouting()
}
