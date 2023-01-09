import { useEffect, useState } from 'react';
import Dropdown from "react-dropdown";
import axios from "axios";

function ConfigurableAttackAgentForm() {

  const host = process.env.REACT_APP_HOST
  const port = process.env.REACT_APP_PORT


  const [techniqueMap, setTechniqueMap] = useState(new Map())
  const [formElements, setFormElements] = useState([])
  const [count, setCount] = useState(0)

  const [defaultTechniqueMap, setDefaultTechniqueMap] = useState({})

  useEffect(() => {
    async function populatesDefaultTechniqueMap() {
      try {
         await axios.get(`http://${host}:${port}/attack/defaults`).then((result) => {
          var obj = JSON.parse(result.data)
          setDefaultTechniqueMap(obj)
        })
        
      } catch (error) {
        console.error("Error: ", error)
      }
    }

    populatesDefaultTechniqueMap()
  }, [])

  function setDefaultScore(technique, dropdownId) {
    const ele = document.getElementById(`score-${dropdownId}`)
    var event = new Event('input', {
      'bubbles': true,
      'cancelable': true
    })
    ele.value = defaultTechniqueMap[technique]
    ele.dispatchEvent(event)
  }

  function generateFormField(dropdown=false) {
    var techniqueScore = {
      "technique": null,
      "score": 0
    }
    setCount(prevState => prevState + 1)
    console.log(count)
    return (
      <label key={count}  style={{ display: "flex", justifyContent: "space-evenly" }}>

        {dropdown ?
        <Dropdown className="input-width" options={Object.keys(defaultTechniqueMap)} onChange={(option) => {
          techniqueScore.technique = option.value
          setDefaultScore(option.value, count)
        }}/>
        :
        <input className="input-width"
          type="text"
          value={techniqueMap[count]}
          onInput={(e) => techniqueScore.technique = e.target.value}
        />}
        <input className="input-width"
          id={`score-${count}`}
          type="number"
          value={techniqueMap[count]}
          onInput={((e) => setTechniqueMap(prevState => {
            techniqueScore.score = e.target.value
            return prevState.set(techniqueScore.technique, techniqueScore.score)
          }))}
        />
      </label>
    )
  }

  function callWithoutSubmit(event, func) {
    // to prevent form submission
    event.preventDefault()
    func()
  }

  function addFormField() {
    setFormElements(prevState => [...prevState, generateFormField(true)])
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

        {
          formElements.map((item) => item)
        }
      </form>
      <div style={{display: "flex", justifyContent: "space-evenly"}}>
        <button className="input-custom" id="add-button" onClick={(event) => callWithoutSubmit(event, addFormField)}> Add new technique </button>
        <button className="input-custom" id="submit-button" onClick={(event) => callWithoutSubmit(event, sendTechniquesToBackend)}> Submit </button>
      </div>
    </div>
  )
}

export default ConfigurableAttackAgentForm;