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

  var elements = JSON.stringify([{ data: { id: 'one', label: 'Node 1' }, position: { x: 30, y: 30 } },
      { data: { id: 'two', label: 'Node 2' }, position: { x: 100, y: 50 } },
      { data: { source: 'one', target: 'two', label: 'Edge from Node1 to Node2' } }]);

  var styles = { 
    width: '100%', 
    height: '200px',
    backgroundColor: 'blue'
  }

  var stylesheet = [
    {
      selector: 'node',
      style: {
        label: 'data(id)',
        width: 20,
        height: 20,
        shape: 'hexagon',
        color: 'white',
        backgroundColor: 'red'
      }
    },
    {
      selector: 'edge',
      style: {
        width: 5,
        lineColor: '#ccc',
        label: 'data(label)'
      }
    }
  ]

  return (
    <div className="App">

      <div onClick={() => generateGraph()}> Generate Graph</div>
  
      <div onClick={() => post()}>Post item</div>
      <div>
        {items == null
          ? <p>No items</p>
          : 
          <>
          <p>New item</p>
          {items}
          <CytoscapeComponent elements={JSON.parse(items)} style={styles} stylesheet={stylesheet} />
          </>
        }
        
      </div>

    </div>
  );
}

export default App;