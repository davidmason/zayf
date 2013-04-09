/*
 * Zayf (Zanata at your Fingertips) - a Zanata client for unstable connections
 * Copyright (C) 2013  Alister Symons and David Mason
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.davidmason.zayf.cache.neo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.davidmason.zayf.cache.Mirror;
import org.davidmason.zayf.model.ServerInfo;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.zanata.rest.dto.Project;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Standard Neo4j implementation of {@link Mirror}.
 * 
 * @author David Mason, dr.d.mason@gmail.com
 * 
 */
public class NeoMirror implements Mirror
{

   private Logger log = Logger.getLogger(NeoMirror.class);

   private static final String REFERENCE_NODES_INDEX_NAME = "ref";
   private static final String REFERENCE_NODES_KEY = "ref";
   private static final String SERVERS_INDEX_NAME = "servers";
   private static final String SERVER_URL_KEY = "url";
   private Provider<GraphDatabaseService> databaseProvider;

   /**
    * Relationships are directed towards parent nodes.
    */
   private static enum RelTypes implements RelationshipType
   {
      ROOT,
      SERVER_ROOT,
      PROJECT_ROOT,
      DOCUMENT_ROOT,
      SERVER,
      PROJECT,
      DOCUMENT
   }

   @Inject
   public NeoMirror(Provider<GraphDatabaseService> databaseProvider)
   {
      this.databaseProvider = databaseProvider;
      ensureDatabaseStructure();
   }

   /**
    * Check for expected nodes and create any that are not present.
    */
   private void ensureDatabaseStructure()
   {
      GraphDatabaseService db = databaseProvider.get();
      ExecutionEngine ee = new ExecutionEngine(db);
      ee.execute("START root=node(0) " +
                 "CREATE UNIQUE root-[:SERVER_ROOT]->(servers {name: 'Servers'})," +
                 " root-[:PROJECT_ROOT]->(projects {name: 'Projects'})," +
                 " root-[:DOCUMENT_ROOT]->(documents {name: 'Documents'})");

      Index<Node> refNodesIndex = db.index().forNodes(REFERENCE_NODES_INDEX_NAME);

      Node serverRef = getServerRef(db);
      Node projectRef = getProjectRef(db);
      Node docRef = getDocRef(db);

      Transaction tx = db.beginTx();
      try
      {
         refNodesIndex.putIfAbsent(serverRef, REFERENCE_NODES_KEY, "servers");
         refNodesIndex.putIfAbsent(projectRef, REFERENCE_NODES_KEY, "projects");
         refNodesIndex.putIfAbsent(docRef, REFERENCE_NODES_KEY, "documents");
         tx.success();
      }
      finally
      {
         tx.finish();
      }
   }

   private Node getDocRef(GraphDatabaseService db)
   {
      Node docRef =
            db.getReferenceNode().getSingleRelationship(RelTypes.DOCUMENT_ROOT, Direction.OUTGOING)
              .getEndNode();
      return docRef;
   }

   private Node getProjectRef(GraphDatabaseService db)
   {
      Node projectRef =
            db.getReferenceNode().getSingleRelationship(RelTypes.PROJECT_ROOT, Direction.OUTGOING)
              .getEndNode();
      return projectRef;
   }

   private Node getServerRef(GraphDatabaseService db)
   {
      Node serverRef =
            db.getReferenceNode().getSingleRelationship(RelTypes.SERVER_ROOT, Direction.OUTGOING)
              .getEndNode();
      return serverRef;
   }

   // Example ways to access reference nodes
   private void printAllReferenceNodes()
   {
      GraphDatabaseService db = databaseProvider.get();
      Node refNode = db.getReferenceNode();
      log.info("relationships from reference node");
      for (Relationship rel : refNode.getRelationships())
      {
         log.info("relationship type: " + rel.getType() + " name: "
                  + rel.getOtherNode(refNode).getProperty("name"));
      }
      log.info("nodes by lookup in reference index");
      Index<Node> refNodesIndex = db.index().forNodes(REFERENCE_NODES_INDEX_NAME);
      log.info(refNodesIndex.get(REFERENCE_NODES_KEY, "servers").getSingle().getProperty("name"));
      log.info(refNodesIndex.get(REFERENCE_NODES_KEY, "projects").getSingle().getProperty("name"));
      log.info(refNodesIndex.get(REFERENCE_NODES_KEY, "documents").getSingle().getProperty("name"));
   }

   private Node getServerNode(ServerInfo server)
   {
      return getServerNode(server.getServerUrl().toString());
   }

   private Node getServerNode(String url)
   {
      GraphDatabaseService db = databaseProvider.get();
      Index<Node> serverIndex = db.index().forNodes(SERVERS_INDEX_NAME);
      return serverIndex.get(SERVER_URL_KEY, url).getSingle();
   }

   @Override
   public void addServer(ServerInfo server)
   {
      GraphDatabaseService db = databaseProvider.get();

      // DECISION: use URL as the unique identifier for a server
      // DECISION: map ServerInfo to properties on a node (don't serialize it directly)
      // DECISION: index servers and always look them up from the index by url

      Index<Node> serverIndex = db.index().forNodes(SERVERS_INDEX_NAME);

      // look up server
      String serverUrl = server.getServerUrl().toString();
      Node serverNode = serverIndex.get(SERVER_URL_KEY, serverUrl).getSingle();

      Node serverRef = getServerRef(db);
      // create node for server if not present
      if (serverNode == null)
      {
         Transaction tx = db.beginTx();
         try
         {
            serverNode = db.createNode();
            serverNode.setProperty(SERVER_URL_KEY, serverUrl);
            // link to server root
            serverNode.createRelationshipTo(serverRef, RelTypes.ROOT);
            // add to server index
            serverIndex.add(serverNode, SERVER_URL_KEY, serverUrl);
            tx.success();
         }
         finally
         {
            tx.finish();
         }
      }

      // update properties to reflect the values on serverInfo
      // actually other properties may not be necessary, should not be storing data from the user
      // ini file (privacy concerns if migrating the database). Could cache these things but
      // they should really be a reflection of the ini file.
//      server.setProperty(SERVER_NAME_KEY, serverInfo.getServerName());

//      Traversal.traversal().relationships(RelTypes.ROOT, Direction.INCOMING).evaluator(Evaluators.excludeStartPosition()).evaluator(Evaluators.);
//      ExecutionEngine ee = new ExecutionEngine(db);
//      ee.execute("START root=node(" + serverRef.getId() + ") " +
//                 "CREATE UNIQUE root<-[:ROOT]-(server {url: '" + serverInfo.getServerUrl() + "'})");

//      refNode.trav

//      Traversal.traversal().breadthFirst();
//      db.

      //      refNode.
      // Add or update server
//      db.

   }

   @Override
   public void addProjectList(ServerInfo server, List<Project> projects)
   {
      // look up server node
      Node serverNode = getServerNode(server);
      if (serverNode == null)
      {
         log.error("Expected non-null server node, but was null");
      }

      GraphDatabaseService db = databaseProvider.get();
      ExecutionEngine ee = new ExecutionEngine(db);
      for (Project project : projects)
      {

         // look up node for project
         //
         // alternatives:
         //    could look up from project index, then check that it is related to serverNode
         //       this could be handy for a quick check if there are any projects with the
         //       given id.
         //    could make a traversal from serverNode looking for the project id

         ExecutionResult result =
               ee.execute("START server=node({server}) " +
                          "MATCH server<-[:SERVER]-project " +
                          "WHERE project.id = {projectId}" +
                          "RETURN project",
                          paramMap("server", serverNode, "projectId", project.getId()));

         Node projectNode;
         Iterator<Node> resultIterator = result.<Node>columnAs("project");
         if (resultIterator.hasNext())
         {
//            log.info("Using existing node for project " + project.getId());
            projectNode = resultIterator.next();
         }
         else
         {
//            log.info("No node for project " + project.getId() + ", creating.");
            Transaction tx = db.beginTx();
            try
            {
               projectNode = db.createNode();
               projectNode.setProperty("id", project.getId());
               projectNode.createRelationshipTo(serverNode, RelTypes.SERVER);
               tx.success();
            }
            finally
            {
               tx.finish();
            }

            // TODO add project to projects index by name
         }

         if (resultIterator.hasNext())
         {
            log.warn("Matched more than 1 node for project " + project.getId()
                     + ", expected 0 or 1.");
         }

         // add available project details
         Transaction tx = db.beginTx();
         try
         {
            if (project.getDescription() != null)
            {
               projectNode.setProperty("description", project.getDescription());
            }
            if (project.getName() != null)
            {
               projectNode.setProperty("name", project.getName());
            }
            if (project.getStatus() != null)
            {
               projectNode.setProperty("status", project.getStatus().toString());
            }
            tx.success();
         }
         finally
         {
            tx.finish();
         }

         // possibly persist raw xml to a property?

      }
   }

   private Map<String, Object> paramMap(String key1, Object value1, String key2, Object value2)
   {
      Map<String, Object> params = paramMap(key1, value1);
      params.put(key2, value2);
      return params;
   }

   private Map<String, Object> paramMap(String key, Object value)
   {
      Map<String, Object> params = new HashMap<String, Object>();
      params.put(key, value);
      return params;
   }
}
