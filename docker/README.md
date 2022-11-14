# Setting up this toolchain using Docker containers

1. Get/Build relevant images:
    This toolchain uses three containers, one for mulval and the backend server, one for the frontend, and one for the Neo4J database.
    The Dockerfiles for mulval and the frontend are provided, namely `mulval.Dockerfile` and `npm.Dockerfile`.
    You can build them with the following commands:
     ```
      docker build --file mulval.Dockerfile --tag mulval_git:Dockerfile .
      docker build --file npm.Dockerfile --tag node-16 .
    ```
    The Neo4J image can be pulled from Docker Hub:
    ```
    docker pull neo4j
    ```

2. Run the containers using Docker Compose:
    Once the images are correctly installed, the containers can be started up.
    A `compose.yml` file is provided for this purpose. To run it, execute the following command:
    ```
    docker compose up
    ```
    That should start up the three containers in Docker.

3. Starting the backend:
    In the `mulval` container, the backend needs to be started up so that it can serve the frontend requests.
    To do this, execute the following commands in the mulval container:
    ```
    mulval$> cd fullstack/backend
    mulval$> ./gradlew run
    ```

    This will set it up so that the server listens for requests on the container's `0.0.0.0:8080`.
    This is also where the server exceptions will be printed out.

4. Starting the frontend:
    In the `node` container, execute the following commands to start the frontend interface:
    ```
    node$> cd frontend
    node$> npm start 
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
docker exec mulval cd fullstack/backend
```

## Explaining what the compose.yml does

* ### mulval
    It uses the image built from the `mulval.Dockerfile`



* 