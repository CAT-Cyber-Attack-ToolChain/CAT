import { useState } from "react"
import "./Configuration.css"
const Configuration = ({setConfig}) => {

    const [address, setAddr] = useState("")
    const [user, setUser] = useState("")
    const [password, setPassword] = useState("")

    const submitHandler = (event) => {
        event.preventDefault()
        console.log(address)
        console.log(user)
        console.log(password)
        setConfig(true)
    }

    return(
        <div className="form-container">
            <form onSubmit={submitHandler} className="form">
                <input type="text" value={address} placeholder="Neo4j address" onChange={(e) => setAddr(e.target.value)}/>
                <input type="text" value={user} placeholder="Username" onChange={(e) => setUser(e.target.value)}/>
                <input type="text" value={password} placeholder="Password" onChange={(e) => setPassword(e.target.value)}/>
                <button className="input-custom" id="open-button" type="submit"> Set Config </button>
            </form>
        </div>
    )
}

export default Configuration;