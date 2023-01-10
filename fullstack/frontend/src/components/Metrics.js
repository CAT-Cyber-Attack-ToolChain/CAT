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

const Metrics = ({mets}) => {

  return (
      <div style={metricsContainerStyle}>
        <h4>Metrics</h4>
        {mets == null
          ? 
          <p>No metrics to calculate, upload a graph</p>
          : 
          <div className="Metrics">
            <div className="decision">Shortest Path
              <p className='text'>{mets["shortestpath"]}</p>  
            </div>
            <div className='decision'>Number of Paths
              <p className='text'>{mets["numberofpaths"]}</p>
            </div> 
            <div className='decision'>
              <div> Weakest Adversary </div>
              <p className='text'>{mets["weakestadversary"]}</p> 
            </div>
        
            <div className='decision'>Mean Path Length
              <p className='text'>{mets["meanpathlength"]}</p> 
            </div> 
            <div className='decision'>Normalised Mean of Path Lengths
              <p className='text'>{mets["normalisedmopl"]}</p> 
            </div> 
            <div className='decision'>SD of Path Lengths 
              <p className='text'>{mets["sdpathlength"]}</p>
            </div> 
          
            
            <div className='decision'>Mode of Path Lengths
              <p className='text'>{mets["modepathlength"]}</p> 
            </div> 
            <div className='decision'>
              <div> Median of Path Lengths </div>
              <p className='text'>{mets["medianpathlength"]}</p> 
            </div> 
          </div>
        }
      </div>
  )
}

export default Metrics;