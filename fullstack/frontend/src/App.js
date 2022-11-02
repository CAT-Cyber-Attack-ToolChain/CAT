import './App.css';
import axios from 'axios';
import { useState } from 'react';
import React from 'react';
import CytoscapeComponent from 'react-cytoscapejs';
import cytoscape from 'cytoscape';

import popper from 'cytoscape-popper';
import tippy from 'tippy.js';

cytoscape.use(popper);

function App() {

  const [items, setItems] = useState()

  function doStuffOnCy(cy) {
    cy.ready(() => onMouseover(cy))

  }

  function onMouseover(cy) {
    function makePopper(ele) {
      let ref = ele.popperRef(); // used only for positioning
      ele.popper = ele.popper({
        content: () => {
          let div = document.createElement('div');

          div.innerHTML = ele.data('properties').text;
          div.setAttribute("role", "tooltip")
          div.classList.add("tooltip")

          div.style.display = 'none'

          document.body.appendChild(div);

          return div;
        },
        popper: {
          placement: 'auto'
        }
      })
    }

    cy.ready(function () {
      cy.nodes().forEach(function (ele) {
        makePopper(ele);
      });
    });

    cy.removeListener('mouseover');
    cy.on('mouseover', 'node', (event) => event.target.popper.state.elements.popper.style.display = "flex");

    cy.removeListener('mouseout');
    cy.on('mouseout', 'node', (event) => event.target.popper.state.elements.popper.style.display = "none");

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

  /**
 * Wait for an element before resolving a promise
 * @param {String} querySelector - Selector of element to wait for
 * @param {Integer} timeout - Milliseconds to wait before timing out, or 0 for no timeout              
 */
  function waitForElement(querySelector, timeout) {
    return new Promise((resolve, reject) => {
      var timer = false;
      if (document.querySelectorAll(querySelector).length) return resolve();

      const observer = new MutationObserver(() => {
        if (document.querySelectorAll(querySelector).length) {
          observer.disconnect();
          if (timer !== false) clearTimeout(timer);
          return resolve();
        }
      });
      observer.observe(document.body, {
        childList: true,
        subtree: true
      });
      if (timeout) timer = setTimeout(() => {
        observer.disconnect();
        reject();
      }, timeout);
    });
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
    <div className="App">
      <h1>Cyber Attack Tool Chain</h1>
      <div onClick={() => {
        generateGraph()
      }}> Generate Graph</div>

      <div onClick={() => post()}>Post item</div>
      <div>
        {items == null
          ? <p>No items</p>
          :
          <>
            <p>New item</p>
            {items}
            <h2>Attack Graph</h2>
            <CytoscapeComponent cy={function (cy) { doStuffOnCy(cy) }}
              elements={JSON.parse(items)} style={styles} stylesheet={stylesheet} layout={layout} />
          </>
        }
      </div>
      <div id="cy"></div>
      <div>
        <h2>Metrics</h2>
      </div>
    </div>
  );
}

export default App;