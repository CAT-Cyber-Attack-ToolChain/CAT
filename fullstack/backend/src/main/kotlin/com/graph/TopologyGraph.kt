package com.graph

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.FileWriter
import java.io.FileReader

data class Hacl(val m1: String, val m2: String, val protocol: String, val port: String) {
  override fun toString(): String {
    return "hacl($m1, $m2, $protocol, $port)."
  }
}

@kotlinx.serialization.Serializable
data class Account(val user: String, val machine: String, val privilege: String) {
  override fun toString(): String {
    return "hasAccount($user, $machine, $privilege).\ninCompetent($user)."
  }
}

@kotlinx.serialization.Serializable
data class Service(
        val machine: String,
        val application: String,
        val protocol: String,
        val port: String,
        val user: String
) {
  override fun toString(): String {
    return "networkServiceInfo($machine, $application, $protocol, $port, $user)."
  }
}

@kotlinx.serialization.Serializable
data class Clause(val name: String, val args: List<String>) {
  override fun toString(): String {
    val sb = StringBuilder()
    sb.append(name)
    sb.append("(")
    if (args.isNotEmpty()) {
      sb.append(args[0])
    }
    for (i in 1 until args.size) {
      sb.append(",")
      sb.append(args[i])
    }
    sb.append(").")
    return sb.toString()
  }
}

@kotlinx.serialization.Serializable
data class Machine(val type: String, val value: String, val label: String, val name: String, val accounts: List<Account>, val services: List<Service>, val programs: List<Program>, val vulnerabilities: List<Vulnerability>, val other: List<Clause>) {
  override fun toString(): String {
    val sb = StringBuilder()
    var flag = false
    if (accounts.isNotEmpty()) {
      sb.append(accounts.joinToString("\n"))
      sb.append("\n")
      flag = true
    }
    if (services.isNotEmpty()) {
      sb.append(services.joinToString("\n"))
      sb.append("\n")
      flag = true
    }
    if (programs.isNotEmpty()) {
      sb.append(programs.joinToString("\n"))
      sb.append("\n")
      flag = true
    }
    if (vulnerabilities.isNotEmpty()) {
      sb.append(vulnerabilities.joinToString("\n"))
      sb.append("\n")
      flag = true
    }
    if (other.isNotEmpty()) {
      sb.append(other.joinToString("\n"))
      sb.append("\n")
      flag = true
    }
    if (flag) {
      sb.append("\n")
    }
    return sb.toString()
  }
}

@kotlinx.serialization.Serializable
data class Program(val machine: String, val application: String, val privilege: String) {
  override fun toString(): String {
    val sb = StringBuilder()
    sb.append("clientApplication($application).\n")
    sb.append("setuidProgramInfo($machine, $application, $privilege).\n")
    return sb.toString()
  }
}

@Serializable
data class Vulnerability(val name: String, val machine: String, val application: String, val locality: String, val type: String, val cvss: String) {
  override fun toString(): String {
    val sb = StringBuilder()
    sb.append("vulProperty($name, $locality, $type).\n")
    sb.append("cvss($name, $cvss).\n")
    sb.append("vulExists($machine, $name, $application).")
    return sb.toString()
  }
}

class TopologyGraph {
  companion object {
    fun build(machines: String, routers: String, links: String, outputFile: String) {
      val writer = FileWriter(outputFile)
      val sb = StringBuilder()
      sb.append("attackerLocated(internet).\n")
      sb.append("attackGoal(execCode(_,_)).\n")
      //Parse machines and construct machine objects
      //Parse routers and construct router objects
      //Parse edges and add connections to routers
      //Build input.P from machines and routers
      println(machines)
      for (m in Json.decodeFromString<List<Machine>>(machines)) {
        sb.append(m)
      }
      println(sb.toString())
      writer.write(sb.toString())
      writer.close()
    }
  }
}

fun main() {
  TopologyGraph.build("[${FileReader("machine.json").readText()}, ${FileReader("webServer.json").readText()}, ${FileReader("workStation.json").readText()}]", "[]", "[]", "output.txt")
}
