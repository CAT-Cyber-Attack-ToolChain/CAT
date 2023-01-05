import './Metrics.css';
import React from 'react';

const metricsContainerStyle = {
  width : "100%",
  backgroundColor: "#0a111f",
  padding: "20px",
  height: "200px",
  display : "flex",
  flexDirection: "column",
} 

// const Metrics = ({mets}) => {

//     return (
//         <div style={metricsContainerStyle}>
//           <h4>Metrics</h4>
//           {mets == null
//             ? <p>No metrics to calculate, upload a graph</p>
//             : <div style={{overflow : "scroll"}}>
//               <div>shortest path: {mets["shortestpath"]}</div>
//               <div>mean path length: {mets["meanpathlength"]}</div>
//               <div>normalised mean of path lengths: {mets["normalisedmopl"]}</div>
//               <div>mode of path lengths: {mets["modepathlength"]}</div>
//               <div>sd of path lengths: {mets["sdpathlength"]}</div>
//               <div>number of paths: {mets["numberofpaths"]}</div>
//               <div>weakest adversary: {mets["weakestadversary"]}</div>
//             </div>
//           }
//         </div>
//     )
// }

const mets2 = {shortestpath : "1", meanpathlength: "2", normalisedmopl: "3", modepathlength: "4", sdpathlength: "5", numberofpaths: "6", weakestadversary: "7"}

const Metrics = ({mets}) => {

  return (
      <div style={metricsContainerStyle}>
        <h4>Metrics</h4>
        {mets == null
          ? <div className="Metrics">
            <div className="column">
              <div className="decision">Shortest Path
                <p className='text'>{mets2["shortestpath"]}</p>  
              </div>
              <div className='decision'>Number of Paths
                <p className='text'>{mets2["numberofpaths"]}</p>
              </div> 
              <div className='decision'>Weakest Adversary
                <p className='text'>{mets2["weakestadversary"]}</p> 
              </div>
            </div>
              
            <div className="column">
              <div className='decision'>Mean Path Length
                <p className='text'>{mets2["meanpathlength"]}</p> 
              </div> 
              <div className='decision'>Normalised Mean of Path Lengths
                <p className='text'>{mets2["normalisedmopl"]}</p> 
              </div> 
              <div className='decision'>SD of Path Lengths 
                <p className='text'>{mets2["sdpathlength"]}</p>
              </div> 
            </div>
              
            <div className="column">
              <div className='decision'>Mode of Path Lengths
                <p className='text'>{mets2["modepathlength"]}</p> 
              </div> 
              <div className='decision'>Median of Path Lengths
                <p className='text'>{mets2["sdpathlength"]}</p> 
              </div> 
              <div className='filler'>Filler text
                <p className='fillerText'>{mets2["sdpathlength"]}</p>
              </div>
            </div>
          </div>
          : <p>No metrics to calculate, upload a graph</p>
        }
      </div>
  )
}

export default Metrics;