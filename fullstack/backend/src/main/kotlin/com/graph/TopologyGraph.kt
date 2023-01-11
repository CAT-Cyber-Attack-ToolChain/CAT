package com.graph

import com.beust.klaxon.Klaxon
import com.cytoscape.CytoDataWrapper
import com.cytoscape.CytoEdge
import com.cytoscape.CytoNode
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.FileWriter
import java.io.FileReader
import java.lang.Exception

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
        val privilege: String
) {
  override fun toString(): String {
    return "networkServiceInfo($machine, $application, $protocol, $port, $privilege)."
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
data class FirewallRule(val source: String, val dest: String, val protocol: String, val port: String, val direction: String) {
  fun accept(m1: String, m2: String, protocol: String, port: String, reachability: MutableMap<String, MutableSet<String>>): String {
    val restrictedM1 = restrict(m1, source) ?: return ""
    val restrictedM2 = restrict(m2, dest) ?: return ""
    val restrictedProtocol = restrict(protocol, this.protocol) ?: return ""
    val restrictedPort = restrict(port, this.port) ?: return ""
    reachability[restrictedM1]!!.add(restrictedM2)
    println("$restrictedM1->$restrictedM2")
    return "hacl($restrictedM1, $restrictedM2, $restrictedProtocol, $restrictedPort).\n"
  }

  private fun restrict(x: String, y: String): String? {
    return if (x == "*" && y == "*") {
      "_"
    } else if (x == "*") {
      y
    } else if (y == "*") {
      x
    } else if (x == y) {
      x
    } else {
      null
    }
  }
}

@kotlinx.serialization.Serializable
data class Router(val type: String, val label: String, val value: String, val name: String, val rules: List<FirewallRule>) {
  var subnet: Set<String>? = null
  fun build(routers: Set<Router>, machineMap: Map<String, Router>, reachability: MutableMap<String, MutableSet<String>>): String {
    if (subnet == null) {
      throw Exception("Failed To Create Subnet $name")
    }
    val sb = StringBuilder()
    for (machine in subnet!!) {
      sb.append("inSubnet($machine, ${name}Subnet).\n")
    }
    for (rule in rules) {
      if (rule.direction == "out") {
        val machines = mutableSetOf<String>()
        if (rule.source == "*") {
          machines.addAll(subnet!!)
        } else {
          machines.add(rule.source)
        }
        if (rule.dest == "*") {
          for (r2 in routers) {
            if (r2 == this) {
              continue
            }
            for (m in machines) {
              sb.append(r2.acceptAll(m, rule.protocol, rule.port, reachability))
            }
          }
        } else {
          for (m in machines) {
            sb.append(machineMap[rule.dest]!!.accept(m, rule.dest, rule.protocol, rule.port, reachability))
          }
        }
      }
    }
    return sb.toString()
  }

  private fun accept(m1: String, m2: String, protocol: String, port: String, reachability: MutableMap<String, MutableSet<String>>): String {
    val sb = StringBuilder()
    for (rule in rules) {
      if (rule.direction == "out") {
        continue
      }
      sb.append(rule.accept(m1, m2, protocol, port, reachability))
    }
    return sb.toString()
  }

  private fun acceptAll(m1: String, protocol: String, port: String, reachability: MutableMap<String, MutableSet<String>>): String {
    val sb = StringBuilder()
    for (m2 in subnet!!) {
      sb.append(accept(m1, m2, protocol, port, reachability))
    }
    return sb.toString()
  }
}

@kotlinx.serialization.Serializable
data class Link(val source: String, val dest: String)

class TopologyGraph {
  companion object {
    fun build(machines: String, routers: String, links: String, outputFile: String): String {
      val writer = FileWriter(outputFile)
      val sb = StringBuilder()
      sb.append("attackerLocated(internet).\n")
      sb.append("attackGoal(execCode(_,_)).\n")
      sb.append("hacl(X,Y,_,_):-\n\tinSubnet(X,S),\n\tinSubnet(Y,S).\n")
      //Parse machines and construct machine objects
      //Parse routers and construct router objects
      //Parse edges and add connections to routers
      //Build input.P from machines and routers
      val machineList = Json.decodeFromString<List<Machine>>(machines)
      for (m in machineList) {
        sb.append(m)
      }
      val routerList = Json.decodeFromString<List<Router>>(routers)
      val linkList = Json.decodeFromString<List<Link>>(links)
      val routerComponents = findRoutes(routerList, linkList)
      val subnetMaps = findSubnets(routerList, linkList)
      val reachability = mutableMapOf<String, MutableSet<String>>()
      for (m in machineList) {
        reachability[m.name] = mutableSetOf()
      }
      for (router in routerList) {
        router.subnet = subnetMaps.first[router]!!
        for (m in (router.subnet as Set<String>)) {
          reachability[m]!!.addAll(router.subnet as Set<String>)
        }
      }
      for (router in routerList) {
        sb.append(router.build(routerComponents.first[routerComponents.second[router]!!], subnetMaps.second, reachability))
      }
      println(reachability)
      println(sb.toString())
      writer.write(sb.toString())
      writer.close()
      return exportToCytoscapeJSON(machineList, reachability)
    }

    fun exportToCytoscapeJSON(machines: List<Machine>, reachability: MutableMap<String, MutableSet<String>>) : String{
      val klaxon = Klaxon()
      val strlist: MutableList<String> = mutableListOf()
      var id = 0
      for (m in machines) {
        val cytoNode = CytoNode(m.name, m.name)
        cytoNode.addProperty("bool", 0)
        cytoNode.addProperty("node_id", id)
        cytoNode.addProperty("text", m.name)
        cytoNode.addProperty("type", "OR")
        strlist.add(klaxon.toJsonString(CytoDataWrapper(cytoNode)))
        id++
      }

      for ((k, v) in reachability) {
        for (n in v) {
          id++
          val cytoEdge = CytoEdge("e$id", "$k", "$n", "edge")
          strlist.add(klaxon.toJsonString(CytoDataWrapper(cytoEdge)))
        }
      }
      return "[" + strlist.joinToString() + "]"
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
        if (visited[node.name] == true) {
          continue
        }
        visited[node.name] = true
        if (node !in currentComponent) {
          components.add(currentComponent)
          currentComponent = mutableSetOf(node)
          routerMap[node] = components.size
        }
        for (i in routers.indices) {
          if (visited[routers[i].name] == true) {
            continue
          }
          if (graph[indexMap[node.name]!!][i] || graph[i][indexMap[node.name]!!]) {
            currentComponent.add(routers[i])
            stack.add(routers[i])
            routerMap[routers[i]] = components.size
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
