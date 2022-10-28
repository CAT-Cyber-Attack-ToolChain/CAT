
import contoller.MulvalController
import contoller.Neo4JController
import model.AttackGraphOutput
import model.MulvalInput
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
import org.neo4j.driver.Session
import org.neo4j.driver.Values.parameters
import metrics.decision.ShortestPath

open class Main {
    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val cur = System.getProperty("user.dir")
            val mulvalInput = MulvalInput("$cur/../mulval/testcases/3host/input.P")
            val mulvalOutput = AttackGraphOutput("$cur/../output")

            val mulvalController = MulvalController(mulvalInput, mulvalOutput)
            val neo4JController = Neo4JController(mulvalOutput)

            mulvalController.generateGraph()
            if (mulvalController.getGenerated()) {
                neo4JController.update()
            }


        }

//        fun generateGraphFromDatalog(inputFile : String, workingDirPath : String) {
//            print("Generating attack graph using MulVAL...")
//
//            val workingDir = File(workingDirPath)
//            shellRun("graph_gen.sh", listOf(inputFile, "-v", "-p"), workingDir)
//
//            println("Done!")
//
//            print("Transferring files to Neo4j...")
//            val curDir = System.getProperty("user.dir")
//            shellRun("mv", listOf("VERTICES.CSV", "ARCS.CSV", curDir + "/../Neo4j/mulval_output/"), workingDir)
//
//            println("Done!")
//        }
//
//        fun runNeo4j() {
//            print("Sending attack graph to Neo4j AuraDB...")
//            val neo4jDir = File(System.getProperty("user.dir") + "/../Neo4j")
//            shellRun("python3", listOf("graph_gen_neo4j.py"), neo4jDir)
//            println("Done!")
//        }
    }
}
