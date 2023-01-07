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
import Configuration from './components/Configuration';


//TODO: Add configurability for host and port for all requests being sent.

function App() {

  const example =`[{"data" : {"id" : "n8", "label" : "netAccess(webServer,tcp,80)", "properties" : {"machines": ["webServer", "tcp"]}}}, {"data" : {"id" : "e7", "label" : "RULE 2 (remote exploit of a server program)", "properties" : {}, "source" : "n8", "target" : "n6"}}, {"data" : {"id" : "n6", "label" : "execCode(webServer,apache)", "properties" : {"machines": ["webServer", "apache"]}}}, {"data" : {"id" : "e4", "label" : "RULE 5 (multi-hop access)", "properties" : {}, "source" : "n6", "target" : "n3"}}, {"data" : {"id" : "e21", "label" : "RULE 17 (NFS shell)", "properties" : {}, "source" : "n6", "target" : "n20"}}, {"data" : {"id" : "n3", "label" : "netAccess(fileServer,rpc,100005)", "properties" : {"machines": ["fileServer", "rpc"]}}}, {"data" : {"id" : "e2", "label" : "RULE 2 (remote exploit of a server program)", "properties" : {}, "source" : "n3", "target" : "n1"}}, {"data" : {"id" : "n1", "label" : "execCode(fileServer,root)", "properties" : {"machines": ["fileServer", "root"]}}}, {"data" : {"id" : "n20", "label" : "accessFile(fileServer,write,'/export')", "properties" : {"machines": []}}}, {"data" : {"id" : "e27", "label" : "RULE 4 (Trojan horse installation)", "properties" : {}, "source" : "n20", "target" : "n1"}}, {"data" : {"id" : "e19", "label" : "RULE 16 (NFS semantics)", "properties" : {}, "source" : "n20", "target" : "n18"}}, {"data" : {"id" : "n18", "label" : "accessFile(workStation,write,'/usr/local/share')", "properties" : {"machines": []}}}, {"data" : {"id" : "e17", "label" : "RULE 4 (Trojan horse installation)", "properties" : {}, "source" : "n18", "target" : "n16"}}, {"data" : {"id" : "n16", "label" : "execCode(workStation,root)", "properties" : {"machines": ["workStation", "root"]}}}, {"data" : {"id" : "e14", "label" : "RULE 5 (multi-hop access)", "properties" : {}, "source" : "n16", "target" : "n3"}}, {"data" : {"id" : "n0", "label" : "start", "properties" : {"machines": []}}}, {"data" : {"id" : "e9", "label" : "RULE 6 (direct network access)", "properties" : {}, "source" : "n0", "target" : "n8"}}]`  

  const [isConfig, setConfig] = useState(false)
  const [atkGraph, setGraph] = useState()
  const [reachabilityGraph, setReachability] = useState()
  const [topology, setTopology] = useState()
  const [selectedFile, setSelectedFile] = useState(null);
  const [mets, setMets] = useState()
  const [loading, setLoading] = useState()

  const [attackAgent, setAttackAgent] = useState('custom')

  /* Mapping */
  const [mapTopology, setMapTop] = useState([])

  const { containerProps, indicatorEl } = useLoading({
    loading: true,
    indicator: <BallTriangle width="50"/>
  })

  const host = process.env.REACT_APP_HOST
  const port = process.env.REACT_APP_PORT

  return (
    <div className='fill'>
    {isConfig ? 
      <div className="fill" style={{display: "flex", boxSizing: "border-box", flexDirection: "column", overflow: "hidden"}}>
        <SimulationSidebar setAttackAgent={setAttackAgent}/>
        <ReflexContainer orientation="vertical" className='App'>
          <ReflexElement className='topology-builder' minSize='450' style={{overflow: "hidden"}}>          
            <div className='heading'>
              <h2>Network Topology</h2>
            </div>   
            <TopologyBuilder setAtkGraph={setGraph} setReachability={setReachability} setMets={setMets} setLoading={setLoading} toHighlight={mapTopology}/>
          </ReflexElement>

          <ReflexSplitter style={{width: '10px', zIndex: '1'}} className='gutter-vertical' />

          <ReflexElement className='attack-graph' minSize='450'>
            <div className='heading'>
              <h2>Attack Graph</h2>
            </div>   
            {atkGraph == null ?
              <div className="no-item">{!loading && "Please select input file"} {loading && indicatorEl}</div> :
              <Cytoscape attackAgent={attackAgent} graph={atkGraph} reachability={reachabilityGraph} key={atkGraph} setMapTop={setMapTop} loading={loading} loader={indicatorEl}/>
            }
            <Metrics mets={mets} />
          </ReflexElement>

        </ReflexContainer>
      </div>
    :
      <Configuration setConfig={setConfig}/>}
    </div>
  );
}

export default App;
