import './App.css';
import axios from 'axios';
import { useState } from 'react';
import React from 'react';
import Cytoscape from "./components/Cytoscape"

import Button from 'react-bootstrap/Button';
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';



function App() {

  const [items, setItems] = useState()
  const [mets, setMets] = useState()
  const [selectedFile, setSelectedFile] = useState();
	const [isFilePicked, setIsFilePicked] = useState(false);



  const changeHandler = (event) => {
		setSelectedFile(event.target.files[0]);
		setIsFilePicked(true);
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
       .then((result) => setItems(result))
       .catch((error) => {
				console.error('Error:', error);
			});
    } else {
      alert("Please upload an input file!");
    }
  }

  const generateGraph = async () => {
    const response = await axios.get('http://localhost:8080/shoppingList')
    console.log(response)
    setItems(response.data)
  }

  const post = async () => {
    const response = await axios.post('http://localhost:8080/shoppingList', {
      "desc": "Apple 🍎",
      "priority": 5,
      "id": 2040789031
    });
    console.log(response)
  }

  const metrics = async() => {
    const response = await axios.get('http://localhost:8080/metrics')
    setMets(JSON.parse(response.data))
    console.log(response)
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
        
        <div onClick={() => post()}>Post item</div>


        <div>
          {items == null
            ? <p>No items</p>
            : 
            <>
              <h2>Attack Graph</h2>
              
              <Cytoscape items={items}></Cytoscape>
            </>
          }
        </div>
        <div>
          <h2 onClick={()=>metrics()}>Metrics</h2>
          {mets == null
            ? <p>Click Metrics To Calculate</p>
            : <ul>
            <li>shortest path: {mets["shortestpath"]}</li>
            <li>mean path length: {mets["meanpathlength"]}</li>
            <li>normalised mean of path lengths: {mets["normalisedmopl"]}</li>
            <li>mode of path lengths: {mets["modepathlength"]}</li>
            <li>sd of path lengths: {mets["sdpathlength"]}</li>
            <li>number of paths: {mets["numberofpaths"]}</li>
            <li>weakest adversary: {mets["weakestadversary"]}</li>
            </ul>
          }
        </div>
      </div>
    </Container>
  );
}

export default App;