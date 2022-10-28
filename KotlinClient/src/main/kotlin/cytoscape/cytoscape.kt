package cytoscape

interface CytoObject {}

data class CytoNode(val id: String, val name: String) : CytoObject

data class CytoEdge(val id: String, val source: String, val target: String) : CytoObject

data class CytoDataWrapper(val data: CytoObject)