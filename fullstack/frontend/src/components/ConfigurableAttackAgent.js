import { useState } from 'react';

function ConfigurableAttackAgentForm() {
  const [techniqueMap, setTechniqueMap] = useState([])
  const [formElements, setFormElements] = useState([])
  const [count, setCount] = useState(0)

  function generateFormField() {

    return (
      <label>Enter techinque name:
        <input
          type="text"
          value={techniqueMap[count]}
          onChange={(e) => setTechniqueMap(prevState => [...prevState, e.target.value])}
        />
      </label>
    )
  }

  function addFormField() {
    setCount(prevState => prevState + 1)
    setFormElements(prevState => [...prevState, generateFormField()])
  }

  return (
    <>
    <form>
      {generateFormField()}

      {
        formElements.map((item) => <>{item}</>)
      }
    </form>

     <button id="cut-button" style={{position: "absolute", zIndex: 1, left: 0, bottom: 0,  margin : "0 0 20px 20px"}} onClick={() => addFormField()}> Add new technique </button>
    </>
  )
}

export default ConfigurableAttackAgentForm;