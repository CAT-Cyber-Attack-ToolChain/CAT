import CytoscapeComponent from 'react-cytoscapejs';
import cytoscape from 'cytoscape';
import popper from 'cytoscape-popper';
import axios from 'axios';

cytoscape.use(popper);

function doStuffOnCy(cy) {
    cy.ready(() => onMouseover(cy))

    return cy
}

function onMouseover(cy) {
    function makePopper(ele) {
        ele.popperDiv = ele.popper({
            content: () => {
                let div = document.createElement('div');

                div.innerHTML = ele.data('properties').text;
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
        console.log("Showing")
    });

    cy.removeListener('mouseout');
    cy.on('mouseout', 'node', (event) => event.target.popperDiv.state.elements.popper.style.display = "none");
}
    

var styles = {
    width: '100%',
    height: '500px',
    backgroundColor: 'grey',
    zIndex:  0,
    position: "relative"
  }

var layout = {
    name: 'breadthfirst'
}

var stylesheet = [
    {
        selector: 'node',
        style: {
            label: 'data(properties.node_id)',
            'font-size': 30,
            width: 'label',
            padding: 10,
            "text-valign": "center",
            "text-halign": "center",
            shape: function (node) {
                switch (node.data('properties').type) {
                    case "LEAF": return 'rectangle'
                    case "AND": return 'ellipse'
                    case "OR": return 'diamond'
                    default: return 'circle'
                }

            },
            color: function (node) {
                switch (node.data('properties').type) {
                    case "LEAF": return 'black'
                    case "AND": return 'white'
                    case "OR": return 'yellow'
                    default: return 'white'
                }
            },
            backgroundColor: function (node) {
                if (node.data('properties').type === "AND") {
                    return 'green'
                } else if (node.data('properties').type === "OR") {
                    return 'orange'
                } else {
                    return 'white'
                }
            }
        }
    },
    {
        selector: 'edge',
        style: {
            'width': 3,
            'line-color': '#ccc',
            'target-arrow-color': '#ccc',
            'target-arrow-shape': 'triangle',
            'curve-style': 'bezier'
        }
    },
    {   selector : '.attackedNode',
        style: {
            backgroundColor : "red",
            shape : "circle",
            'transition-property': 'background-color, shape',
            'transition-duration': '0.5s'
        }
    },
    {   selector : '.attackedEdge',
        style: {
            'target-arrow-color': '#ff0000',
            'line-color': '#ff0000',
            'transition-property': 'line-color, target-arrow-color',
            'transition-duration': '0.5s'
        }
    }
]

async function simulateRandomAttack() {
    const response = await axios.get("http://localhost:8080/simulation/random")
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



const Cytoscape = ({graph}) => {

    //initialise once Cytoscape components finishes
    var cyRef = undefined;
    // set in every attack simulation (used for removing previous attack path)
    var prevAttackPath = undefined;
    
    /*
        Find id of edge on graph with corresponding src and dst
        Returns id of nodes and edges the belongs on the graph
    */
    function simulationParser(attackedPath) {
        const nodes = getNodesFromPath(attackedPath)
        const edges = []
        
        for (var i = 0; i < attackedPath.length; i++) {
            const src = attackedPath[i].first
            const dst = attackedPath[i].second
            const queryPath = JSON.parse(graph).filter((item)=> (item.data.source === src) && (item.data.target === dst))
            if (queryPath.length !== 0) {
                edges.push(queryPath[0].data.id)
            } else {
                console.error('Could not find edges with source : ' + src + ' target: ' + dst)
            }
        }
        return {nodes: nodes, edges: edges}
    }

    async function simulationHandler() {
        
        //disable simulate button
        document.getElementById('simulate-button').disabled = true
    
        if (typeof prevAttackPath !== 'undefined') {
            prevAttackPath.nodes.forEach((id) => {
                cyRef.$('#' + id).removeClass("attackedNode")
            })
            prevAttackPath.edges.forEach((id) => {
                cyRef.$('#' + id).removeClass("attackedEdge")
            })
        }
        
        const attacked = await simulateRandomAttack().then(path=>simulationParser(path))
        prevAttackPath = attacked;

        function highlightNode(index) {
          if (index === attacked.nodes.length) {
            //allow simulate button to be press after animation is complete
            document.getElementById('simulate-button').disabled = false
            return
          }
          cyRef.$('#' + attacked.nodes[index]).addClass("attackedNode")
          setTimeout(function(){highlightEdge(index)}, 500)
        }

        function highlightEdge(index) {
          // animate the path (if not the last node)
          cyRef.$('#' + attacked.edges[index]).addClass("attackedEdge")
          setTimeout(function(){highlightNode(index + 1)}, 500)
        }

        // start highlighting nodes and edges of attack
        highlightNode(0)
    }

    return(
        <div style={{position: "relative"}}>
            <button id="simulate-button" style={{position: "absolute", zIndex: 1, right: 0, margin : "20px 20px 0 0"}} onClick={() => simulationHandler()}> Simulate </button>
            <CytoscapeComponent cy={(cy) => cyRef = doStuffOnCy(cy)} elements={JSON.parse(graph)} style={styles} stylesheet={stylesheet} layout={layout} />
        </div>
    )
}

export default Cytoscape;