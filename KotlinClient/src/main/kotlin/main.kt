import contoller.MulvalController
import contoller.Neo4JController
import model.PathCache
import model.AttackGraphOutput
import model.MulvalInput
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
import org.neo4j.driver.Session
import org.neo4j.driver.Values.parameters

open class Main {
    companion object {
        val driver: Driver = GraphDatabase.driver(
            "neo4j+s://42ce3f9a.databases.neo4j.io",
            AuthTokens.basic("neo4j", "qufvn4LK6AiPaRBIWDLPRzFh4wqzgI5x_n2bXHc1d38")
        )

        fun close() {
            driver.close()
        }

        fun get_graph() {
            val session: Session = driver.session()
            val result: List<String> = session.writeTransaction { tx ->
                val result: org.neo4j.driver.Result = tx.run("MATCH(n) RETURN n", parameters())
                result.list { r -> r.toString() }
            }
            println(result)
        }

        @JvmStatic
        fun main(args: Array<String>) {
            val cur = System.getProperty("user.dir")
            val inputFile = MulvalInput("$cur/../mulval/testcases/3host/input.P")
            val output = AttackGraphOutput("$cur/../mulval/testcases/3host/3host_output")

            val mulvalController = MulvalController(inputFile, output)
            val neo4JController = Neo4JController(output, PathCache())

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
