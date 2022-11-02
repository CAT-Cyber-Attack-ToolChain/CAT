package routes

import ModeOfPathLengths
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.example.model.*
import com.example.controller.*
import com.example.shoppingList
import com.example.graph.*
import metrics.assistive.MeanOfPathLengths
import metrics.assistive.MedianOfPathLengths
import metrics.assistive.StandardDeviationOfPathLengths
import metrics.decision.NormalisedMOPL
import metrics.decision.NumberOfPaths
import metrics.decision.ShortestPath
import metrics.decision.WeakestAdversary
import model.PathCache


fun Route.MetricsRouting() {
    val cache = PathCache()
    cache.update()
    val shortest_path = ShortestPath(cache)
    val meanofpathlength = MeanOfPathLengths(cache)
    val normalisedmopl = NormalisedMOPL(cache)
    val medianpathlength = MedianOfPathLengths(cache)
    val modepathlength = ModeOfPathLengths(cache)
    val sdpathlength = StandardDeviationOfPathLengths(cache)
    val numberofpaths = NumberOfPaths(cache)
    val weakestadversary = WeakestAdversary()
    route("/metrics") {
        get {
            call.respond("{\"shortestpath\": " + shortest_path.calculate() +
                    ",\"meanpathlength\": " + meanofpathlength.calculate() +
                    ",\"normalisedmopl\": " + normalisedmopl.calculate() +
                    ",\"medianpathlength\": "+ medianpathlength.calculate() +
                    ",\"modepathlength\": "+ modepathlength.calculate() +
                    ",\"sdpathlength\": " + sdpathlength.calculate() +
                    ",\"numberofpaths\": "+ numberofpaths.calculate() +
                    ",\"weakestadversary\": " + weakestadversary.calculate() + "}")
        }
    }
}
