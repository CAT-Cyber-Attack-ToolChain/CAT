import contoller.MulvalController
import contoller.Neo4JController
import model.AttackGraphOutput
import model.MulvalInput

import graph.Graph

open class Main {
    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val cur = System.getProperty("user.dir")
            val mulvalInput = MulvalInput("$cur/../mulval/testcases/3host/input.P")
            val mulvalOutput = AttackGraphOutput("$cur/../output")

            val mulvalController = MulvalController(mulvalInput, mulvalOutput)
            val neo4JController = Neo4JController(mulvalOutput)
            val graph : Graph = Export.translateToGraph(Export.exportToJSON())

            mulvalController.generateGraph()
            if (mulvalController.getGenerated()) {
                neo4JController.update()
            }
        }

    }
}
