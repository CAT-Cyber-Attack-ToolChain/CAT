import './App.css';
import { useState } from 'react';
import React from 'react';
import axios from 'axios';
import Cytoscape from "./components/Cytoscape";
import Metrics from "./components/Metrics";
import { useEffect } from 'react';
function App() {

  const [graph, setGraph] = useState()
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
          setGraph(result)
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
        {graph == null ?
          <div className="no-item"> No graph displayed</div> :
          <Cytoscape graph={graph} />
        }
        <Metrics mets={mets} />
      </div>
      {/* <button onClick={() => test()}>Test</button> */}
    </div>
  );
}

export default App;
