package com.example.controller

import com.example.model.AttackGraphOutput
import com.lordcodes.turtle.shellRun
import com.example.model.MulvalInput
import java.io.File

class MulvalController(private val inputP: MulvalInput, private val outputDir: AttackGraphOutput) : Controller() {

    private var generated = false

    //TODO: Track mulval error messages and set generated accordingly
    fun generateGraph() {
        generated = false
        val path = outputDir.getPath()
        print("Generating attack graph using MulVAL...")

        val workingDir = File(path)
        shellRun("graph_gen.sh", listOf(inputP.getPath(), "-v", "-p"), workingDir)
        generated = true
        notifyObservers()

        println("Done!")
    }

    fun getGenerated(): Boolean {
        return generated
    }
}