import axios from "axios";
import CytoscapeComponent from "react-cytoscapejs";
import "react-dropdown/style.css";
import "./TopologyBuilder.css";
import { useState, useEffect } from "react";
import { MdChevronLeft, MdChevronRight } from "react-icons/md"
import fileDownload from "js-file-download";

const panelHeight = 250

var styles = {
  backgroundColor: "#B1C5CB",
  zIndex: 0,
  position: "relative",
  height: "100%",
  display: "flex",
  width: "100%",
  justifyContent: "center",
  alignItems: "center",
  maxHeight: "100%"
};

var layout = {
  name: "fcose",
  spacingFactor: 3,
  animate: false
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
      color: "#B1C5CB",
      backgroundColor: "#30414b",
    },
  },
  {
    selector: "edge",
    style: {
      width: 20,
      lineColor: "#414952",
      targetArrowColor: "#000",
      arrowScale: 5,
      targetArrowShape: "line",
      curveStyle: "bezier",
      "control-point-step-size": "1000",
    },
  },
  {
    selector: ".highlightNode",
    style: {
      color:"#14213d",
      backgroundColor: "#414952",
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
      lineColor: "#93032e",
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

const TopologyBuilder = ({setAtkGraph, setReachability, setMets, setLoading, toHighlight}) => {

  //initialised once component renders
  var cyRef = undefined
  // network graph values
  const [cursor, setCursor] = useState("default");
  const [netGraph, setNetGraph] = useState([]);
  const [selected, setSelected] = useState(undefined);
  const [machines, setMachines] = useState([]);
  const [curDevice, setCurDevice] = useState(undefined);
  const [nextId, setNextId] = useState(0);
  const [created, setCreated] = useState({});

  const host = process.env.REACT_APP_HOST
  const port = process.env.REACT_APP_PORT
  // used for repeatedly executing function (device slider)
  var interval;

  useEffect(() => {
    // allow user to use mouse wheel to scroll the device list
    var slider = document.getElementById("device-slider")
    slider.addEventListener("wheel", function (e) {
      slider.scrollLeft += (e.deltaY > 0) ? 30 : -30;
    })
  }, []);

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
        if (selected === nodeId) {
          cy.$('#' + selected).removeClass("clickedNode");
          setSelected(undefined);
          return
        }
        if (netGraph.some((x) => x.data.label === "edge" && ((x.data.source === nodeId && x.data.target === selected) || (x.data.source === selected && x.data.target === nodeId)))) {
          cy.$('#' + selected).removeClass("clickedNode");
          cy.$('#' + nodeId).removeClass("clickedNode");
          setSelected(undefined);
          return;
        }
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
    let machineConf = []
    let promises = [];
    for (let machine of file.target.files) {
        let filePromise = new Promise(resolve => {
            let reader = new FileReader();
            reader.readAsText(machine);
            reader.onload = () => {
              return resolve(reader.result)
            };
        });
        promises.push(filePromise);
    }
    Promise.all(promises).then((fileContents) => {
      fileContents.forEach((configs) => {
        JSON.parse(configs).forEach((config) => machineConf.push(config))
      })
      setMachines(prevState => [...prevState,...machineConf])
    })
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

  function clearNetGraph() {
    setNetGraph([]);
    setCreated({});
    setAtkGraph(undefined);
    setReachability(undefined);
  }

  function addDragDropDevice(e) {
    e.preventDefault();
    console.log(curDevice)
    addDevice();
  }

  function printNetGraph() {
    console.log(netGraph)
    // setting zoom
    if (cyRef) {

      cyRef.minZoom(cyRef.zoom() - 0.01)
    }
    submitHandler()
  }

  async function submitHandler() {
    var edges = netGraph.filter((x) => x.data.label === "edge").map((x) => {return {source: netGraph.filter((y) => y.data.id === x.data.source.toString())[0].data.label, dest: netGraph.filter((y) => y.data.id === x.data.target.toString())[0].data.label}})
    var machines = netGraph.filter((x) => x.data.type === "machine").map((x) => x.data.machine)
    var routers = netGraph.filter((x) => x.data.type === "router").map((x) => x.data.machine)
    try {
      setLoading(true)
      var response = await axios.post(`http://${host}:${port}/submitInput`, {
        machines: JSON.stringify(Array.from(machines)),
        routers: JSON.stringify(Array.from(routers)),
        links: JSON.stringify(Array.from(edges))
      });
      let data = JSON.parse(response.data)
      setAtkGraph(JSON.stringify(data["attackGraph"]))
      setReachability(JSON.stringify(data["reachability"]))
      setLoading(false) 
      //setMets(getMetrics())
      setMets(data["metrics"])
    } catch (error) {
      console.error('Error:', error);
      setLoading(false) 
    }
  }

  async function saveGraph() {
    fileDownload(JSON.stringify(netGraph), "output.json")
  }

  function mergeTopology(file) {
    const fr = new FileReader();
    const idMap = {}
    fr.addEventListener("load", (event) => {
      var obj = JSON.parse(event.target.result);
      console.log(obj)
      const n = obj.length
      obj = obj.filter((x) => x.data.label === "edge" || !netGraph.some((y) => y.data.label === x.data.label));
      console.log(obj)
      obj = obj.filter((x) => x.data.label !== "edge" || (obj.some((y) => y.data.id === x.data.source) && obj.some((y) => y.data.id === x.data.target)));
      console.log(obj)
      for (var i = 0; i < obj.length; ++i) {
        idMap[obj[i].data.id] = String(nextId + i);
        obj[i].data.id = idMap[obj[i].data.id];
        if (obj[i].data.label === "edge") {
          obj[i].data.source = idMap[obj[i].data.source];
          obj[i].data.target = idMap[obj[i].data.target];
        }
        created[obj[i].data.label] = true;
        netGraph.push(obj[i]);
      }
      setNetGraph(netGraph);
      setNextId(nextId + n);
      setCreated(created);
      console.log(netGraph);
    });
    fr.readAsText(file.target.files[0]);
  }

  async function getMetrics() {
    const response = await axios.get(`http://${host}:${port}/metrics`)
    setMets(JSON.parse(response.data))
  }

  // helper function to slide device list to the left (1 time)
  function slideLeft() {
    var slider = document.getElementById("device-slider");
    slider.scrollLeft = slider.scrollLeft - 30;
  }

  // helper function to slide device list to the right (1 time)
  function slideRight() {
    var slider = document.getElementById("device-slider");
    slider.scrollLeft = slider.scrollLeft + 30;
  }

  // slide the slider to the left during the time the left icon is clicked
  function slideLeftMouseDown() {
    interval = setInterval(slideLeft, 95);
  }

  function slideLeftMouseUp() {
    clearInterval(interval);
  }

  // slide the slider to the right during the time the left icon is clicked
  function slideRightMouseDown() {
    interval = setInterval(slideRight, 95);
  }

  function slideRightMouseUp() {
    clearInterval(interval);
  }

  const mockAtkGraph = `[{"data" : {"id" : "n8", "label" : "netAccess(webServer, tcp, 80)", "properties" : {"machines": ["webServer"]}}}, {"data" : {"id" : "e7", "label" : "RULE 2(remote exploit of a server program)", "properties" : {}, "source" : "n8", "target" : "n6"}}, {"data" : {"id" : "n6", "label" : "execCode(webServer, apache)", "properties" : {"machines": ["webServer"]}}}, {"data" : {"id" : "e4", "label" : "RULE 5(multi - hop access)", "properties" : {}, "source" : "n6", "target" : "n3"}}, {"data" : {"id" : "e21", "label" : "RULE 17(NFS shell)", "properties" : {}, "source" : "n6", "target" : "n20"}}, {"data" : {"id" : "n3", "label" : "netAccess(fileServer, rpc, 100005)", "properties" : {"machines": ["fileServer"]}}}, {"data" : {"id" : "e2", "label" : "RULE 2(remote exploit of a server program)", "properties" : {}, "source" : "n3", "target" : "n1"}}, {"data" : {"id" : "n1", "label" : "execCode(fileServer, root)", "properties" : {"machines": ["fileServer"]}}}, {"data" : {"id" : "n20", "label" : "accessFile(fileServer, write, '/export')", "properties" : {"machines": ["fileServer"]}}}, {"data" : {"id" : "e19", "label" : "RULE 16(NFS semantics)", "properties" : {}, "source" : "n20", "target" : "n18"}}, {"data" : {"id" : "e27", "label" : "RULE 4(Trojan horse installation)", "properties" : {}, "source" : "n20", "target" : "n1"}}, {"data" : {"id" : "n18", "label" : "accessFile(workStation, write, '/usr/local/share')", "properties" : {"machines": ["workStation"]}}}, {"data" : {"id" : "e17", "label" : "RULE 4(Trojan horse installation)", "properties" : {}, "source" : "n18", "target" : "n16"}}, {"data" : {"id" : "n16", "label" : "execCode(workStation, root)", "properties" : {"machines": ["workStation"]}}}, {"data" : {"id" : "e14", "label" : "RULE 5(multi - hop access)", "properties" : {}, "source" : "n16", "target" : "n3"}}, {"data" : {"id" : "n0", "label" : "start", "properties" : {"machines": []}}}, {"data" : {"id" : "e9", "label" : "RULE 6(direct network access)", "properties" : {}, "source" : "n0", "target" : "n8"}}]`

  return (
    <div style={{ width: "100%", position: "relative", cursor: cursor, height: "100%", display: "flex", flexDirection: "column", boxSizing: "border-box"}}>
      <div className="build-panel" style={{padding: "20px", width: "100%", height : `${panelHeight}px`}}>
        <div className="panel">
          <div>
            <input
              type="file"
              name="Add Machine"
              id="add-machine"
              multiple
              onChange={addConfiguration}
            />
            <label htmlFor="add-machine" className="input-custom">New machine/router/firewall configuration</label>
          </div>
          <div>
            <input 
              type="file" 
              name="merge-toppology" 
              id="merge-topology"
              onChange={mergeTopology}
            />
            <label htmlFor="merge-topology" className="input-custom">Upload existing topology</label>
          </div>
        </div>
        <div className="device-slider-container">
            <label style={{display: "inline-block", border: "1px", padding: "6px 12px"}}>Devices: </label>
            <MdChevronLeft style={{cursor: "pointer"}} onClick={slideLeft} onMouseDown={slideLeftMouseDown} onMouseUp={slideLeftMouseUp} size={40} />
            <div id="device-slider" style={{padding: "6px 12px", overflowX: "scroll", display: "flex", scrollBehavior: "smooth"}}>
              {machines.map((machine) => {
                return (<button className="input-custom device" draggable="true" onDrag={function(){setCurDevice(machine.label)}}>{machine.label}</button>)
              })}
            </div>
            <MdChevronRight style={{cursor: "pointer"}} onClick={slideRight} onMouseDown={slideRightMouseDown} onMouseUp={slideRightMouseUp} size={40} />
        </div>
        <div className="panel">
          <button className="input-custom" onClick={printNetGraph}>Generate Attack Graph</button>
          <div className="panel">
            <button className="input-custom" onClick={saveGraph}> Save Topology Graph </button>
            <button className="input-custom" onClick={clearNetGraph}>Clear Topology Graph</button>
          </div>
        </div>
      </div>
      
  
      {netGraph.length === 0 ?
        <div onDrop={(e) => addDragDropDevice(e)} onDragOver={(e) => e.preventDefault()} style={styles}> No graph displayed </div> :
        <div onDrop={(e) => addDragDropDevice(e)} onDragOver={(e) => e.preventDefault()} style={styles}>
          <CytoscapeComponent
            cy={(cy) => cyRef = onMouseover(cy)}
            elements={netGraph}
            key={netGraph}
            style={styles}
            stylesheet={stylesheet}
            layout={layout}
          />
        </div>
      }
   
    </div>
  );
};

export default TopologyBuilder;
