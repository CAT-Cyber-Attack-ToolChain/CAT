import './App.css';
import { useState } from 'react';
import React from 'react';
import Cytoscape from "./components/Cytoscape"
import Metrics from "./components/Metrics"
import Button from 'react-bootstrap/Button';
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';



function App() {

  const [attackGraph, setAttackGraph] = useState()
  const [topologyGraph, setTopologyGraph] = useState()
  const [selectedFile, setSelectedFile] = useState();
	const [isFilePicked, setIsFilePicked] = useState(false);

  const changeHandler = (event) => {
    if (event.target.files.length > 0) {
      setSelectedFile(event.target.files[0]);
		  setIsFilePicked(true);
    } else {
      setIsFilePicked(false);
    }
	};

  const handleSubmission = async () => {
    if (isFilePicked) {
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
         setAttackGraph(JSON.stringify(parsed['attackGraph']))
         setTopologyGraph(JSON.stringify(parsed['topologyGraph']))
         console.log(JSON.stringify(parsed['topologyGraph']))
       })
       .catch((error) => {
				console.error('Error:', error);
			});
    } else {
      alert("Please upload an input file!");
    }
  }



  // var elements = JSON.stringify(
  //   [{ data: { id: 'one', label: 'Node 1' }, position: { x: 30, y: 30 } },
  //    { data: { id: 'two', label: 'Node 2' }, position: { x: 100, y: 50 } },
  //    { data: { id: 'three', label: 'Node 3'}, position: { x: 50, y: 100 }}, 
  //    { data: { source: 'one', target: 'two', label: 'Edge from Node1 to Node2' } },
  //    { data: { source: 'one', target: 'three', label: 'Edge from Node1 to Node3' } }]);



  return (
    <Container>
      <div className="App">
        <Row>
          <h1>Cyber Attack Tool Chain</h1>
        </Row>

        <input type="file" name="file" onChange={changeHandler} />
        
        <button onClick={() => handleSubmission()}>Generate Graph</button>

        <div>
          {attackGraph == null
            ? <p>No items</p>
            : 
            <>
              <h2>Attack Graph</h2>
              <Cytoscape items={attackGraph}></Cytoscape>
            </>
          }
        </div>
        <Metrics/>
      </div>
    </Container>
  );
}

export default App;