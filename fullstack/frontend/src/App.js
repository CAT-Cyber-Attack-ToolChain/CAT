import './App.css';
import { useState } from 'react';
import React from 'react';
import Cytoscape from "./components/Cytoscape";
import Metrics from "./components/Metrics";
function App() {

  const [graph, setGraph] = useState()
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
      console.log(graph)

      formData.append('File', selectedFile);

      await fetch(
        'http://localhost:8080/submitInput',
        {
          method: 'POST',
          body: formData,
        }
      ).then((response) => response.json())
       .then((result) => setGraph(result))
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
      <div className="App">
        <h1>Cyber Attack Tool Chain</h1>
        <div>

          <div class="inner-button"> 
            <input class="input-button" type="file" name="file" onChange={changeHandler} />
            <button class="generate-button" onClick={() => handleSubmission()}>Generate Graph</button>
          </div>

          {graph == null ?
              
              <div class="no-item"> No graph displayed</div>

              :

              <Cytoscape graph={graph}/>
          }

            <Metrics/>
        </div>      
      </div>
  );
}

export default App;