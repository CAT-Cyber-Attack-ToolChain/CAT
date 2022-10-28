import kotlin.test.Test
import kotlin.test.assertContains

import graph.Graph

internal class GraphExportTest {
    @Test
    fun exportingNeo4jToJson() {
        val json = Export.exportToJSON()
        assertContains(json, "node")
        assertContains(json, "relationship")
    }

    @Test
    fun exportingNeo4jToGraph() {
        val graph : Graph = Export.translateToGraph(Export.exportToJSON())

        assertContains(graph.toString(), "Node")
        assertContains(graph.toString(), "Relationship")
    }

    @Test
    fun exportingGraphToCytoscapeJSON() {
        val graph : Graph = Export.translateToGraph(Export.exportToJSON())
        val json: String = graph.exportToCytoscapeJSON()

        assertContains(json, "\"data\" : {")
        assertContains(json, "name")
        assertContains(json, "source")
        assertContains(json, "target")
    }
}