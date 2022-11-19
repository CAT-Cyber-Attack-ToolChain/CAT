package com.model

import java.io.FileReader

object NetworkConfiguration {

  private val conf: String
  val neo4j: Configuration
  val backend: Configuration
  val frontend: Configuration

  private val HOSTNAME_INDEX = 2
  private val PORT_INDEX = 6
  private val PROTOCOL_INTEX = 1


  init {
    conf = FileReader( "./.conf").readText()
    neo4j = getConfigFor("neo4j")
    backend = getConfigFor("backend")
    frontend = getConfigFor("frontend")
  }

  //TODO: this indexing is jank, will fix at some point
  private fun getConfigFor (service: String): Configuration {
    val secureNeo4J = Regex.escape("neo4j+s")
    val ptn = Regex("$service:(($secureNeo4J|bolt|neo4j|https):)?(localhost|[0-9]+.[0-9]+.[0-9]+.[0-9]+|(([A-Z]*[a-z]*[0-9]*)+.)+([A-Z]*[a-z]*)+):([0-9]*)")
    val result = ptn.find(conf)
    val groups = result!!.destructured.toList()
    if (!groups[0].isEmpty())
      return Configuration(groups[HOSTNAME_INDEX], groups[PORT_INDEX].toInt(), groups[PROTOCOL_INTEX])
    return Configuration(groups[HOSTNAME_INDEX], groups[PORT_INDEX].toInt())
  }
}

class Configuration(val address: String, val port: Int, val protocol: String = "https"){
  override fun toString(): String {
    return "$protocol://$address:$port"
  }
}

fun main() {
  println(NetworkConfiguration.neo4j)
  println(NetworkConfiguration.frontend)
  println(NetworkConfiguration.backend)
}