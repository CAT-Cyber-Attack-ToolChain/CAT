package graph

import com.beust.klaxon.Klaxon
import cytoscape.CytoDataWrapper
import cytoscape.CytoObject
import cytoscape.CytoNode
import cytoscape.CytoEdge

class Graph(val list : MutableList<NodeOrRelationship>) {

    fun getNode(id: Int) : Node {
        try {
            val node = list.first { nr -> nr.id == id && nr is Node }
            return node as Node
        } catch (e: NoSuchElementException) {
            throw NoSuchElementException("Could not find Node with id $id")
        }
    }

    fun getRelationship(id: Int) : Relationship {
        try {
            val relationship = list.first { nr -> nr.id == id && nr is Relationship }
            return relationship as Relationship
        } catch (e: NoSuchElementException) {
            throw NoSuchElementException("Could not find Relationship with id $id")
        }
    }

    fun exportToCytoscapeJSON() : String {
        val jsonArray: StringBuilder = java.lang.StringBuilder("[")
        val klaxon = Klaxon()
        list.forEach { nr -> jsonArray.append(klaxon.toJsonString(nr.toCytoscapeJson()) + ", ") }

        jsonArray.append("]")
        return jsonArray.toString()
    }

    override fun toString(): String {
        return list.toString()
    }
}

abstract class NodeOrRelationship(open val id: Int, open val properties: Map<String, Any>) {
    abstract fun toCytoscapeJson() : CytoDataWrapper
}

data class Node(override val id: Int,
                override val properties: Map<String, Any>,
                val labels: List<String>) : NodeOrRelationship(id, properties) {

    override fun toCytoscapeJson(): CytoDataWrapper {
        return CytoDataWrapper(CytoNode(id.toString(), id.toString()))
    }

    override fun toString(): String {
        return "Node $id: $labels"
    }
}

data class Relationship(override val id: Int,
                        override val properties: Map<String, Any>,
                        val label: String,
                        val startId: Int,
                        val endId: Int) : NodeOrRelationship(id, properties) {

    override fun toCytoscapeJson(): CytoDataWrapper {
        return CytoDataWrapper(CytoEdge(id.toString(), startId.toString(), endId.toString()))
    }

    override fun toString(): String {
        return "Relationship $id: $startId $label $endId"
    }
}
