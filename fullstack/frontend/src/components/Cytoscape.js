import CytoscapeComponent from 'react-cytoscapejs';
import cytoscape from 'cytoscape';
import popper from 'cytoscape-popper';
import dagre from 'cytoscape-dagre';
import axios from 'axios';
import {useEffect, useState} from "react"
import Modal from "react-modal";

cytoscape.use(popper);
cytoscape.use( dagre );

const host = process.env.REACT_APP_HOST
const port = process.env.REACT_APP_PORT

function makePopper(ele) {
  ele.popperDiv = ele.popper({
      content: () => {
          let div = document.createElement('div');

          div.innerHTML = ele.data('label');
          div.setAttribute("role", "tooltip")
          div.classList.add("my-tooltip")

          div.style.display = 'none'

          document.body.appendChild(div);

          return div;
      },
      popper: {
          placement: 'auto'
      }
  })
}



    
var attackGraphStyles = {
    backgroundColor: '#B1C5CB',
    zIndex:  0,
    position: "relative",
    height : "100%"
}

var reachabilityGraphStyles = {
    backgroundColor: "#B1C5CB",
    zIndex:  0,
    position: "relative",
    height : "600px"
}


var layout = {
    name: "dagre",
    spacingFactor: 3
}

var modalStyles = {
    content: {
        color: "#05b2dc",
        backgroundColor: "#0a111f",
        height : "80%",
        width : "80%",
        inset : "0",
        margin : "auto"
    },
    overlay: {
        zIndex: 1
    }
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
            shape: 'ellipse',
            color: '#B1C5CB',
            backgroundColor: '#30414b'
        }
    },
    {
        selector: 'edge',
        style: {
            width: 40,
            lineColor: '#414952',
            targetArrowColor: '#000',
            arrowScale : 5,
            targetArrowShape: 'triangle-backcurve',
            curveStyle : 'taxi',
            // 'control-point-step-size' : '100'
        }
    },
    {   selector : '.attackedNode',
        style: {
            backgroundColor: '#ff91a4',
            transitionProperty: 'background-color, shape',
            transitionDuration: '0.5s',
            color: '#060A12'
        }
    },
    {   selector : '.attackedEdge',
        style: {
            width: 40,
            targetArrowColor: '#ff91a4',
            lineColor: '#ff91a4',
            transitionProperty: 'line-color, target-arrow-color',
            transitionDuration: '0.5s'
        }
    }
]

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




/* Attack Graph */
const Cytoscape = ({graph,reachability,setMapTop,attackAgent,loading,loader}) => {


    const [isAttackResultOpen, setAttackResultOpen] = useState(false)
    const [isReachabilityGraphOpen, setReachabilityOpen] = useState(false)

    function toggleAttackResult() {
        setAttackResultOpen(!isAttackResultOpen)
    }

    function attackUnsuccessfulPopUp() {
        toggleAttackResult()
    }

    //initialise once Cytoscape components finishes
    var cyRef = undefined;
    
    useEffect(() => {
        function fitGraph() {
            cyRef.fit(cyRef.elements())
        }

        window.addEventListener('resize', fitGraph)
    })

    /* Set mapping for higlighting Topology */
    useEffect(() => {
        cyRef.ready(() => {
            cyRef.on('mouseover','node', (event) => {
              setMapTop(event.target.data("properties")["machines"])
            })
            cyRef.on('mouseout', 'node', () => setMapTop([]))

           
        })

        cyRef.ready(function () {
          cyRef.edges().forEach(function (ele) {
              makePopper(ele);
          });

          cyRef.on('mouseover','edge', (event) => {
            event.target.popperDiv.state.elements.popper.style.display = "flex";
          })
          cyRef.on('mouseout', 'edge', (event) => event.target.popperDiv.state.elements.popper.style.display = "none");
        });

        cyRef.minZoom(cyRef.zoom() - 0.001)
        cyRef.maxZoom(0.001)

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

        if (attacked.nodes.length === 0) {
            attackUnsuccessfulPopUp()
            document.getElementById('simulate-button').disabled = false
        } else {
            // start highlighting nodes and edges of attack
            highlightNode(0)
        }
    }

    return(
        <div style={{width: "100%", position: "relative", height: "100%"}} id="attack-graph">
            {!loading ? 
            <>
                <button className="input-custom" id="open-button" style={{position: "absolute", zIndex: 1, left: 0, margin : "20px 0 0 20px"}} onClick={() => setReachabilityOpen(true)}> Reachability Graph </button>
                <button className="input-custom" id="simulate-button" style={{position: "absolute", zIndex: 1, right: 0, margin : "20px 20px 0 0"}} onClick={() => simulationHandler()}> Simulate </button>
                <CytoscapeComponent cy={(cy) => cyRef = cy} elements={JSON.parse(graph)} style={attackGraphStyles} stylesheet={stylesheet} layout={layout} />
            </> : <div style={{alignItems: 'center', justifyContent: 'center', display: 'flex', height: '100%'}}>{loader}</div>
            }
            <Modal
                isOpen={isAttackResultOpen}
                onRequestClose={toggleAttackResult}
                contentLabel="Attack Unsuccessful"
                style={modalStyles}
            >
                <div>Attack Unsuccessful</div>
                <div className='right-aligned'>
                    <button onClick={toggleAttackResult} className="input-custom">OK</button>
                </div>
                
            </Modal>
            <Modal
                isOpen={isReachabilityGraphOpen}
                style={modalStyles}
                contentLabel="Reachability Graph"
                ariaHideApp={false}
            >
                <CytoscapeComponent elements={JSON.parse(reachability)} style={reachabilityGraphStyles} stylesheet={stylesheet} layout={layout} />
                <div className='right-aligned'>
                    <button onClick={() => setReachabilityOpen(false)} className="input-custom">OK</button>
                </div>
            </Modal>
        </div>
    )
}

export default Cytoscape;