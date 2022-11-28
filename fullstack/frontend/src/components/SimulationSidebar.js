import React, { useState } from 'react'
import * as FaIcons from "react-icons/fa";
import * as AiIcons from "react-icons/ai";
import './SimulationSidebar.css';
import { IconContext } from 'react-icons'

function SimulationSidebar() {
  const [sidebar, setSidebar] = useState(false)

  const showSidebar = () => setSidebar(!sidebar)

  return (
    <>
      <IconContext.Provider value={{color: '#fff'}}>
        <div>
        <div className='simulationSidebar'>
          <div className='menu-icons'>
            <FaIcons.FaBars onClick={showSidebar}/>
          </div>
        </div>
        <div className={sidebar ? 'menu-content active' : 'menu-content'}>
          <ul className='menu-content-items'>
            <li className='menu-icons' onClick={showSidebar}>
              <AiIcons.AiOutlineClose />
            </li>
            <p>Hiya!</p>
          </ul>
        </div>
        </div>
      </IconContext.Provider>
    </>
  )
}

export default SimulationSidebar
