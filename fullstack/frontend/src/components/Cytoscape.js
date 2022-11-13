import CytoscapeComponent from 'react-cytoscapejs';
import cytoscape from 'cytoscape';
import popper from 'cytoscape-popper';

cytoscape.use(popper);

function doStuffOnCy(cy) {
    cy.ready(() => onMouseover(cy))
}

function onMouseover(cy) {
    function makePopper(ele) {
        ele.popperDiv = ele.popper({
            content: () => {
                let div = document.createElement('div');

                div.innerHTML = ele.data('properties').text;
                div.setAttribute("role", "tooltip")
                div.classList.add("my-tooltip")

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

    cy.on('mouseover', 'node', (event) => {
        event.target.popperDiv.state.elements.popper.style.display = "flex";
        console.log("Showing")
    });

    cy.removeListener('mouseout');
    cy.on('mouseout', 'node', (event) => event.target.popperDiv.state.elements.popper.style.display = "none");
}
    

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
            label: 'data(id)',
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

  // var example = JSON.stringify(
  //   [{ data: { id: 'one', label: 'Node 1' }, position: { x: 30, y: 30 } },
  //    { data: { id: 'two', label: 'Node 2' }, position: { x: 100, y: 50 } },
  //    { data: { id: 'three', label: 'Node 3'}, position: { x: 50, y: 100 }}, 
  //    { data: { source: 'one', target: 'two', label: 'Edge from Node1 to Node2' } },
  //    { data: { source: 'one', target: 'three', label: 'Edge from Node1 to Node3' } }]);


const Cytoscape = ({items}) => {
    return(
    <CytoscapeComponent cy={(cy) => {doStuffOnCy(cy)}} elements={JSON.parse(items)} style={styles} stylesheet={stylesheet} layout={layout} />)
}

export default Cytoscape;