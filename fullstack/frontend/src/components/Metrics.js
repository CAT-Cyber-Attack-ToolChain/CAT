import './Metrics.css';
import React from 'react';

const Metrics = ({mets}) => {

  return (
      <div className="metrics-container">
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
        
            <div className='decision'>Mean Path Length
            <p className='text'>{mets["meanpathlength"].toFixed(2)}</p> 
            </div> 
            <div className='decision'>Normalised Mean of Path Lengths
            <p className='text'>{mets["normalisedmopl"].toFixed(2)}</p> 
            </div> 
            <div className='decision'>SD of Path Lengths 
            <p className='text'>{mets["sdpathlength"].toFixed(2)}</p>
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