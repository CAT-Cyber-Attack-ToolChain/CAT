package com.graph

import java.io.BufferedReader
import java.io.FileReader

data class Hacl(val m1: Machine, val m2: Machine, val protocol: String, val port : String) {
    override fun toString(): String {
        return "hacl($m1, $m2, $protocol, $port)."
    }
}

data class HasAccount(val user: User, val machine: Machine, val privilege: String) {
    override fun toString(): String {
        return "hasAccount($user, $machine, $privilege)."
    }
}

data class NetworkServiceInfo(val machine: Machine, val application: Application, val protocol: String, val port: String, val privilege: String) {
    override fun toString(): String {
        return "networkServiceInfo($machine, $application, $protocol, $port, $privilege)."
    }
}

class Machine(val name: String) {
    val haclList: MutableList<Hacl> = mutableListOf()
    val accountList: MutableList<HasAccount> = mutableListOf()
    val services: MutableList<NetworkServiceInfo> = mutableListOf()

    override fun toString(): String {
        return name
    }

    fun build(): String {
        return haclList.joinToString("\n") + "\n" + accountList.joinToString("\n") + "\n" + services.joinToString("\n")
    }
}

class Subnet(val name: String) {
    val machines: MutableSet<Machine> = mutableSetOf()

    override fun toString(): String {
        return machines.joinToString("\n") { x -> "inSubnet($x, $name)" }
    }
}

class Application(val name: String) {
    override fun toString(): String {
        return name
    }
}

class User(val name: String) {
    override fun toString(): String {
        return name
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
        return "vulProperty($name, $locality, $type).\n" + exists.joinToString("\n")
    }
}

data class SetuidProgramInfo(val machine: Machine, val application: Application, val privilege: String) {
    override fun toString(): String {
        return "setuidProgramInfo($machine, $application, $privilege)."
    }
}

data class VulExists(val machine: Machine, val vulnerability: Vulnerability, val application: Application) {
    override fun toString(): String {
        return "vulExists($machine, $vulnerability, $application)"
    }
}

enum class CVSS {
    UNDEFINED,
    LOW,
    MEDIUM,
    HIGH
}

class TopologyGraph(filepath: String) {
    init {
        val machines = mutableMapOf<String, Machine>()
        val subnets = mutableMapOf<String, Subnet>()
        val incompetents = mutableListOf<Machine>()
        val clientApplications = mutableListOf<Application>()
        val applications = mutableMapOf<String, Application>()
        val setuidProgramInfo = mutableListOf<SetuidProgramInfo>()
        val vulnerabilities = mutableMapOf<String, Vulnerability>()
        val users = mutableMapOf<String, User>()
        val reader = BufferedReader(FileReader(filepath))
        for (line in reader.lines()) {
            if (line == "") {
                continue
            }
            val props = line.replace('(', ',').substring(0, line.length - 2).split(",")
            when(props[0]) {
                "hacl" -> {
                    val m1 = getMachine(props[1], machines)
                    val m2 = getMachine(props[2], machines)
                    m1.haclList.add(Hacl(m1, m2, props[3], props[4]))
                }
                "inSubnet" -> {
                    val m1 = getMachine(props[1], machines)
                    val subnet = getSubnet(props[2], subnets)
                    subnet.machines.add(m1)
                }
                "inCompetent" -> {
                    val m = getMachine(props[1], machines)
                    incompetents.add(m)
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
                    clientApplications.add(app)
                }
                "setuidProgramInfo" -> {
                    val m = getMachine(props[1], machines)
                    val a = getApplication(props[2], applications)
                    val upi = SetuidProgramInfo(m, a, props[3])
                    setuidProgramInfo.add(upi)
                }
                "cvss" -> {
                    val v = getVulnerability(props[1], vulnerabilities)
                    when (props[2]) {
                        "l" -> {v.cvss = CVSS.LOW}
                        "m" -> {v.cvss = CVSS.MEDIUM}
                        "h" -> {v.cvss = CVSS.HIGH}
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
        machines.values.map { x -> println(x.build()) }
        vulnerabilities.values.map { x -> println(x.build()) }
    }

    fun getMachine(name: String, machines: MutableMap<String, Machine>): Machine {
        if (!machines.containsKey(name)) {
            machines[name] = Machine(name)
        }
        return machines[name]!!
    }

    fun getSubnet(name: String, subnets: MutableMap<String, Subnet>): Subnet {
        if (!subnets.containsKey(name)) {
            subnets[name] = Subnet(name)
        }
        return subnets[name]!!
    }

    fun getApplication(name: String, map: MutableMap<String, Application>): Application {
        if (!map.containsKey(name)) {
            map[name] = Application(name)
        }
        return map[name]!!
    }

    fun getVulnerability(name: String, map: MutableMap<String, Vulnerability>): Vulnerability {
        if (!map.containsKey(name)) {
            map[name] = Vulnerability(name)
        }
        return map[name]!!
    }

    fun getUser(name: String, map: MutableMap<String, User>): User {
        if (!map.containsKey(name)) {
            map[name] = User(name)
        }
        return map[name]!!
    }
}