import axios from "axios"
import { useEffect, useState } from "react"
import CytoscapeComponent from "react-cytoscapejs"

import { useLoading, Audio, ThreeDots, BallTriangle, Grid } from '@agney/react-loading';

var styles = {
    height : "100%",
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
            backgroundColor : "green",
        }
    },
    {   selector : '.highlightEdge',
        style: {
            targetArrowColor: 'green',
            lineColor: 'green',
        }
    },
    {   selector : '.toDelete',
        style: {
            backgroundColor : "red",
            targetArrowColor: '#ff0000',
            lineColor: '#ff0000',
        }
    }
]


const Topology = ({graph, setAtkGraph, setTopology, setMetrics}) => {

    const [cursor, setCursor] = useState("default")

    const [isCutting, setCutting] = useState(false)
    const [edgeToCut, setCutEdges] = useState([])
    const [nodeToCut, setCutNodes] = useState([])
    const [loading, setLoading] = useState(false)

    const { containerProps, indicatorEl } = useLoading({
        loading: true,
        indicator: <Grid width="50" class="center" />
    })

    var topologyCyRef = undefined

    useEffect(() => {

        function resetToCut() {
            if (!isCutting) {
                setCutEdges([])
                setCutNodes([])
                topologyCyRef.nodes().removeClass("toDelete")
                topologyCyRef.edges().removeClass("toDelete")
            }
        }

        resetToCut()

    },[isCutting])

    function doStuffOnCy(cy) {
        cy.ready(() => onMouseover(cy))
        return cy
    }
    
    function onMouseover(cy) {
    
        cy.removeListener('click'); 
    
        cy.on('click', 'node', (event) => {
            if (isCutting) {
                const nodeId = event.target.data("id")
                const edges = getEdgesFromNode(nodeId)
                cy.$('#'+nodeId).addClass("toDelete")
                edges.addClass("toDelete")

                var addedEdges = edges.map(edge => ({"first": edge.data("source"), "second": edge.data("target")}))
                console.log(addedEdges)
                
                setCutEdges(prevState => [...prevState, ...addedEdges])
                setCutNodes(prevState => [...prevState, nodeId])
            }
        });
    
        cy.on('click', 'edge', (event) => {
            if (isCutting) {
                const edgeId = event.target.data("id")
                cy.$('#'+edgeId).addClass("toDelete")
                setCutEdges(prevState => [...prevState, {"first" : event.target.data("source"), "second" : event.target.data("target")}])
            }
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

    function cutModeHandler() {
        setCutting(!isCutting)
        setCursor(prevState => {
            if(prevState === 'default'){
              return "url(cursor.cur), auto";
            }
            return "default";
        });
    }

    function getEdgesFromNode(nodeId) {
        return topologyCyRef.edges(`[source = "${nodeId}"],[target = "${nodeId}"]`)
    }

    async function submitHandler() {
        if (isCutting) {
            setLoading(true)
            var nodes = new Set(nodeToCut)
            console.log("Edges strings (contain duplicates): " + edgeToCut)
            var edges = [...new Set(edgeToCut.map(JSON.stringify))].map(JSON.parse)
            console.log("nodes cutting " + Array.from(nodes.values()))
            console.log(Array.from(edges))
            console.log(JSON.stringify(Array.from(edges)))

            try {
              const response = await axios.post('http://localhost:8080/graph/separate', {
                nodes: JSON.stringify(Array.from(nodes)),
                edges: JSON.stringify(Array.from(edges))
              })

              let data = JSON.parse(response.data)
              console.log("Attack graph: ", data["attackGraph"])
              setAtkGraph(JSON.stringify(data['attackGraph']))
              setTopology(JSON.stringify(data['topologyGraph']))
              setMetrics()
              // console.log(data)
            } catch (error) {
              console.error('Error:', error);
            }
            setLoading(false)
        }
    }


    return (
        
        <div style={{width: "100%",position: "relative", cursor : cursor}}>
                {!loading ?
                <>
            <button id="cut-button" style={{position: "absolute", zIndex: 1, left: 0, bottom: 0,  margin : "0 0 20px 20px"}} onClick={() => cutModeHandler()}> Cut mode </button>
            <button id="push-button" style={{position: "absolute", zIndex: 1, right: 0, bottom: 0,  margin : "0 20px 20px 0"}} onClick={() => submitHandler()}> Cut </button>
                    <CytoscapeComponent cy={(cy) => topologyCyRef = doStuffOnCy(cy)} elements={JSON.parse(graph)} style={styles} stylesheet={stylesheet} layout={layout} /> </> :
            indicatorEl}
        </div>
        
    )
}

export default Topology;