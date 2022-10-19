from neo4j import GraphDatabase
import logging
import csv
from neo4j.exceptions import ServiceUnavailable

class App:

    def __init__(self, uri, user, password, arcs_path, vertices_path):
        self.driver = GraphDatabase.driver(uri, auth=(user, password))
        self.vertices = []
        self.arcs = []
        with open(vertices_path) as vertices_file, open(arcs_path) as arcs_file:
            vertices = csv.reader(vertices_file, delimiter=',')
            arcs = csv.reader(arcs_file, delimiter=',')
            for vertex in vertices : 
                self.vertices.append(vertex)
            for arc in arcs : 
                self.arcs.append(arc)
            
    def close(self):
        # Don't forget to close the driver connection when you are finished with it
        self.driver.close()

    # Create an attack graph on the Neo4j AuraDB database
    def create_attack_graph(self) :
        with self.driver.session(database="neo4j") as session :
            session.execute_write(self._clear_the_db)
            session.execute_write(self._create_vertices)
            session.execute_write(self._set_rule_and_permission_label)
            session.execute_write(self._create_relations)
       
    # Clear the database
    def _clear_the_db(self, tx):
        query = "MATCH (n) DETACH DELETE n "
        try:
            tx.run(query)
        except ServiceUnavailable as exception:
            logging.error("{query} raised an error: \n {exception}".format(query=query, exception=exception))
            raise

    # Create vertices on the Neo4j AuraDB database
    def _create_vertices(self, tx):
        # To learn more about the Cypher syntax, see https://neo4j.com/docs/cypher-manual/current/
        # The Reference Card is also a good resource for keywords https://neo4j.com/docs/cypher-refcard/current/
        query = ""
        for vertex in self.vertices:
            query += f'CREATE (:vertex {{node_id: toInteger({vertex[0]}),  text: "{vertex[1]}",type: "{vertex[2]}", bool:toInteger({vertex[3]})}}) '
        try:
            tx.run(query)
        # Capture any errors along with the query and data for traceability
        except ServiceUnavailable as exception:
            logging.error("{query} raised an error: \n {exception}".format(query=query, exception=exception))
            raise
    
    # Set rule or permission label to each vertex
    def _set_rule_and_permission_label(self, tx):
        try:
            query = ('MATCH (n) WHERE n.text STARTS WITH "RULE" '
                    'SET n:Rule ')
            tx.run(query)
            query = ('MATCH (n) WHERE NOT (n:Rule) '
                    'SET n:Permission ')
            tx.run(query)
        # Capture any errors along with the query and data for traceability
        except ServiceUnavailable as exception:
            logging.error("{query} raised an error: \n {exception}".format(query=query, exception=exception))
            raise
    
    # Create relations between vertices in an attack graph
    def _create_relations(self, tx):
        for arc in self.arcs:
            query = ""
            query += f'MATCH (dst:vertex {{node_id: toInteger({arc[0]})}}) '
            query += f'MATCH (src:vertex {{node_id: toInteger({arc[1]})}}) '
            query += f'CREATE (src) -[r:To {{step: toInteger({arc[2]})}}]-> (dst) '
            try:
                tx.run(query)
            # Capture any errors along with the query and data for traceability
            except ServiceUnavailable as exception:
                logging.error("{query} raised an error: \n {exception}".format(query=query, exception=exception))
                raise
        return "Create relations successfully"

if __name__ == "__main__":
    # Aura queries use an encrypted connection using the "neo4j+s" URI scheme
    uri = "neo4j+s://42ce3f9a.databases.neo4j.io"
    user = "neo4j"
    password = "qufvn4LK6AiPaRBIWDLPRzFh4wqzgI5x_n2bXHc1d38"
    arcs_path = 'mulval_output/ARCS.CSV'
    vertices_path = 'mulval_output/VERTICES.CSV'
    app = App(uri, user, password, arcs_path, vertices_path)
    app.create_attack_graph()
    app.close()
