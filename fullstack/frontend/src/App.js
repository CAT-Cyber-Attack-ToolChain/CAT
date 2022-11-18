import './App.css';
import { useState } from 'react';
import React from 'react';
import axios from 'axios';
import Cytoscape from "./components/Cytoscape";
import Metrics from "./components/Metrics";
import { useEffect } from 'react';
import Topology from './components/Topology';
function App() {

  const [atkGraph, setGraph] = useState()
  const [topology, setTopology] = useState()
  const [selectedFile, setSelectedFile] = useState(null);
  const [mets, setMets] = useState()

  useEffect(() => {

    const handleSubmission = async () => {
      const formData = new FormData();

      formData.append('File', selectedFile);

      await fetch(
        'http://localhost:8080/submitInput',
        {
          method: 'POST',
          body: formData,
        }
      ).then((response) => response.json())
        .then((result) => {
          let parsed = JSON.parse(result)
          console.log(parsed)
          setGraph(JSON.stringify(parsed['attackGraph']))
          setTopology(JSON.stringify(parsed['topologyGraph']))
          setMets(getMetrics())
        }).catch((error) => {
          console.error('Error:', error);
        });
    }

    // skip the initial render
    if (selectedFile !== null) {
      handleSubmission()
    }
  }, [selectedFile])

  async function getMetrics() {
    const response = await axios.get('http://localhost:8080/metrics')
    setMets(JSON.parse(response.data))
  }

  const changeHandler = (event) => {
    if (event.target.files.length > 0) {
      setSelectedFile(event.target.files[0]);
    }
  };

  const test = async () => {
    const response = await axios.get('http://localhost:8080/test')
    console.log(response)
  }


const sample = `[
  {"data" : {"id" : "n0", "label" : "internet", "properties" : {"bool": 0, "text": "internet", "type": "OR", "node_id": 0}}},
  {"data" : {"id" : "n1", "label" : "webServer", "properties" : {"bool": 0, "text": "webServer", "type": "OR", "node_id": 1}}}, 
  {"data" : {"id" : "n3", "label" : "fileServer", "properties" : {"bool": 0, "text": "fileServer", "type": "OR", "node_id": 3}}}, 
  {"data" : {"id" : "n5", "label" : "workStation", "properties" : {"bool": 0, "text": "workStation", "type": "OR", "node_id": 5}}}, 
  {"data" : {"id" : "n7", "label" : "H", "properties" : {"bool": 0, "text": "H", "type": "OR", "node_id": 7}}},
  {"data" : {"id" : "e1", "label" : "edge", "properties" : {}, "source" : "n0", "target" : "n1"}}, 
  {"data" : {"id" : "e2", "label" : "edge", "properties" : {}, "source" : "n1", "target" : "n0"}},
  {"data" : {"id" : "e3", "label" : "edge", "properties" : {}, "source" : "n1", "target" : "n3"}},
  {"data" : {"id" : "e4", "label" : "edge", "properties" : {}, "source" : "n1", "target" : "n5"}},
  {"data" : {"id" : "e9", "label" : "edge", "properties" : {}, "source" : "n3", "target" : "n7"}}, 
  {"data" : {"id" : "e10", "label" : "edge", "properties" : {}, "source" : "n5", "target" : "n0"}}, 
  {"data" : {"id" : "e11", "label" : "edge", "properties" : {}, "source" : "n5", "target" : "n1"}}, 
  {"data" : {"id" : "e12", "label" : "edge", "properties" : {}, "source" : "n5", "target" : "n3"}}, 
  {"data" : {"id" : "e13", "label" : "edge", "properties" : {}, "source" : "n5", "target" : "n7"}}, 
  {"data" : {"id" : "e14", "label" : "edge", "properties" : {}, "source" : "n7", "target" : "n7"}}
]`

  // var elements = JSON.stringify(
  //   [{ data: { id: 'one', label: 'Node 1' }, position: { x: 30, y: 30 } },
  //    { data: { id: 'two', label: 'Node 2' }, position: { x: 100, y: 50 } },
  //    { data: { id: 'three', label: 'Node 3'}, position: { x: 50, y: 100 }}, 
  //    { data: { source: 'one', target: 'two', label: 'Edge from Node1 to Node2' } },
  //    { data: { source: 'one', target: 'three', label: 'Edge from Node1 to Node3' } }]);

  return (
    <div className="App">
      <h1>Cyber Attack Tool Chain</h1>

      <div>
        <div className="inner-button">
          <input className="input-button" type="file" name="file" onChange={changeHandler} />
        </div>
        <div id="toolchain-container">
          <div id="attack-graph">
            {atkGraph == null ?
              <div className="no-item"> No graph displayed</div> :
              <Cytoscape graph={atkGraph} key={atkGraph} />
            }
            <Metrics mets={mets} />
          </div>
          <div id="topology">
            {topology == null ?
              <div className="no-item"> No graph displayed </div> :
              <Topology graph={topology} key={topology} />
            }
          </div>
        </div>
        
      </div>
      {/* <button onClick={() => test()}>Test</button> */}
    </div>
  );
}

export default App;
