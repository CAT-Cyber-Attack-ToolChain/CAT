package contoller

import com.lordcodes.turtle.shellRun
import model.AttackGraphOutput
import model.MulvalInput
import java.io.File

class MulvalController (private val inputP: MulvalInput, private val outputDir: AttackGraphOutput): Controller () {
    fun generateGraph(){
        val path = outputDir.getPath()
        print("Generating attack graph using MulVAL...")

        val workingDir = File(path)
        shellRun("graph_gen.sh", listOf(inputP.getPath(), "-v", "-p"), workingDir)
        notifyObservers()

        println("Done!")
    }

}