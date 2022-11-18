import { useState } from "react"
import axios from 'axios';


const Metrics = () => {

    const [mets, setMets] = useState()
    const host = process.env.REACT_APP_HOST
    const port = process.env.REACT_APP_PORT

    const metrics = async() => {
        const response = await axios.get(`http://${host}:${port}/metrics`)
        setMets(JSON.parse(response.data))
        console.log(response)
    }

    return (
        <div>
          <h2 onClick={()=>metrics()}>Metrics</h2>
          {mets == null
            ? <p>Click Metrics To Calculate</p>
            : <ul>
            <li>shortest path: {mets["shortestpath"]}</li>
            <li>mean path length: {mets["meanpathlength"]}</li>
            <li>normalised mean of path lengths: {mets["normalisedmopl"]}</li>
            <li>mode of path lengths: {mets["modepathlength"]}</li>
            <li>sd of path lengths: {mets["sdpathlength"]}</li>
            <li>number of paths: {mets["numberofpaths"]}</li>
            <li>weakest adversary: {mets["weakestadversary"]}</li>
            </ul>
          }
        </div>
    )
}

export default Metrics;