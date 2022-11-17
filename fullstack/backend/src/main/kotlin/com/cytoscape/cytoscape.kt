package com.cytoscape

import java.util.HashMap

interface CytoObject {
    fun addProperty(key: String, value : Any)

    fun addProperties(properties : MutableMap<String, Any>)
}

data class CytoNode(val id: String, val label: String) : CytoObject {
    var properties: MutableMap<String, Any> = HashMap<String, Any>()
    override fun addProperties(properties: MutableMap<String, Any>) {
        this.properties = properties
    }

    override fun addProperty(key: String, value: Any) {
        properties.put(key, value)
    }
}

data class CytoEdge(val id: String, val source: String, val target: String, val label: String) : CytoObject {
    var properties: MutableMap<String, Any> = HashMap<String, Any>()
    override fun addProperties(properties: MutableMap<String, Any>) {
        this.properties = properties
    }

    override fun addProperty(key: String, value: Any) {
        properties.put(key, value)
    }
}

data class CytoDataWrapper(val data: CytoObject)