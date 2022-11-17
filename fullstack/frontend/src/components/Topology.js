import { useState } from "react"
import CytoscapeComponent from "react-cytoscapejs"

function doStuffOnCy(cy) {
    cy.ready(() => onMouseover(cy))
    return cy
}

function onMouseover(cy) {

    cy.removeListener('click'); 

    cy.on('click', 'node', (event) => {
        console.log(event.target.data("id"))
    });

    cy.on('click', 'edge', (event) => {
        console.log(event.target.data("id"))
    });

    cy.removeListener('mouseover') 
    
    cy.on('mouseover', 'node', (event) => {
        cy.$('#'+event.target.data("id")).addClass("highlightNode")
    })

    cy.on('mouseover', 'edge', (event) => {
        cy.$('#'+event.target.data("id")).addClass("highlightEdge")
    })
    
    cy.removeListener('mouseout')

    cy.on('mouseout', 'node' , (event) => {
        cy.$('#'+event.target.data("id")).removeClass("highlightNode")
    })

    cy.on('mouseout', 'edge' , (event) => {
        cy.$('#'+event.target.data("id")).removeClass("highlightEdge")
    })
}
    

var styles = {
    height : "800px",
    backgroundColor: 'grey',
    zIndex:  0,
    position: "relative",
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
            curveStyle : 'bezier',
            'control-point-step-size' : '1000'
        }
    },
    {   selector : '.highlightNode',
        style: {
            backgroundColor : "red",
        }
    },
    {   selector : '.highlightEdge',
        style: {
            targetArrowColor: '#ff0000',
            lineColor: '#ff0000',
        }
    }
]

const Topology = ({graph}) => {

    const [isCutting, setCutting] = useState(false)
    var topologyCyRef = undefined

    return (
        <div style={{width: "100%",position: "relative"}}>
            <button id="cut-button" style={{position: "absolute", zIndex: 1, right: 0, margin : "20px 20px 0 0"}} onClick={() => setCutting(true)}> Simulate </button>
            <CytoscapeComponent cy={(cy) => topologyCyRef = doStuffOnCy(cy)} elements={JSON.parse(graph)} style={styles} stylesheet={stylesheet} layout={layout} />
        </div>
    )
}

export default Topology;