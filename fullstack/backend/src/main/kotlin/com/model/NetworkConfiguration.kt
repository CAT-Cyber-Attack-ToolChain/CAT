package com.model

import java.io.FileReader

object NetworkConfiguration {

  private lateinit var conf: String
  lateinit var neo4j: Configuration
  lateinit var backend: Configuration
  lateinit var frontend: Configuration


  fun init() {
    conf = FileReader( "./.conf").readText()
    neo4j = getConfigFor("neo4j")
    backend = getConfigFor("backend")
    frontend = getConfigFor("frontend")
  }

  //TODO: this indexing is jank, will fix at some point
  private fun getConfigFor (service: String): Configuration {
    val ptn = Regex("$service:(localhost|[0-9]+.[0-9]+.[0-9]+.[0-9]+|(([A-Z]*[a-z]*[0-9]*)+.)+([A-Z]*[a-z]*)+):([0-9]*)")
    val result = ptn.find(conf)
    val groups = result!!.destructured.toList()
    return Configuration(groups[0], groups[4].toInt())
  }
}

class Configuration(val address: String, val port: Int){}
