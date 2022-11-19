import './App.css';
import { useState } from 'react';
import React from 'react';
import Cytoscape from "./components/Cytoscape"
import Metrics from "./components/Metrics"
import Button from 'react-bootstrap/Button';
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';

//TODO: Add configurability for host and port for all requests being sent.

function App() {

  const [items, setItems] = useState()
  const [selectedFile, setSelectedFile] = useState();
	const [isFilePicked, setIsFilePicked] = useState(false);

  const host = process.env.REACT_APP_HOST
  const port = process.env.REACT_APP_PORT

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
        `http://${host}:${port}/submitInput`,
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
          {items == null
            ? <p>No items</p>
            : 
            <>
              <h2>Attack Graph</h2>              
              <Cytoscape items={items}></Cytoscape>
            </>
          }
        </div>
        <Metrics/>
      </div>
    </Container>
  );
}

export default App;
