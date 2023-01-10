package com.ktor.routes

import com.controller.Neo4J
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

//TODO: Implement MVC model for metrics
fun Route.MetricsRouting() {
  route("/metrics") {
    get {

      val cache = Neo4J.getCache()

      val shortestPath = ShortestPath(cache)
      val meanofpathlength = MeanOfPathLengths(cache)
      val normalisedmopl = NormalisedMOPL(cache)
      val medianpathlength = MedianOfPathLengths(cache)
      val modepathlength = ModeOfPathLengths(cache)
      val sdpathlength = StandardDeviationOfPathLengths(cache)
      val numberofpaths = NumberOfPaths(cache)
      val weakestadversary = WeakestAdversary(cache)

      call.respond("{\"shortestpath\": " + shortestPath.calculate() +
              ",\"meanpathlength\": " + meanofpathlength.calculate() +
              ",\"normalisedmopl\": " + normalisedmopl.calculate() +
              ",\"medianpathlength\": " + medianpathlength.calculate() +
              ",\"modepathlength\": " + modepathlength.calculate() +
              ",\"sdpathlength\": " + sdpathlength.calculate() +
              ",\"numberofpaths\": " + numberofpaths.calculate() +
              ",\"weakestadversary\": " + weakestadversary.calculate() + "}")
    }
  }
}
