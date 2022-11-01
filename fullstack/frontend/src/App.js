import './App.css';
import axios from 'axios';
import { useState } from 'react';
import React from 'react';
import CytoscapeComponent from 'react-cytoscapejs';


function App() {
  const [items, setItems] = useState()

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

  var elements = JSON.stringify(
    [{ data: { id: 'one', label: 'Node 1' }, position: { x: 30, y: 30 } },
     { data: { id: 'two', label: 'Node 2' }, position: { x: 100, y: 50 } },
     { data: { id: 'three', label: 'Node 3'}, position: { x: 50, y: 100 }}, 
     { data: { source: 'one', target: 'two', label: 'Edge from Node1 to Node2' } },
     { data: { source: 'one', target: 'three', label: 'Edge from Node1 to Node3' } }]);

  var styles = { 
    width: '100%', 
    height: '500px',
    backgroundColor: 'black'
  }

  var layout = {
    name: 'breadthfirst'
  }

  var stylesheet = [
    {
      selector: 'node',
      style: {
        label: 'data(id)',
        width: 20,
        height: 20,
        shape: 'circle',
        color: 'white',
        backgroundColor: 'white'
      }
    },
    {
      selector: 'edge',
      style: {
        width: 5,
        color: 'white',
        lineColor: 'turquoise',
        label: 'data(label)'
      }
    }
  ]

  return (
    <div className="App">
      <h1>Cyber Attack Tool Chain</h1>
      <div onClick={() => generateGraph()}> Generate Graph</div>
  
      <div onClick={() => post()}>Post item</div>
      <div>
        {items == null
          ? <p>No items</p>
          : 
          <>
          <p>New item</p>
          {items}
          <h2>Attack Graph</h2>
          <CytoscapeComponent elements={JSON.parse(items)} style={styles} stylesheet={stylesheet} />
          </>
        }
      </div>
      <div>
        <h2>Metrics</h2>
      </div>
    </div>
  );
}

export default App;