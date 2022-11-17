package com.graph

import com.beust.klaxon.Klaxon
import com.cytoscape.CytoDataWrapper
import com.cytoscape.CytoEdge
import com.cytoscape.CytoNode
import com.lordcodes.turtle.shellRun
import com.model.MulvalInput
import kotlinx.serialization.json.jsonArray
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.OutputStream

data class Hacl(val m1: Machine, val m2: Machine, val protocol: String, val port: String) {
  override fun toString(): String {
    return "hacl($m1, $m2, $protocol, $port)."
  }
}

data class HasAccount(val user: User, val machine: Machine, val privilege: String) {
  override fun toString(): String {
    return "hasAccount($user, $machine, $privilege)."
  }
}

data class NetworkServiceInfo(
        val machine: Machine,
        val application: Application,
        val protocol: String,
        val port: String,
        val privilege: String
) {
  override fun toString(): String {
    return "networkServiceInfo($machine, $application, $protocol, $port, $privilege)."
  }
}

class Machine : GraphNode {
  val haclList: MutableList<Hacl> = mutableListOf()
  val accountList: MutableList<HasAccount> = mutableListOf()
  val services: MutableList<NetworkServiceInfo> = mutableListOf()
  val name: String
  val id: Int
  var subnet: String = ""

  constructor(_name: String, _id: Int) {
    name = _name
    id = _id
  }

  constructor(name: String) : this(name, next++)

  override fun id(): Int = id

  companion object {
    var next: Int = 0
  }

  override fun toString(): String {
    return name
  }

  fun build(): String {
    val sb = StringBuilder()
    var flag = false
    if (haclList.isNotEmpty()) {
      sb.append(haclList.joinToString("\n") { x -> x.toString() })
      sb.append("\n")
      flag = true
    }
    if (accountList.isNotEmpty()) {
      sb.append(accountList.joinToString("\n"))
      sb.append("\n")
      flag = true
    }
    if (services.isNotEmpty()) {
      sb.append(services.joinToString("\n"))
      sb.append("\n")
      flag = true
    }
    if (subnet != "") {
      sb.append("inSubnet($name, $subnet)\n")
      flag = true
    }
    if (flag) {
      sb.append("\n")
    }
    return sb.toString()
  }
}

class Application(val name: String) {
  val programInfo: MutableList<SetuidProgramInfo> = mutableListOf()
  var client: Boolean = false
  override fun toString(): String {
    return name
  }

  fun build(): String {
    val sb = StringBuilder()
    if (client) {
      sb.append("clientApplication($name)\n")
    }
    for (p in programInfo) {
      sb.append(sb)
    }
    return sb.toString()
  }
}

class User(val name: String) {
  var inCompetent = false
  override fun toString(): String {
    return name
  }

  fun build(): String {
    val sb = StringBuilder()
    if (inCompetent) {
      sb.append("inCompetent($name)\n")
    }

    return sb.toString()
  }
}

class Vulnerability(val name: String) {
  val exists: MutableSet<VulExists> = mutableSetOf()
  var cvss: CVSS = CVSS.UNDEFINED
  var locality: String = "localExploit"
  var type: String = "privEscalation"

  override fun toString(): String {
    return name
  }

  fun build(): String {
    return "vulProperty($name, $locality, $type).\ncvss($name, $cvss).\n" + exists.joinToString("") + "\n"
  }
}

data class SetuidProgramInfo(val machine: Machine, val application: Application, val privilege: String) {
  override fun toString(): String {
    return "setuidProgramInfo($machine, $application, $privilege).\n"
  }
}

data class VulExists(val machine: Machine, val vulnerability: Vulnerability, val application: Application) {
  override fun toString(): String {
    return "vulExists($machine, $vulnerability, $application).\n"
  }
}

enum class CVSS {
  UNDEFINED,
  LOW,
  MEDIUM,
  HIGH
}

class TopologyGraph(
        nodes: MutableMap<Int, Machine>,
        arcs: MutableMap<Int, MutableSet<Int>>,
        val machines: MutableMap<String, Machine>,
        val applications: MutableMap<String, Application>,
        val vulnerabilities: MutableMap<String, Vulnerability>,
        val users: MutableMap<String, User>
) : Graph<Machine>(nodes, arcs) {
  companion object {
    fun build(input: MulvalInput): TopologyGraph {
      val filepath = input.getPath()
      val machines = mutableMapOf<String, Machine>()
      val applications = mutableMapOf<String, Application>()
      val vulnerabilities = mutableMapOf<String, Vulnerability>()
      val users = mutableMapOf<String, User>()
      val reader = BufferedReader(FileReader(filepath))
      for (line in reader.lines()) {
        if (line == "") {
          continue
        }
        val props = line.replace(Regex("\\s"), "").replace('(', ',').dropLast(2).split(",")
        when (props[0]) {
          /*"hacl" -> {
            val m1 = getMachine(props[1], machines)
            val m2 = if (props[2] == "_") {
              Machine("_")
            } else {
              getMachine(props[2], machines)
            }
            m1.haclList.add(Hacl(m1, m2, props[3], props[4]))
          }*/

          "inSubnet" -> {
            val m1 = getMachine(props[1], machines)
            m1.subnet = props[2]
          }

          "inCompetent" -> {
            val u = getUser(props[1], users)
            u.inCompetent = true
          }

          "vulExists" -> {
            val m = getMachine(props[1], machines)
            val v = getVulnerability(props[2], vulnerabilities)
            val a = getApplication(props[3], applications)
            v.exists.add(VulExists(m, v, a))
          }

          "vulProperty" -> {
            val v = getVulnerability(props[1], vulnerabilities)
            v.locality = props[2]
            v.type = props[3]
          }

          "isClient" -> {
            val app = getApplication(props[1], applications)
            app.client = true
          }

          "setuidProgramInfo" -> {
            val m = getMachine(props[1], machines)
            val a = getApplication(props[2], applications)
            a.programInfo.add(SetuidProgramInfo(m, a, props[3]))
          }

          "cvss" -> {
            val v = getVulnerability(props[1], vulnerabilities)
            when (props[2]) {
              "l" -> {
                v.cvss = CVSS.LOW
              }

              "m" -> {
                v.cvss = CVSS.MEDIUM
              }

              "h" -> {
                v.cvss = CVSS.HIGH
              }
            }
          }

          "hasAccount" -> {
            val u = getUser(props[1], users)
            val m = getMachine(props[2], machines)
            m.accountList.add(HasAccount(u, m, props[3]))
          }

          "networkServiceInfo" -> {
            val m = getMachine(props[1], machines)
            val a = getApplication(props[2], applications)
            m.services.add(NetworkServiceInfo(m, a, props[3], props[4], props[5]))
          }

          else -> continue
        }
      }

      val writer = FileWriter("xsbParse")
      writer.write("xsb -e \"consult(command),consult('${input.getPath()}'),start1,halt.\"")
      writer.close()
      shellRun("chmod", listOf("+x", "xsbParse"))
      shellRun("./xsbParse")
      File("xsbParse").delete()

      val haclReader = BufferedReader(FileReader("output.txt"))
      val haclText: String = haclReader.readText()
      haclReader.close()
      val obj = kotlinx.serialization.json.Json.parseToJsonElement(haclText)
      for (props in obj.jsonArray) {

        val m1 = if (props.jsonArray[0].toString() == "_") {
          Machine("_")
        } else {
          getMachine(props.jsonArray[1].toString(), machines)
        }
        val m2 = if (props.jsonArray[2].toString() == "_") {
          Machine("_")
        } else {
          getMachine(props.jsonArray[2].toString(), machines)
        }
        //m1.haclList.add(Hacl(m1, m2, props.json))
      }

      val sb = StringBuilder()
      for (m in machines.values) {
        sb.append(m.build())
      }
      for (v in vulnerabilities.values) {
        sb.append(v.build())
      }
      for (u in users.values) {
        sb.append(u.build())
      }
      for (a in applications.values) {
        sb.append(a.build())
      }
      //println(sb)
      val nodes = mutableMapOf<Int, Machine>()
      val arcs = mutableMapOf<Int, MutableSet<Int>>()
      for (m in machines.values) {
        nodes[m.id()] = m
        arcs[m.id()] = mutableSetOf()
        for (h in m.haclList) {
          if (h.m2.name == "_") {
            addWildcardConnections(m, machines, arcs)
          } else {
            arcs[m.id()]!!.add(h.m2.id())
            // println("Added arc from ${m.name} to ${h.m2.name}")
          }
        }

      }
      return TopologyGraph(nodes, arcs, machines, applications, vulnerabilities, users)
    }

    private fun addWildcardConnections(
            m: Machine,
            machines: MutableMap<String, Machine>,
            arcs: MutableMap<Int, MutableSet<Int>>
    ) {
      for (m2 in machines.values) {
        if (m != m2) {
          arcs[m.id()]!!.add(m2.id())
          // println("Added arc from ${m.name} to ${m2.name}")
        }
      }

    }

    private fun getMachine(
            name: String,
            machines: MutableMap<String, Machine>,
    ): Machine {
      if (!machines.containsKey(name)) {
        machines[name] = Machine(name)
      }
      return machines[name]!!
    }

    private fun getApplication(name: String, map: MutableMap<String, Application>): Application {
      if (!map.containsKey(name)) {
        map[name] = Application(name)
      }
      return map[name]!!
    }

    private fun getVulnerability(name: String, map: MutableMap<String, Vulnerability>): Vulnerability {
      if (!map.containsKey(name)) {
        map[name] = Vulnerability(name)
      }
      return map[name]!!
    }

    private fun getUser(name: String, map: MutableMap<String, User>): User {
      if (!map.containsKey(name)) {
        map[name] = User(name)
      }
      return map[name]!!
    }
  }

  fun exportToCytoscapeJSON() : String{
    val klaxon = Klaxon()
    val nodestrlist: MutableList<String> = mutableListOf()
    val arcstrlist: MutableList<String> = mutableListOf()

    for (m in nodes.values) {
      val cytoNode = CytoNode("n${m.id()}", m.name)
      cytoNode.addProperty("bool", 0)
      cytoNode.addProperty("node_id", m.id())
      cytoNode.addProperty("text", m.toString())
      cytoNode.addProperty("type", "OR")
      nodestrlist.add(klaxon.toJsonString(CytoDataWrapper(cytoNode)))
    }

    var counter = 0
    for ((k, v) in arcs) {
      for (n in v) {
        counter++
        val cytoEdge = CytoEdge("e$counter", "n$k", "n$n", "edge")
        arcstrlist.add(klaxon.toJsonString(CytoDataWrapper(cytoEdge)))
      }
    }
    return "[" + nodestrlist.joinToString() + "," + arcstrlist.joinToString() + "]"
  }
  fun separateGraph(removeNodes: List<Int>, removeEdges: List<Pair<Int, Int>>) {
    for (i in removeNodes) {
      nodes.remove(i)
      arcs.remove(i)
      for ((_, s) in arcs) {
        s.remove(i)
      }
    }
    for ((i, j) in removeEdges) {
      if (arcs.containsKey(i)) {
        arcs[i]!!.remove(j)
        nodes[i]!!.haclList.filterNot { h -> h.m1.id() == i && h.m2.id() == j }
      }
    }
  }
}
