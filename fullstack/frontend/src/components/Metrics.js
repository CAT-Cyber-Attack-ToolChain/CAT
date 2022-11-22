const metricsContainerStyle = {
  width : "100%",
  backgroundColor : "#C0C0C0",
  padding: "20px",
  height: "200px",
  display : "flex",
  flexDirection: "column",
} 


const Metrics = ({mets}) => {

    return (
        <div style={metricsContainerStyle}>
          <h2>Metrics</h2>
          {mets == null
            ? <p>No metrics to calculate, upload a graph</p>
            : <div style={{overflow : "scroll"}}>
              <div>shortest path: {mets["shortestpath"]}</div>
              <div>mean path length: {mets["meanpathlength"]}</div>
              <div>normalised mean of path lengths: {mets["normalisedmopl"]}</div>
              <div>mode of path lengths: {mets["modepathlength"]}</div>
              <div>sd of path lengths: {mets["sdpathlength"]}</div>
              <div>number of paths: {mets["numberofpaths"]}</div>
              <div>weakest adversary: {mets["weakestadversary"]}</div>
            </div>
          }
        </div>
    )
}

export default Metrics;