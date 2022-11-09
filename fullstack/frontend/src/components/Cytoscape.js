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
    {   selector : '.attacked',
        style: {
            backgroundColor : "red",
            shape : "circle"
        }
    }
]

const simulateRandomAttack = async () => {
    const nodeAttacked = await axios.get("http://localhost:8080/simulation/random")
    return  nodeAttacked.data
}


const Cytoscape = ({items}) => {

    //initialise once Cytoscape components finishes
    var cyRef = undefined;

    /*
        Match node_id to Cytoscape node id i.e

        const example = {"data" : {"id" : "n52", "label" : "52", "properties" : {"bool": 0, "text": "execCode(workStation,root)", "type": "OR", "node_id": 1}}}
        Find element which has id = example.data.properties.node_id and get the corresponsing example.data.id
    */
    function simulationParser(nodeAttacked) {
        const result = []
        for (var i = 0; i < nodeAttacked.length; i++) {
            const node = nodeAttacked[i]
            const query = JSON.parse(items).filter((item)=> (item.data.properties.node_id === node))
            if (query.length !== 0) {
                result.push(query[0].data.id)
            } else {
                console.error('Node id :' + node + "not found in Cytoscape elements")
            }
        }
        return result
    }

    async function simulationHandler() {
        const nodeIds = await simulateRandomAttack().then(res=>simulationParser(res))
        //add class to node so that stylesheet applies
        nodeIds.forEach((id) => {
            cyRef.$((ele, i, eles) => (ele._private.data.id === id)).addClass("attacked")
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