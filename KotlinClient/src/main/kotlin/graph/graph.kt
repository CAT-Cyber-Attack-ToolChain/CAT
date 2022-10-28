package graph

class Graph(val list : MutableList<NodeOrRelationship>) {

    fun getNode(id: Int) : Node {
        val node = list.first { nr -> nr.id == id && nr is Node }
        node?.let {
            return node as Node
        }
        throw NoSuchElementException("Could not find Node with id $id")

    }

    fun getRelationship(id: Int) : Relationship {
        val relationship = list.first { nr -> nr.id == id && nr is Relationship }
        relationship?.let {
            return relationship as Relationship
        }
        throw NoSuchElementException("Could not find Relationship with id $id")
    }

    override fun toString(): String {
        return list.toString()
    }
}

open class NodeOrRelationship(open val id: Int, open val properties: Map<String, Any>)

data class Node(override val id: Int,
                override val properties: Map<String, Any>,
                val labels: List<String>) : NodeOrRelationship(id, properties) {
    override fun toString(): String {
        return "Node $id: $labels"
    }
}

data class Relationship(override val id: Int,
                        override val properties: Map<String, Any>,
                        val label: String,
                        val startId: Int,
                        val endId: Int) : NodeOrRelationship(id, properties) {
    override fun toString(): String {
        return "Relationship $id: $startId $label $endId"
    }
}
