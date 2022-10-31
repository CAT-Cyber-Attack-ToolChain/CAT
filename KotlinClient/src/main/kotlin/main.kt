import contoller.MulvalController
import contoller.Neo4JController
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.runBlocking
//import metrics.PathCache
//import metrics.decision.NormalisedMOPL
//import metrics.decision.NumberOfPaths
//import metrics.decision.ShortestPath
import model.AttackGraphOutput
import model.MulvalInput

open class Main {
  companion object {

    @JvmStatic
    fun main(args: Array<String>) {
      val cur = System.getProperty("user.dir")
      val mulvalInput = MulvalInput("$cur/../mulval/testcases/3host/input.P")
      val mulvalOutput = AttackGraphOutput("$cur/../output")

      val mulvalController = MulvalController(mulvalInput, mulvalOutput)
      val neo4JController = Neo4JController(mulvalOutput)

      if (mulvalController.generateGraph()) {
        neo4JController.update()
      }

//      PathCache.update()
//
//      val shortestPath = ShortestPath()
//      val normalisedMOPL = NormalisedMOPL()
//      val numberOfPaths = NumberOfPaths()
//      val metricList = listOf(shortestPath, normalisedMOPL, numberOfPaths)
//
//      println(PathCache.get())
//      runBlocking {
//        metricList.forEach{
//          launch {print(it.calculate())}
//        }
//      }
    }
  }
}
