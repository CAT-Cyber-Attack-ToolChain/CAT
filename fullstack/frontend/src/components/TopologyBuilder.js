import axios from "axios";
import CytoscapeComponent from "react-cytoscapejs";
import Dropdown from "react-dropdown";
import "react-dropdown/style.css";
import { useState, useEffect } from "react";

const padding = 40
const panelHeight = 250
const builderHeight = window.innerHeight - padding - panelHeight

var styles = {
  height: builderHeight,
  backgroundColor: "grey",
  zIndex: 0,
  position: "relative",
};

var layout = {
  name: "dagre",
  spacingFactor: 3,
};

var stylesheet = [
  {
    selector: "node",
    style: {
      label: "data(label)",
      fontSize: 200,
      padding: 300,
      width: "label",
      height: "label",
      textValign: "center",
      textHalign: "center",
      shape: "rectangle",
      color: "black",
      backgroundColor: "white",
    },
  },
  {
    selector: "edge",
    style: {
      width: 20,
      lineColor: "#000",
      targetArrowColor: "#000",
      arrowScale: 5,
      targetArrowShape: "none",
      curveStyle: "bezier",
      "control-point-step-size": "1000",
    },
  },
  {
    selector: ".highlightNode",
    style: {
      backgroundColor: "darkgrey",
    },
  },
  {
    selector: ".clickedNode",
    style: {
      borderStyle: "ridge",
      borderWidth: "20px",
      borderColor: "green"
    },
  },
  {
    selector: ".highlightEdge",
    style: {
      targetArrowColor: "red",
      lineColor: "red",
    },
  },
  {
    selector: ".toDelete",
    style: {
      backgroundColor: "red",
      targetArrowColor: "#ff0000",
      lineColor: "#ff0000",
    },
  },
];

const TopologyBuilder = ({setAtkGraph, toHighlight}) => {

  //initialised once component renders
  var cyRef = undefined
  // network graph values
  const [cursor, setCursor] = useState("default");
  const [netGraph, setNetGraph] = useState([]);
  const [selected, setSelected] = useState(undefined);
  const [machines, setMachines] = useState([
    { label: "a", value: "a" },
    { label: "b", value: "b" },
    { label: "c", value: "c" },
  ]);
  const [curDevice, setCurDevice] = useState(undefined);
  const [nextId, setNextId] = useState(0);
  const [created, setCreated] = useState({});

  /* TO IMPLEMENT
     toHighlight gets data from topology builder (Mapping here)
     This is called whenever toHighlight changes
  */
  useEffect(() => {
    if (cyRef) {
      if (toHighlight.length !== 0) {
        toHighlight.forEach(machine => {
          cyRef.$(ele => ele.data('label') === machine).addClass('highlightNode')
        });
      } else {
        cyRef.$('.highlightNode').removeClass('highlightNode')
      }
      
    }
  },[toHighlight, cyRef])

  function onMouseover(cy) {
    cy.removeListener("click");
    cy.on("click", "node", (event) => {
      const nodeId = event.target.data("id");
      if (selected) {
        setNetGraph([
          ...netGraph,
          {
            data: {
              id: nextId,
              label: "edge",
              properties: {},
              source: selected,
              target: nodeId,
            },
          },
        ]);
        cy.$('#' + selected).removeClass("clickedNode");
        cy.$('#' + nodeId).removeClass("clickedNode");
        setNextId(nextId + 1);
        setSelected(undefined);
      } else {
        setSelected(nodeId);
        cy.$('#' + nodeId).addClass("clickedNode");
      }
    });

    cy.removeListener("cxttap");
    cy.on("cxttap", "node", (event) => {
      const nodeId = event.target.data("id");
      console.log("Remove " + nodeId);
      created[event.target.data("label")] = false;
      setCreated(created);
      setNetGraph(
        netGraph.filter(
          (x) =>
            x["data"]["id"] !== nodeId &&
            x["data"]["source"] !== nodeId &&
            x["data"]["target"] !== nodeId
        )
      );
    });

    cy.on("click", "edge", (event) => {
      setNetGraph(
        netGraph.filter((x) => x["data"]["id"] !== event.target.data("id"))
      );
    });

    cy.removeListener("mouseover");

    cy.on("mouseover", "node", (event) => {
      console.log(cy.minZoom())
      cy.$("#" + event.target.data("id")).addClass("highlightNode");
    });

    cy.on("mouseover", "edge", (event) => {
      cy.$("#" + event.target.data("id")).addClass("highlightEdge");
    });

    cy.removeListener("mouseout");

    cy.on("mouseout", "node", (event) => {
      cy.$("#" + event.target.data("id")).removeClass("highlightNode");
    });

    cy.on("mouseout", "edge", (event) => {
      cy.$("#" + event.target.data("id")).removeClass("highlightEdge");
    });

    return cy
  }

  function addConfiguration(file) {
    const fr = new FileReader();
    fr.addEventListener("load", (event) => {
      const obj = JSON.parse(event.target.result);
      if (Array.isArray(obj)) {
        setMachines([...machines, ...obj.filter((o) => !machines.some((m) => m["label"] === o["label"]))])
      } else if (!machines.some((m) => m["label"] === obj["label"])) {
        setMachines([...machines, obj]);
      }
    });
    fr.readAsText(file.target.files[0]);
  }

  function setDevice(option) {
    if (option) {
      setCurDevice(option.value);
    }
  }

  function addDevice() {
    if (!curDevice) {
      return;
    }
    if (created[curDevice]) {
      return;
    }
    setNetGraph([
      ...netGraph,
      {
        data: {
          id: nextId,
          label: curDevice,
          properties: {
            bool: 0,
            text: curDevice,
            type: "OR",
            node_id: nextId,
          },
          type: machines.filter((x) => x.label === curDevice)[0].type,
          machine: {...machines.filter((x) => x.label === curDevice)[0]} 
        },
      },
    ]);
    setNextId(nextId + 1);
    created[curDevice] = true;
    setCreated(created)
    
  }

  function printNetGraph() {
    console.log(netGraph)
    // setting zoom
    if (cyRef) {

      cyRef.fit()
      cyRef.minZoom(cyRef.zoom() - 0.01)
    }
    submitHandler()
  }

  async function submitHandler() {
    var edges = netGraph.filter((x) => x.data.label === "edge").map((x) => {return {source: netGraph.filter((y) => y.data.id === x.data.source.toString())[0].data.label, dest: netGraph.filter((y) => y.data.id === x.data.target.toString())[0].data.label}})
    var machines = netGraph.filter((x) => x.data.type === "machine").map((x) => x.data.machine)
    var routers = netGraph.filter((x) => x.data.type === "router").map((x) => x.data.machine)
    /*var nodes = new Set(nodeToCut)
    var edges = [...new Set(edgeToCut.map(JSON.stringify))].map(JSON.parse)
    await axios.post('http://localhost:8080/graph/separate', {
      machines: JSON.stringify(Array.from(nodes)),
      routers: JSON.stringify(Array.from(nodes)),
      links: JSON.stringify(Array.from(edges))
    });*/
    try {
      var response = await axios.post('http://localhost:8080/submitInput', {
        machines: JSON.stringify(Array.from(machines)),
        routers: JSON.stringify(Array.from(routers)),
        links: JSON.stringify(Array.from(edges))
      });

      let data = JSON.parse(response.data)
      setAtkGraph(JSON.stringify(data["attackGraph"]))
    } catch (error) {
      console.error('Error:', error);
    }
  }

  return (
    <div style={{ width: "100%", position: "relative", cursor: cursor }}>
      <div className="build-panel" style={{padding: "20px", width: "100%", height : `${panelHeight}px`, backgroundColor : "#808080", borderBottom : "3px solid #778899"}}>
        <div>
          <input
            type="file"
            name="Add Machine"
            id="add-machine"
            onChange={addConfiguration}
          />
          <label htmlFor="add-machine" className="input-custom">New machine/router/firewall configuration</label>
        </div>  
        <div className='dropdown'>
          <p>Add to topology: </p>
          <Dropdown
            options={machines}
            onChange={setDevice}
          />
          <button className="input-custom" onClick={addDevice}> + </button>
        </div>
        <div>
          <input 
            type="file" 
            name="merge-toppology" 
            id="merge-topology"
          />
          <label htmlFor="merge-topology" className="input-custom">Upload topology (initialisation/network merging)</label>
        </div>
        <button className="input-custom" onClick={printNetGraph}>Generate Attack Graph</button>
      </div>
          
      {netGraph.length === 0 ?
        <div className="no-item" style={{height: builderHeight}}> No graph displayed </div> :
        <CytoscapeComponent
          cy={(cy) => cyRef = onMouseover(cy)}
          elements={netGraph}
          key={netGraph}
          style={styles}
          stylesheet={stylesheet}
          layout={layout}
        />
      }
    </div>
  );
};

export default TopologyBuilder;
