import { useState } from 'react';
import axios from "axios";

function ConfigurableAttackAgentForm() {

  const host = process.env.REACT_APP_HOST
  const port = process.env.REACT_APP_PORT


  const [techniqueMap, setTechniqueMap] = useState(new Map())
  const [formElements, setFormElements] = useState([])
  const [count, setCount] = useState(0)

  function generateFormField() {
    var techniqueScore = {
      "technique": null,
      "score": 0
    }
    return (
      <label style={{ display: "flex", justifyContent: "space-evenly" }}>
        <input className="input-width"
          type="text"
          value={techniqueMap[count]}
          onChange={(e) => techniqueScore.technique = e.target.value}
        />
        <input className="input-width"
          type="number"
          value={techniqueMap[count]}
          onChange={((e) => setTechniqueMap(prevState => {
            techniqueScore.score = e.target.value
            return prevState.set(techniqueScore.technique, techniqueScore.score)
          }))}
        />
      </label>
    )
  }

  function addFormField() {
    setCount(prevState => prevState + 1)
    setFormElements(prevState => [...prevState, generateFormField()])
  }

  async function sendTechniquesToBackend() {
    try {
      const response = await axios.post(`http://${host}:${port}/attack/custom`, {
        techniqueMap: JSON.stringify(Array.from(techniqueMap.entries()))
      })
    } catch (error) {
      console.error('Error:', error);
    }
  }

  return (
    <div style={{display: "flex", flexDirection:"column"}}>
      <form style={{ display: "flex", flexDirection: "column"}}>
        <label style={{ display: "flex", justifyContent: "space-evenly" }}>
          <label className='input-width'>Technique Name</label>
          <label className='input-width'>Score</label>
        </label>
        
        {generateFormField()}

        {
          formElements.map((item) => <>{item}</>)
        }
      </form>
      <div style={{display: "flex", justifyContent: "space-evenly"}}>
        <button id="add-button" onClick={() => addFormField()}> Add new technique </button>
        <button id="submit-button" onClick={() => sendTechniquesToBackend()}> Submit </button>
      </div>
    </div>
  )
}

export default ConfigurableAttackAgentForm;