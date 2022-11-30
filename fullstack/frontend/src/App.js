import './App.css';
import { useCallback, useState } from 'react';
import React from 'react';
import Cytoscape from "./components/Cytoscape";
import Metrics from "./components/Metrics";
import "react-dropdown/style.css";
import TopologyBuilder from './components/TopologyBuilder';
import ConfigurableAttackAgentForm from "./components/ConfigurableAttackAgent"

import 'react-reflex/styles.css'
import { ReflexContainer, ReflexSplitter, ReflexElement } from 'react-reflex'
import { useLoading, BallTriangle } from '@agney/react-loading';
import SimulationSidebar from './components/SimulationSidebar';

//TODO: Add configurability for host and port for all requests being sent.

function App() {

  const [atkGraph, setGraph] = useState()
  const [mets, setMets] = useState()
  const [loading, setLoading] = useState()

  /* Mapping */
  const [mapAtkGraph, setMapAtk] = useState([])
  const [mapTopology, setMapTop] = useState([])

  const { containerProps, indicatorEl } = useLoading({
    loading: true,
    indicator: <BallTriangle width="50" class="loader"/>
  })

  const host = process.env.REACT_APP_HOST
  const port = process.env.REACT_APP_PORT

  return (
    <>
      <SimulationSidebar/>
      <ReflexContainer orientation="vertical" className='App'>
        <ReflexElement className='topology-builder' minSize='450'>
          <TopologyBuilder setAtkGraph={setGraph} toHighlight={mapTopology}/>
        </ReflexElement>

        <ReflexSplitter style={{width: '10px', backgroundColor: '#696969', zIndex: '1'}} className='gutter-vertical' />

        <ReflexElement className='attack-graph' minSize='450'>
          {atkGraph == null ?
            <div className="no-item">{!loading && "Please select input file"} {loading && indicatorEl}</div> :
            <Cytoscape graph={atkGraph} key={atkGraph} map={setMapTop}/>
          }
          <Metrics mets={mets} />
        </ReflexElement>

      </ReflexContainer>
    </>
  );
}

export default App;