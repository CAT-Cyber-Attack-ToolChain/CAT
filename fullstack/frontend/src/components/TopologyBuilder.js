import axios from "axios";
import CytoscapeComponent from "react-cytoscapejs";
import Dropdown from "react-dropdown";
import "react-dropdown/style.css";
import { useState } from "react";

var styles = {
  height: "800px",
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
      targetArrowShape: "triangle",
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

const TopologyBuilder = () => {
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
      console.log(netGraph[0]);
      console.log(netGraph);
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
  }

  function addConfigurationHandler(file) {
    const fr = new FileReader();
    fr.addEventListener("load", (event) => {
      console.log(event.target.result);
      const obj = JSON.parse(event.target.result);
      if (!machines.some((m) => m["label"] === obj["label"])) {
        setMachines([...machines, JSON.parse(event.target.result)]);
      }
    });
    fr.readAsText(file.target.files[0]);
  }

  function setDeviceHandler(option) {
    if (option) {
      setCurDevice(option.value);
    }
  }

  function addDeviceHandler() {
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
        },
      },
    ]);
    setNextId(nextId + 1);
  }

  return (
    <div style={{ width: "100%", position: "relative", cursor: cursor }}>
      <p className='no-margin-p'>Upload a new machine/router/firewall configuration:</p>
      <input
        type="file"
        name="Add Machine"
        onChange={addConfigurationHandler}
      />

      <div className='dropdown'>
        <p className='no-margin-p'>Add a new device: </p>
        <Dropdown
          options={machines}
          onChange={setDeviceHandler}
        />
        <button type="button" onClick={addDeviceHandler}>
          <ion-icon name="add-outline"></ion-icon>
        </button>
      </div>

      <p className='no-margin-p'>Upload a topology file (for initialisation/network merging):</p>
      <input type="file" name="merge-toppology" />
      <br/><br/>
      <button>Generate Attack Graph</button>

          
      {netGraph.length === 0 ?
        <div className="no-item" style={{height: "800px"}}> No graph displayed </div> :
        <CytoscapeComponent
          cy={(cy) => onMouseover(cy)}
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
