package com.graph

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.FileWriter
import java.io.FileReader
import java.lang.Exception
import javax.crypto.Mac

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

@kotlinx.serialization.Serializable
data class Vulnerability(val name: String, val machine: String, val application: String, val locality: String, val type: String, val cvss: String) {
  override fun toString(): String {
    val sb = StringBuilder()
    sb.append("vulProperty($name, $locality, $type).\n")
    sb.append("cvss($name, $cvss).\n")
    sb.append("vulExists($machine, $name, $application).")
    return sb.toString()
  }
}

@kotlinx.serialization.Serializable
data class FirewallRule(val source: String, val dest: String, val protocol: String, val port: String) {
  fun accept(m1: String, m2: String, protocol: String, port: String): Boolean {
    if (source != "*" && source != m1) {
      return false
    }
    if (dest != "*" && dest != m2) {
      return false
    }
    if (this.protocol != "*" && this.protocol != protocol) {
      return false
    }
    if (this.port != "*" && this.port != port) {
      return false
    }
    return true
  }
}

@kotlinx.serialization.Serializable
data class Router(val type: String, val label: String, val value: String, val name: String, val rules: List<FirewallRule>) {
  var subnet: Set<String>? = null
  fun build(routers: Set<Router>, machineMap: Map<String, Router>): String {
    if (subnet == null) {
      throw Exception("Failed To Create Subnet $name")
    }
    val sb = StringBuilder()
    for (machine in subnet!!) {
      sb.append("inSubnet($machine, ${name}Subnet).\n")
    }
    for (rule in rules) {
      if (rule.source == "*" && rule.dest == "*") {
        val m1 = getRepresentative() ?: break
        for (r2 in routers) {
          val m2 = r2.getRepresentative() ?: continue
          if (r2.accept(m1, m2, rule.protocol, rule.port)) {
            sb.append("hacl($m1, $m2, ${rule.protocol}, ${rule.port}).\n")
          }
        }
      } else if (rule.source == "*") {
        if (rule.dest in subnet!!) {
          for (r2 in routers) {
            val m2 = r2.getRepresentative() ?: continue
            if (r2.accept(m2, rule.dest, rule.protocol, rule.port)) {
              sb.append("hacl($m2, ${rule.dest}, ${rule.protocol}, ${rule.port}).\n")
            }
          }
        } else {
          val m1 = getRepresentative() ?: break
          val r2 = machineMap[rule.dest]!!
          val m2 = r2.getRepresentative() ?: continue
          if (r2.accept(m1, m2, rule.protocol, rule.port)) {
            sb.append("hacl($m1, $m2, ${rule.protocol}, ${rule.port}).\n")
          }
        }
      } else if (rule.dest == "*") {
        if (rule.source in subnet!!) {
          for (r2 in routers) {
            val m2 = r2.getRepresentative() ?: continue
            if (r2.accept(rule.source, m2, rule.protocol, rule.port)) {
              sb.append("hacl(${rule.source}, $m2, ${rule.protocol}, ${rule.port}).\n")
            }
          }
        } else {
          val m1 = getRepresentative() ?: break
          val r2 = machineMap[rule.source]!!
          val m2 = r2.getRepresentative() ?: continue
          if (r2.accept(m2, m1, rule.protocol, rule.port)) {
            sb.append("hacl($m2, $m1, ${rule.protocol}, ${rule.port}).\n")
          }
        }
      } else {
        var r2 = machineMap[rule.source]!!
        if (rule.source in subnet!!) {
          r2 = machineMap[rule.dest]!!
        }
        if (r2.accept(rule.source, rule.dest, rule.protocol, rule.port)) {
          sb.append("hacl(${rule.source}, ${rule.dest}, ${rule.protocol}, ${rule.port}).\n")
        }
      }
    }
    return sb.toString()
  }

  private fun getRepresentative(): String? {
    return subnet?.firstOrNull()
  }

  private fun accept(m1: String, m2: String, protocol: String, port: String): Boolean {
    for (rule in rules) {
      if (rule.accept(m1, m2, protocol, port) || rule.accept(m2, m1, protocol, port)) {
        return true
      }
    }
    return false
  }
}

@kotlinx.serialization.Serializable
data class Link(val source: String, val dest: String)

class TopologyGraph {
  companion object {
    fun build(machines: String, routers: String, links: String, outputFile: String) {
      val writer = FileWriter(outputFile)
      val sb = StringBuilder()
      sb.append("attackerLocated(internet).\n")
      sb.append("attackGoal(execCode(_,_)).\n")
      sb.append("hacl(X,Y,_,_):-\n\tinSubnet(X,S),\n\tinSubnet(Y,S).\n")
      //Parse machines and construct machine objects
      //Parse routers and construct router objects
      //Parse edges and add connections to routers
      //Build input.P from machines and routers
      for (m in Json.decodeFromString<List<Machine>>(machines)) {
        sb.append(m)
      }
      val routerList = Json.decodeFromString<List<Router>>(routers)
      val linkList = Json.decodeFromString<List<Link>>(links)
      val routerComponents = findRoutes(routerList, linkList)
      val subnetMaps = findSubnets(routerList, linkList)
      for (router in routerList) {
        router.subnet = subnetMaps.first[router]!!
      }
      for (router in routerList) {

        sb.append(router.build(routerComponents.first[routerComponents.second[router]!!], subnetMaps.second))
      }
      println(sb.toString())
      writer.write(sb.toString())
      writer.close()
    }

    private fun findSubnets(routers: List<Router>, links: List<Link>): Pair<Map<Router, Set<String>>, Map<String, Router>> {
      val subnetMap = mutableMapOf<Router, MutableSet<String>>()
      val routerMap = mutableMapOf<String, Router>()
      for (router in routers) {
        subnetMap[router] = mutableSetOf()
        routerMap[router.name] = router
      }
      val machineMap = mutableMapOf<String, Router>()
      for (link in links) {
        if (routerMap[link.source] in subnetMap && routerMap[link.dest] in subnetMap) {
          continue
        } else if (routerMap[link.source] in subnetMap){
          subnetMap[routerMap[link.source]]!!.add(link.dest)
          machineMap[link.dest] = routerMap[link.source]!!
        } else {
          subnetMap[routerMap[link.dest]]!!.add(link.source)
          machineMap[link.source] = routerMap[link.dest]!!
        }
      }
      return Pair(subnetMap, machineMap)
    }

    //Calculates the connected components in the topology
    private fun findRoutes(routers: List<Router>, links: List<Link>): Pair<List<Set<Router>>, Map<Router, Int>> {
      val components = mutableListOf<Set<Router>>()
      val graph = Array(routers.size) {Array(routers.size) {false}}
      val routerMap = mutableMapOf<Router, Int>()
      val indexMap = mutableMapOf<String, Int>()
      for (i in routers.indices) {
        indexMap[routers[i].name] = i
      }
      for (link in links) {
        if (link.source in indexMap && link.dest in indexMap) {
          graph[indexMap[link.source]!!][indexMap[link.dest]!!] = true
        }
      }
      val stack = mutableListOf<Router>()
      stack.addAll(routers)
      val visited = mutableMapOf<String, Boolean>()
      var currentComponent = mutableSetOf<Router>()
      while (stack.isNotEmpty()) {
        val node = stack.removeFirst()
        if(visited[node.name] == true) {
          continue
        }
        visited[node.name] = true
        if (node !in currentComponent) {
          components.add(currentComponent)
          currentComponent = mutableSetOf()
          routerMap[node] = components.size - 1
        }
        for (i in routers.indices) {
          if (visited[routers[i].name] == true) {
            continue
          }
          if (graph[indexMap[node.name]!!][i]) {
            currentComponent.add(routers[i])
            stack.add(routers[i])
            routerMap[routers[i]] = components.size - 1
          }
        }
      }
      if (currentComponent.isNotEmpty()) {
        components.add(currentComponent)
      }
      return Pair(components, routerMap)
    }
  }
}

fun main() {
  TopologyGraph.build("[${FileReader("machine.json").readText()}, ${FileReader("webServer.json").readText()}, ${FileReader("workStation.json").readText()}]", "[${FileReader("router.json").readText()}]", "[{\"source\": \"workStation\", \"dest\": \"router\"}, {\"source\": \"router\", \"dest\": \"webServer\"}, {\"source\": \"router\", \"dest\": \"fileServer\"}]", "output.txt")
}
