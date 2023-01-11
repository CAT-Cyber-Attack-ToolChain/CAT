package com.controller

import com.model.PathCache
import com.controller.Neo4J
import com.view.Updatable
import com.metrics.decision.NormalisedMOPL
import com.metrics.decision.NumberOfPaths
import com.metrics.decision.ShortestPath
import com.metrics.decision.WeakestAdversary
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.metrics.assistive.MeanOfPathLengths
import com.metrics.assistive.MedianOfPathLengths
import com.metrics.assistive.ModeOfPathLengths
import com.metrics.assistive.StandardDeviationOfPathLengths

object  Metric : Updatable {
    private lateinit var cache : PathCache 
    private lateinit var metrics : String

    fun getMetrics() : String {
        return metrics
    }

    override fun update() {
        cache = Neo4J.getCache()
        cache.update()

        val shortestPath = ShortestPath(cache)
        val meanofpathlength = MeanOfPathLengths(cache)
        val normalisedmopl = NormalisedMOPL(cache)
        val medianpathlength = MedianOfPathLengths(cache)
        val modepathlength = ModeOfPathLengths(cache)
        val sdpathlength = StandardDeviationOfPathLengths(cache)
        val numberofpaths = NumberOfPaths(cache)
        //val weakestadversary = WeakestAdversary(cache)

         metrics = "{\"shortestpath\": " + shortestPath.calculate() +
                   ",\"meanpathlength\": " + meanofpathlength.calculate() +
                   ",\"normalisedmopl\": " + normalisedmopl.calculate() +
                   ",\"medianpathlength\": " + medianpathlength.calculate() +
                   ",\"modepathlength\": " + modepathlength.calculate() +
                   ",\"sdpathlength\": " + sdpathlength.calculate() +
                   ",\"numberofpaths\": " + numberofpaths.calculate()+ "}"
                  // ",\"weakestadversary\": " + weakestadversary.calculate() + "}"

//        metrics = "{\"shortestpath:\" \"" + shortestPath.calculate() +
//                  "\",meanpathlength\" \": " + meanofpathlength.calculate() +
//                  "\",normalisedmopl: \"" + normalisedmopl.calculate() +
//                  "\",medianpathlength: \"" + medianpathlength.calculate() +
//                  "\",modepathlength: \"" + modepathlength.calculate() +
//                  "\",sdpathlength: \"" + sdpathlength.calculate() +
//                  "\",numberofpaths: \"" + numberofpaths.calculate() + "\"}"
                 // "\",weakestadversary: \"" + weakestadversary.calculate() + "\"}"

        //val mets2 = {shortestpath : "1", meanpathlength: "2", normalisedmopl: "3", modepathlength: "4", sdpathlength: "5", numberofpaths: "6", weakestadversary: "7"}
    }
}