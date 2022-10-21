package metrics.decision

import metrics.DecisionMetric
import metrics.PathCache
import org.neo4j.driver.*

class ShortestPath : DecisionMetric() {
    override fun calculate(): Int = PathCache.get().min()
        //1. find the destination node D
        //2. find all the neighbours and add them to an and set and an or set. 
        //3. for each member of the OR set O, create a new node with O appended
        //   to the AND set.
}
