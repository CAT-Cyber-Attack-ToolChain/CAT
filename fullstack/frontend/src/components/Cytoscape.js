import CytoscapeComponent from 'react-cytoscapejs';
import cytoscape from 'cytoscape';
import popper from 'cytoscape-popper';
import dagre from 'cytoscape-dagre';
import axios from 'axios';
import prevAttackPath from './PreviousPath';
import {useEffect} from "react"

cytoscape.use(popper);
cytoscape.use(dagre);

function doStuffOnCy(cy) {
    cy.ready(() => mouseAction(cy))

    return cy
}

const host = process.env.REACT_APP_HOST
const port = process.env.REACT_APP_PORT

function mouseAction(cy) {
    function makePopper(ele) {
        ele.popperDiv = ele.popper({
            content: () => {
                let div = document.createElement('div');

                div.innerHTML = ele.data('id');
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

    cy.ready(function () {
        cy.nodes().forEach(function (ele) {
            makePopper(ele);
        });
    });

    cy.removeListener('mouseover');

    cy.on('mouseover', 'node', (event) => {
        event.target.popperDiv.state.elements.popper.style.display = "flex";
    });

    cy.removeListener('mouseout');
    cy.on('mouseout', 'node', (event) => event.target.popperDiv.state.elements.popper.style.display = "none");
}
    

var styles = {
    backgroundColor: 'grey',
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
            color: 'black',
            backgroundColor: 'white'
        }
    },
    {
        selector: 'edge',
        style: {
            width: 20,
            lineColor: '#000',
            targetArrowColor: '#000',
            arrowScale : 5,
            targetArrowShape: 'triangle',
            curveStyle : 'taxi',
            //'control-point-step-size' : '1000'
        }
    },
    {   selector : '.attackedNode',
        style: {
            backgroundColor : "red",
            transitionProperty: 'background-color, shape',
            transitionDuration: '0.5s'
        }
    },
    {   selector : '.attackedEdge',
        style: {
            targetArrowColor: '#ff0000',
            lineColor: '#ff0000',
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





const Cytoscape = ({graph,map,toHighlight,attackAgent}) => {

    //initialise once Cytoscape components finishes
    var cyRef = undefined;
    
    useEffect(() => {
        function fitGraph() {
            cyRef.fit(cyRef.elements())
        }

        window.addEventListener('resize', fitGraph)
        return () => window.removeEventListener('resize', fitGraph)
    })

    /* TO IMPLEMENT
       toHighlight gets data from Atk graph (Mapping here)
       This is called whenever toHighlight changes
    */    
    useEffect(() => {
        console.log("from topologybuilder " + toHighlight)
    },[toHighlight])


    /* Set mapping for higlighting Topology */
    useEffect(() => {
        cyRef.on('click','node', (event) => {
            map(event.target.data("id"))
        })

        cyRef.on('zoom', (event) => console.log(event.target.zoom()))

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
    
        // check if previous attack path exists
        if (Object.keys(prevAttackPath).length !== 0) {
            prevAttackPath.nodes.forEach((id) => {
                cyRef.$('#' + id).removeClass("attackedNode")
            })
            prevAttackPath.edges.forEach((id) => {
                cyRef.$('#' + id).removeClass("attackedEdge")
            })
        }
        
        const attacked = await simulateAttack(attackAgent).then(path=> {
          return simulationParser(path);
        })
        prevAttackPath.nodes = attacked.nodes;
        prevAttackPath.edges = attacked.edges;

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
        <div style={{width: "100%", position: "relative"}}>
            <button id="simulate-button" style={{position: "absolute", zIndex: 1, right: 0, margin : "20px 20px 0 0"}} onClick={() => simulationHandler()}> Simulate </button>
            <CytoscapeComponent cy={(cy) => cyRef = doStuffOnCy(cy)} elements={JSON.parse(graph)} style={styles} stylesheet={stylesheet} layout={layout} />
        </div>
    )
}

export default Cytoscape;