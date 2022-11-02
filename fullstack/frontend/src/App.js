import './App.css';
import axios from 'axios';
import { useState } from 'react';
import React from 'react';
import CytoscapeComponent from 'react-cytoscapejs';
import cytoscape from 'cytoscape';

import tippy from 'tippy.js';
import Button from 'react-bootstrap/Button';
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';

function App() {

  const [items, setItems] = useState()

  function onMouseover() {
    var cy = cytoscape({
      container: document.getElementById('cy')
    });

    function makePopper(ele) {
      let ref = ele.popperRef(); // used only for positioning

      ele.tippy = tippy(ref, { // tippy options:
        content: () => {
          let content = document.createElement('div');

          content.innerHTML = ele.id();

          return content;
        },
        trigger: 'manual' // probably want manual mode
      });
    }

    cy.ready(function () {
      console.log(cy.elements())
      cy.elements().forEach(function (ele) {
        makePopper(ele);
      });
    });

    cy.elements().unbind('mouseover');
    cy.elements().bind('mouseover', (event) => event.target.tippy.show());

    cy.elements().unbind('mouseout');
    cy.elements().bind('mouseout', (event) => event.target.tippy.hide());
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

  // var elements = JSON.stringify(
  //   [{ data: { id: 'one', label: 'Node 1' }, position: { x: 30, y: 30 } },
  //    { data: { id: 'two', label: 'Node 2' }, position: { x: 100, y: 50 } },
  //    { data: { id: 'three', label: 'Node 3'}, position: { x: 50, y: 100 }}, 
  //    { data: { source: 'one', target: 'two', label: 'Edge from Node1 to Node2' } },
  //    { data: { source: 'one', target: 'three', label: 'Edge from Node1 to Node3' } }]);

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
        // label: 'data(properties.text)',
        label: 'data(label)',        
        'font-size': 30,
        width: 'label',
        padding: 10,
        "text-valign": "center",
        "text-halign": "center",
        shape: function (node) {
          switch (node.data('properties').type) {
            case "LEAF": return 'rectangle'
            case "AND": return 'ellipse'
            case "OR": return 'diamond'
            default: return 'circle'
          }

        },
        color: function (node) {
          switch (node.data('properties').type) {
            case "LEAF": return 'black'
            case "AND": return 'white'
            case "OR": return 'yellow'
            default: return 'white'
          }
        },
        backgroundColor: function (node) {
          if (node.data('properties').type === "AND") {
            return 'green'
          } else if (node.data('properties').type === "OR") {
            return 'red'
          } else {
            return 'white'
          }
        }
      }
    },
    {
      selector: 'edge',
      style: {
        'width': 2,
        'line-color': '#ccc',
        'target-arrow-color': '#ccc',
        'target-arrow-shape': 'triangle',
        'curve-style': 'bezier'
      }
    }
  ]

  return (
    <Container>
      <div className="App">
        <Row>
          <h1>Cyber Attack Tool Chain</h1>
        </Row>
        
        <Button variant="primary" onClick={() => generateGraph()}>Generate Graph</Button>
        
        <div onClick={() => post()}>Post item</div>


        <div>
          {items == null
            ? <p>No items</p>
            : 
            <>
              <p>New item</p>
              {items}
              <h2>Attack Graph</h2>
              <CytoscapeComponent id="cy"
                elements={JSON.parse(items)} style={styles} stylesheet={stylesheet} layout={layout} />
            </>
          }
        </div>
        <div>
          <h2>Metrics</h2>
          <p>Hello world!</p>
        </div>
      </div>
    </Container>
  );
}

export default App;