package com.model

import java.io.FileReader

object NetworkConfiguration {

  private val conf = FileReader(System.getProperty("user.dir") + ".conf").readText()
  val neo4j: Configuration
  val backend: Configuration
  val frontend: Configuration

  init {
    neo4j = getConfigFor("neo4j")
    backend = getConfigFor("backend")
    frontend = getConfigFor("frontend")
  }

  private fun getConfigFor (service: String): Configuration {
    val ptn = Regex("$service:(localhost|[0-9]*.[0-9]*.[0-9]*.[0-9]*):([0-9]*)")
    val result = ptn.find(conf)
    val (address, port) = result!!.destructured
    return Configuration(address, port.toInt())
  }
}

class Configuration(val address: String, val port: Int){}
