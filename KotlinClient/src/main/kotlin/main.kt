import contoller.MulvalController
import contoller.Neo4JController
import metrics.decision.NormalisedMOPL
import metrics.decision.NumberOfPaths
import metrics.decision.ShortestPath
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import metrics.assistive.MeanOfPathLengths
import metrics.assistive.MedianOfPathLengths
import metrics.assistive.ModeOfPathLengths
import metrics.assistive.StandardDeviationOfPathLengths
import metrics.decision.WeakestAdversary
import model.AttackGraphOutput
import model.MulvalInput
import model.PathCache

open class Main {
  companion object {

    @JvmStatic
    fun main(args: Array<String>) {
      val cur = System.getProperty("user.dir")
      val mulvalInput = MulvalInput("$cur/../mulval/testcases/3host/input.P")
      val mulvalOutput = AttackGraphOutput("$cur/../output")
      val cache = PathCache()

      val mulvalController = MulvalController(mulvalInput, mulvalOutput)
      val neo4JController = Neo4JController(mulvalOutput, cache)

      if (mulvalController.generateGraph()) {
        neo4JController.update()
      }

      val metricList = listOf(NormalisedMOPL(cache), NumberOfPaths(cache), ShortestPath(cache), WeakestAdversary(), MeanOfPathLengths(cache), MedianOfPathLengths(cache), ModeOfPathLengths(cache), StandardDeviationOfPathLengths(cache))

      runBlocking {
        metricList.forEach{
          launch {println(it.toString() + ": " + it.calculate())}
        }
      }
    }
  }
}
