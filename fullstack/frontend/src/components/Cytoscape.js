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
    zIndex:  0
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
            shape : "circle"
        }
    },
    {   selector : '.attackedEdge',
        style: {
            'target-arrow-color': '#ff0000',
            'line-color': '#ff0000',
        }
    }
]

async function simulateRandomAttack() {
    const response = await axios.get("http://localhost:8080/simulation/random")
    return response.data
}

//Convert array of path into set of node
function getNodesFromPath(arr) {
    const nodes = new Set()
    arr.forEach((path) => {
        nodes.add(path.first) 
        nodes.add(path.second)
    })
    return Array.from(nodes)
}



const Cytoscape = ({items}) => {

    //initialise once Cytoscape components finishes
    var cyRef = undefined;
    
    //Find id of edge on graph with corresponding src and dst
    function simulationParser(attackedPath) {
        const nodes = getNodesFromPath(attackedPath)
        const edges = []
        
        for (var i = 0; i < attackedPath.length; i++) {
            const src = attackedPath[i].first
            const dst = attackedPath[i].second
            const queryPath = JSON.parse(items).filter((item)=> (item.data.source === src) && (item.data.target === dst))
            if (queryPath.length !== 0) {
                edges.push(queryPath[0].data.id)
            } else {
                console.error('Could not find edges with source : ' + src + ' target: ' + dst)
            }
        }
        return {nodes: nodes, edges: edges}
    }

    async function simulationHandler() {
        const attacked = await simulateRandomAttack().then(res=>simulationParser(res))
        
        //add class to node and edges so that stylesheet applies
        attacked.nodes.forEach((id) => {
            cyRef.$((ele) => (ele._private.data.id === id)).addClass("attackedNode")
        })
        attacked.edges.forEach((id) => {
            cyRef.$((ele) => (ele._private.data.id === id)).addClass("attackedEdge")
        })
    }

    return(
        <div style={{width: "100%", height : "100%"}}>
            <button style={{position: "absolute", zIndex: 1}} onClick={() => simulationHandler()}> Simulate </button>
            <CytoscapeComponent cy={(cy) => cyRef = doStuffOnCy(cy)} elements={JSON.parse(items)} style={styles} stylesheet={stylesheet} layout={layout} />
        </div>
    )
}

export default Cytoscape;