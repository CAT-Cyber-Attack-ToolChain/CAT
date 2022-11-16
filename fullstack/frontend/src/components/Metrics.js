const metricsContainerStyle = {
  width : "100%",
  height : "300px",
  backgroundColor : "#C0C0C0",
  padding: "20px"
} 

const Metrics = ({mets}) => {

    return (
        <div style={metricsContainerStyle}>
          <h2>Metrics</h2>
          {mets == null
            ? <p>No metrics to calculate, upload a graph</p>
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