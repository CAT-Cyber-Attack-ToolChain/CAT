package contoller

import com.lordcodes.turtle.shellRun
import model.AttackGraphOutput
import java.io.File

class Neo4JController(private val dir: AttackGraphOutput) {
    fun update() {
        print("Sending attack graph to Neo4j AuraDB...")
        val neo4jDir = File(dir.getPath())
        shellRun("python3", listOf("graph_gen_neo4j.py"), neo4jDir)
        println("Done!")
    }

}