package com.graph

import com.beust.klaxon.Klaxon
import com.cytoscape.CytoDataWrapper
import com.cytoscape.CytoEdge
import com.cytoscape.CytoNode

abstract class NodeOrRelationship(open val id: Int, open val properties: MutableMap<String, Any>) {
    abstract fun toCytoscapeJson() : CytoDataWrapper
}

data class AttackNode(override val id: Int,
                override val properties: MutableMap<String, Any>,
                val labels: List<String>) : NodeOrRelationship(id, properties), GraphNode {

    override fun toCytoscapeJson(): CytoDataWrapper {
        val cytoNode = CytoNode("n${id.toString()}", id.toString())
        cytoNode.addProperties(properties)
        return CytoDataWrapper(cytoNode)
    }

    override fun toString(): String {
        return "Node $id: $labels"
    }

    override fun id(): Int = id
}

data class AttackRelationship(override val id: Int,
                        override val properties: MutableMap<String, Any>,
                        val label: String,
                        val startId: Int,
                        val endId: Int) : NodeOrRelationship(id, properties) {

    override fun toCytoscapeJson(): CytoDataWrapper {
        val cytoEdge = CytoEdge("e${id.toString()}", "n${startId.toString()}", "n${endId.toString()}", label)
        cytoEdge.addProperties(properties)
        return CytoDataWrapper(cytoEdge)
    }

    override fun toString(): String {
        return "Relationship $id: $startId $label $endId"
    }
}

class AttackGraph(nodes: MutableMap<Int, AttackNode>, arcs: MutableMap<Int, MutableSet<Int>>, val arclist : MutableList<AttackRelationship>): Graph<AttackNode>(nodes, arcs) {
    fun exportToCytoscapeJSON(): String {
        val klaxon = Klaxon()
        val nodestrlist: List<String> = nodes.values.map {nr -> klaxon.toJsonString(nr.toCytoscapeJson())}
        val arcstrlist: List<String> = arclist.map {nr -> klaxon.toJsonString(nr.toCytoscapeJson())}
        return "[" + nodestrlist.joinToString() + "," + arcstrlist.joinToString() + "]"
    }
}
