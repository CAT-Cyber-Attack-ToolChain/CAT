import React, { useState } from 'react'
import * as FaIcons from "react-icons/fa";
import * as AiIcons from "react-icons/ai";
import './SimulationSidebar.css';
import { IconContext } from 'react-icons'
import ConfigurableAttackAgentForm from './ConfigurableAttackAgent';

import Radio from '@mui/material/Radio';
import RadioGroup from '@mui/material/RadioGroup';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormControl from '@mui/material/FormControl';
import FormLabel from '@mui/material/FormLabel';


const SimulationSidebar = ({setAttackAgent}) => {
  const [sidebar, setSidebar] = useState(false)
  const [value, setValue] = React.useState('custom');

  const handleSimulationSelect = (event) => {
    setValue(event.target.value)
    setAttackAgent(event.target.value)
  };

  const showSidebar = () => setSidebar(!sidebar)

  const radioButtonStyle = {
    color: "white",
    '&.Mui-checked': {
      color: "#05b2dc",
    },
  }


  return (
    <>
      <IconContext.Provider value={{color: '#fff'}}>
        <div>
        <div className='simulationSidebar'>
          <div className='menu-icons' onClick={showSidebar}>
            <FaIcons.FaBars/>
          </div>
        </div>
        <div className={sidebar ? 'menu-content active' : 'menu-content'}>
          <ul className='menu-content-items'>
            <li className='menu-icons' onClick={showSidebar}>
              <AiIcons.AiOutlineClose />
            </li>
            <form>
                <FormControl>
                  <FormLabel id="simulation-agent-radio-group" className="radio-group">Simulation Attack Agent</FormLabel>
                  <RadioGroup
                    aria-labelledby="simulation-agent-radio-group"
                    defaultValue="custom"
                    value={value}
                    onChange={handleSimulationSelect}
                    name="sim-radio-buttons-group"
                  >
                    <FormControlLabel value="wannacry" control={<Radio sx={radioButtonStyle}/>} label="WannaCry" sx={{color : "#05b2dc"}}/>
                    <FormControlLabel value="revil" control={<Radio sx={radioButtonStyle} />} label="REvil" sx={{color : "#05b2dc"}}/>
                    <FormControlLabel value="t9000" control={<Radio sx={radioButtonStyle} />} label="T9000" sx={{color : "#05b2dc"}}/>
                    <FormControlLabel value="synack" control={<Radio sx={radioButtonStyle} />} label="SynAck" sx={{color : "#05b2dc"}}/>
                    <FormControlLabel value="wiper" control={<Radio sx={radioButtonStyle} />} label="Wiper" sx={{color : "#05b2dc"}}/>
                    <FormControlLabel value="custom" control={<Radio sx={radioButtonStyle} />} label="Custom" sx={{color : "#05b2dc"}}/>
                  </RadioGroup>
                </FormControl>
                <ConfigurableAttackAgentForm />
              
            </form>

            
            

          </ul>
        </div>
        </div>
      </IconContext.Provider>
    </>
  )
}

export default SimulationSidebar
