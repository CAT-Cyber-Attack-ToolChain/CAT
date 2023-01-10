import { useState } from "react"
import "./Configuration.css"
import axios from "axios";
const Configuration = ({setConfig}) => {

    const [address, setAddr] = useState("")
    const [user, setUser] = useState("")
    const [password, setPassword] = useState("")

    const host = process.env.REACT_APP_HOST
    const port = process.env.REACT_APP_PORT

    const submitHandler = async (event) => {
        event.preventDefault()
        await axios.post(`http://${host}:${port}/submitConfig`, {
            address: address,
            user: user,
            password: password
        }).then((response)=>{
            console.log(response.status)
        })
        setConfig(true)
    }

    const submitDocker = async (event) => {
        event.preventDefault()
        await axios.post(`http://${host}:${port}/submitConfig`, {
            address: "bolt://neo4j:7687",
            user: "neo4j",
            password: "password"
        }).then((response)=>{
            console.log(response.status)
        })
        setConfig(true)
    }

    return(
        <div className="container">
            <form onSubmit={submitHandler} className="form">
                <input type="text" value={address} placeholder="Neo4j address" onChange={(e) => setAddr(e.target.value)}/>
                <input type="text" value={user} placeholder="Username" onChange={(e) => setUser(e.target.value)}/>
                <input type="text" value={password} placeholder="Password" onChange={(e) => setPassword(e.target.value)}/>
                <div className="container">
                    <button className="input-custom" id="open-button" type="submit"> Set Config </button>
                    <button className="input-custom" id="open-button" onClick={submitDocker}> Use Docker Config </button>
                </div>
            </form>
        </div>
    )
}

export default Configuration;