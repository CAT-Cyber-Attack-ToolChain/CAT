import kotlin.test.Test
import kotlin.test.assertContains

import graph.Graph
import kotlin.test.assertEquals

internal class GraphExportTest {
    @Test
    fun exportingNeo4jToJson() {
        val json = Export.exportToJSON()
        assertContains(json, "node")
        assertContains(json, "relationship")
    }

    @Test
    fun exportingNeo4jToGraph() {
        val graph : Graph = Export.translateToCytoscapeJS(Export.exportToJSON())
        val node = graph.getNode(0)
        assertEquals(0, node.id)

        val relationship = graph.getRelationship(0)
        assertEquals(0, relationship.id)

        assertContains(graph.toString(), "Node")
        assertContains(graph.toString(), "Relationship")
    }
}