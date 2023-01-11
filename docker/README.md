# Setting up this toolchain using Docker containers

### 0. Prerequisites: ###

`docker` and `docker compose` plugin need to be installed on the delpoying machine to run this project.

Recommended to install Docker Desktop, which includes `compose`: https://docs.docker.com/desktop/

Alternatively, you could install docker and `docker compose` separately:

Install `docker`: 
```
sudo apt-get update
sudo apt install docker.io
```

Install `docker compose`: https://docs.docker.com/compose/install/linux/



### 1. Get/Build relevant images: ###
This toolchain uses three containers, one for the backend server, one for the frontend, and one for the Neo4J database.
The Dockerfiles for backend and the frontend are provided, namely `backend.Dockerfile` and `frontend.Dockerfile`.
You can build them with the following commands: (You might need root permissions)

Run the following from within the `docker` directory.
```
[sudo] docker build --file backend.Dockerfile --tag mulval_git:Dockerfile .
[sudo] docker build --file frontend.Dockerfile --tag npm-16 .
```
The Neo4J image can be pulled from Docker Hub:
```
[sudo] docker pull neo4j
```

### 2. Run the containers using Docker Compose: ###
Once the images are correctly installed, the containers can be started up.
A `compose.yml` file is provided for this purpose. To run it, execute the following command:
```
[sudo] docker compose up
```
That should start up the three containers in Docker.

### 3. Starting the backend: ###
In the `backend` container, the backend needs to be started up so that it can serve the frontend requests.
To do this, execute the following commands in the backend container:
```
backend$> cd fullstack/backend
backend$> ./gradlew run
```

This will set it up so that the server listens for requests on the container's `0.0.0.0:8080`.
This is also where the server exceptions will be printed out.

### 4. Starting the frontend: ###
In the `frontend` container, execute the following commands to start the frontend interface:
```
frontend> cd frontend
frontend$> npm start 
```
Once a successful compilation message pops up, the frontend web UI should be accessible on `localhost:3000`.

## Some Docker help

I recommend installing Docker Desktop for ease of use and diagnosing issues.
However if you choose to not do that, docker commands can be run from the command line to a specific container:
```
docker exec <CONTAINER> <COMMAND> <ARGS>
```

For example:
```
docker exec backend cd fullstack/backend
```

## Advanced Hostname and Port Configurability

Both the frontend and backend read network configurations from config files to use.

On the frontend, this file is the `.env` file, found in the `fullstack/frontend/` directory.
On the backend, this file is the `.conf` file, found in the `fullstack/backend/` directory.

There are also docker versions of these files: `docker.env` and `docker.conf` respectively to configure the docker containers properly,
and they'll be automatically used when the `docker compose` command is executed. 

In case you need to change it, here's how each works.

`fullstack/frontend/.env`:

```
REACT_APP_HOST=<YOUR_HOST_NAME>
REACT_APP_PORT=<YOUR_PORT>
```

This hostname and port will be used to send HTTP requests to communicate with the backend. 
Do not change it unless you also configure the backend accordingly.

`fullstack/backend/.conf`:

```
frontend:[<PROTOCOL>]:<HOSTNAME>:<PORT>
backend:[<PROTOCOL>]:<HOSTNAME>:<PORT>
neo4j:[<PROTOCOL>]:<HOSTNAME>:<PORT>
```

There are three different directives for the backend configuration: `frontend`, `backend` and `neo4j`.
The first two are just to commnuicate with the frontend. They need not be supplied with protocols, `http` is assumed.
If required, the `https` protocol can be used.

The `neo4j` one is the configuration for the Neo4J database server.
In the case it is hosted on an external service such as AuraDB, the protocol used should be `neo4j+s`.
If running locally, it is likely that the `bolt` protocol is used.

You'll need to check which protocol to use for your database server. There are three that are supported: `bolt`, `neo4j`, `neo4j+s`.

<!---
## Explaining what the compose.yml does

 * ### mulval
   It uses the image built from the `mulval.Dockerfile`
--->
