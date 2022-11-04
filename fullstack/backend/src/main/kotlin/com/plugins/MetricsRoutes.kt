package com.plugins

import ModeOfPathLengths
import com.metrics.decision.NormalisedMOPL
import com.metrics.decision.NumberOfPaths
import com.metrics.decision.ShortestPath
import com.metrics.decision.WeakestAdversary
import com.model.Neo4JMapping
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import metrics.assistive.MeanOfPathLengths
import metrics.assistive.MedianOfPathLengths
import metrics.assistive.StandardDeviationOfPathLengths


fun Route.MetricsRouting() {
  route("/metrics") {
    get {
      val optionalController = Neo4JMapping.get("default")

      if (optionalController.isEmpty) {
        throw Exception()
      }

      val cache = optionalController.get().getCache()

      val shortestPath = ShortestPath(cache)
      val meanofpathlength = MeanOfPathLengths(cache)
      val normalisedmopl = NormalisedMOPL(cache)
      val medianpathlength = MedianOfPathLengths(cache)
      val modepathlength = ModeOfPathLengths(cache)
      val sdpathlength = StandardDeviationOfPathLengths(cache)
      val numberofpaths = NumberOfPaths(cache)
      val weakestadversary = WeakestAdversary()

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
