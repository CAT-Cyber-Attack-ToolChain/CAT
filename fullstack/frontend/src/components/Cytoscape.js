import CytoscapeComponent from 'react-cytoscapejs';
import cytoscape from 'cytoscape';
import popper from 'cytoscape-popper';
import dagre from 'cytoscape-dagre';
import axios from 'axios';
import {useEffect} from "react"

cytoscape.use(popper);
cytoscape.use(dagre);

const host = process.env.REACT_APP_HOST
const port = process.env.REACT_APP_PORT
    

var styles = {
    backgroundColor: '#0a111f',
    zIndex:  0,
    position: "relative",
    height : "600px"
  }

var layout = {
    name: "dagre",
    spacingFactor: 3
}

var stylesheet = [
    {
        selector: 'node',
        style: {
            label: 'data(label)',
            fontSize: 200,
            padding: 300,
            width: 'label' ,
            height : 'label',
            textValign: "center",
            textHalign: "center",
            shape: 'rectangle',
            color: '#fca311',
            backgroundColor: '#14213d'
        }
    },
    {
        selector: 'edge',
        style: {
            width: 40,
            lineColor: '#fca311',
            targetArrowColor: '#fca311',
            arrowScale : 5,
            targetArrowShape: 'triangle',
            curveStyle : 'taxi',
            //'control-point-step-size' : '1000'
        }
    },
    {   selector : '.attackedNode',
        style: {
            backgroundColor: "green",
            transitionProperty: 'background-color, shape',
            transitionDuration: '0.5s'
        }
    },
    {   selector : '.attackedEdge',
        style: {
            width: 40,
            targetArrowColor: 'green',
            lineColor: 'green',
            transitionProperty: 'line-color, target-arrow-color',
            transitionDuration: '0.5s'
        }
    }
]

async function simulateRandomAttack() {
    const response = await axios.get(`http://${host}:${port}/simulation/random`)
    return response.data
}

async function simulateRealAttack() {
    const response = await axios.get(`http://${host}:${port}/simulation/real`)
    return response.data
}

async function simulateAttack(attackAgent) {
    const response = await axios.get(`http://${host}:${port}/simulation/${attackAgent}`)
    return response.data
}

/*
    Convert array of path into set of node
    arr : [{ first: nodeFrom, second: nodeTo}]
*/
function getNodesFromPath(arr) {
    const nodes = new Set()
    arr.forEach((path) => {
        nodes.add(path.first) 
        nodes.add(path.second)
    })
    return Array.from(nodes)
}





const Cytoscape = ({graph,setMapTop,attackAgent}) => {

    //initialise once Cytoscape components finishes
    var cyRef = undefined;
    
    useEffect(() => {
        function fitGraph() {
            cyRef.fit(cyRef.elements())
        }

        window.addEventListener('resize', fitGraph)
        return () => window.removeEventListener('resize', fitGraph)
    })

    /* Set mapping for higlighting Topology */
    useEffect(() => {
        cyRef.ready(() => {
            cyRef.on('mouseover','node', (event) => {
                setMapTop(event.target.data("properties")["machines"])
            })
            cyRef.on('mouseout', 'node', () => setMapTop([]))
        })

        cyRef.minZoom(cyRef.zoom() - 0.01)
        cyRef.maxZoom(0.1)

    }, [cyRef])

    /*
        Find id of edge on graph with corresponding src and dst
        Returns id of nodes and edges the belongs on the graph

        Note: edges is an array of array of edgeIds [[edgeId]] since two nodes can be traversed from two
    */
    function simulationParser(attackedPath) {

        const nodes = getNodesFromPath(attackedPath)
        const edges = []
      
        for (var i = 0; i < attackedPath.length; i++) {
            const src = attackedPath[i].first
            const dst = attackedPath[i].second
            const queryPath = JSON.parse(graph).filter((item)=> (item.data.source === src) && (item.data.target === dst))
            if (queryPath.length !== 0) {
                edges.push(queryPath.map((path) => (
                    path.data.id
                )))
            } else {
                console.error('Could not find edges with source : ' + src + ' target: ' + dst)
            }
        }
        
        return {nodes: nodes, edges: edges}
    }

    async function simulationHandler() {
        
        //disable simulate button
        document.getElementById('simulate-button').disabled = true
    
        // remove previous attack path (if exists)
        cyRef.$('.attackedNode').removeClass("attackedNode")
        cyRef.$('.attackedEdge').removeClass("attackedEdge")
        
        const attacked = await simulateAttack(attackAgent).then(path=> {
          return simulationParser(path);
        })

        function highlightNode(index) {
          cyRef.$('#' + attacked.nodes[index]).addClass("attackedNode")
          if (index === attacked.nodes.length - 1) {
            //allow simulate button to be press after animation is complete
            document.getElementById('simulate-button').disabled = false
            return
          }
          setTimeout(function(){highlightEdge(index)}, 500)
        }

        function highlightEdge(index) {
          // animate the path (if not the last node)
          attacked.edges[index].forEach(edgeId => cyRef.$('#' + edgeId).addClass("attackedEdge"))
          setTimeout(function(){highlightNode(index + 1)}, 500)
        }

        // start highlighting nodes and edges of attack
        highlightNode(0)
    }

    return(
        <div style={{width: "100%", position: "relative", overflow: "hidden"}}>
            <button className="input-custom" id="simulate-button" style={{position: "absolute", zIndex: 1, right: 0, margin : "20px 20px 0 0"}} onClick={() => simulationHandler()}> Simulate </button>
            <CytoscapeComponent cy={(cy) => cyRef = cy} elements={JSON.parse(graph)} style={styles} stylesheet={stylesheet} layout={layout} />
        </div>
    )
}

export default Cytoscape;