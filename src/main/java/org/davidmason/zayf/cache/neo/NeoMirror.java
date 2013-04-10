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
import org.davidmason.zayf.util.Util;
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
import org.zanata.rest.dto.ProjectIteration;
import org.zanata.rest.dto.resource.ResourceMeta;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * <p>
 * Standard Neo4j implementation of {@link Mirror}. This stores all data in a graph database with
 * indexes.
 * </p>
 * 
 * <p>
 * Server reference node is attached to the main reference node by a uniquely typed relationship:
 * 
 * <pre>
 * ref=node(0)
 * ref-[:SERVER_ROOT]->serverRef
 * </pre>
 * 
 * </p>
 * 
 * <p>
 * Servers are attached to the server reference nodes and indexed by URL in index "servers":
 * 
 * <pre>
 * serverRef<-[:ROOT]-server
 * server=node:servers(url="http://...")
 * </pre>
 * 
 * </p>
 * 
 * <p>
 * Projects and versions are treated as nested project nodes to allow more flexible data models in
 * future. Projects are attached to a server, and versions are attached to a project. Incoming
 * relationships of type PROJECT characterize a project node. Both are indexed by id in the
 * "projects" index:
 * 
 * <pre>
 * server<-[:SERVER]-project<-[:PROJECT]-version<-[:PROJECT]-()
 * project=node:projects(id="projectId")
 * version=node:projects(id="versionId")
 * </pre>
 * 
 * </p>
 * 
 * <p>
 * Documents are attached to versions via one or more directory nodes, representing the internal
 * directory structure of the project-version. The entire document-directory structure is connected
 * to a top-level directory node that is attached to the version. Documents are indexed by path
 * (full document name and path) under index "documents":
 * 
 * <pre>
 * version<-[:PROJECT]-directory-[:DIRECTORY*]-document
 * document=node:documents(path="full/path/and/name")
 * </pre>
 * 
 * </p>
 * 
 * @author David Mason, dr.d.mason@gmail.com
 * 
 */
public class NeoMirror implements Mirror
{

   private Logger log = Logger.getLogger(NeoMirror.class);

   private static final String SERVERS_INDEX_NAME = "servers";
   private static final String SERVER_URL_KEY = "url";
   private static final String PROJECTS_INDEX_NAME = "projects";
   private static final String PROJECT_ID_KEY = "id";
   private static final String DOCUMENTS_INDEX_NAME = "documents";
   /** Represents full path, including document name. */
   private static final String DOCUMENT_PATH_KEY = "path";

   private Provider<GraphDatabaseService> databaseProvider;

   /**
    * Relationships are directed towards parent nodes.
    */
   private static enum RelTypes implements RelationshipType
   {
      /** Node relationship to its reference node. e.g. server-[:ROOT]->serverRef */
      ROOT,
      /** Relationship from reference node to the server reference node */
      SERVER_ROOT,
      /**
       * Relationship from any node to the server on which it is hosted (usually top-level projects)
       */
      SERVER,
      /** Relationship from a document or project to its parent project */
      PROJECT,
      /** Relationship from a document or directory to its parent directory */
      DIRECTORY,
      /** Relationship from a text flow to its containing document */
      DOCUMENT
   }

   @Inject
   public NeoMirror(Provider<GraphDatabaseService> databaseProvider)
   {
      this.databaseProvider = databaseProvider;
   }

   @Override
   public void addServer(ServerInfo server)
   {
      // DECISION: use URL as the unique identifier for a server
      // DECISION: map ServerInfo to properties on a node (don't serialize it directly)
      // DECISION: index servers and always look them up from the index by url

      GraphDatabaseService db = databaseProvider.get();
      Index<Node> serverIndex = db.index().forNodes(SERVERS_INDEX_NAME);

      String serverUrl = server.getServerUrl().toString();
      Node serverNode = serverIndex.get(SERVER_URL_KEY, serverUrl).getSingle();

      // create node for server if not present
      if (serverNode == null)
      {
         Node serverRef = ensureServerRef(db);
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
      Index<Node> projectIndex = db.index().forNodes(PROJECTS_INDEX_NAME);

      for (Project project : projects)
      {
         Node projectNode = getProjectNode(server, project);

         if (projectNode == null)
         {
            Transaction tx = db.beginTx();
            try
            {
               projectNode = db.createNode();
               projectNode.setProperty("id", project.getId());
               projectNode.createRelationshipTo(serverNode, RelTypes.SERVER);
               projectIndex.add(projectNode, PROJECT_ID_KEY, project.getId());
               tx.success();
            }
            finally
            {
               tx.finish();
            }
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

   @Override
   public void addVersionList(ServerInfo server, Project project, List<ProjectIteration> versions)
   {
      Node projectNode = getProjectNode(server, project);

      GraphDatabaseService db = databaseProvider.get();
      Index<Node> projectIndex = db.index().forNodes(PROJECTS_INDEX_NAME);

      for (ProjectIteration version : versions)
      {
         Node versionNode = getVersionNode(server, project, version);

         if (versionNode == null)
         {
            Transaction tx = db.beginTx();
            try
            {
               versionNode = db.createNode();
               versionNode.setProperty("id", version.getId());
               versionNode.setProperty("name", version.getId());
               versionNode.createRelationshipTo(projectNode, RelTypes.PROJECT);
               projectIndex.add(versionNode, PROJECT_ID_KEY, version.getId());
               tx.success();
            }
            finally
            {
               tx.finish();
            }
         }

         Transaction tx = db.beginTx();
         try
         {
            if (version.getStatus() != null)
            {
               versionNode.setProperty("status", version.getStatus().toString());
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

   @Override
   public void addDocumentList(ServerInfo server, Project project, ProjectIteration version,
                               List<ResourceMeta> documents)
   {
      Node versionNode = getVersionNode(server, project, version);

      GraphDatabaseService db = databaseProvider.get();
      ExecutionEngine ee = new ExecutionEngine(db);

      // DECISION: every version has a single top-level directory node, to which all
      //           documents for that version are ultimately linked.

      Index<Node> docIndex = db.index().forNodes(DOCUMENTS_INDEX_NAME);

      for (ResourceMeta doc : documents)
      {
         Node docNode = getDocumentNode(ee, versionNode, doc.getName());

         if (docNode == null)
         {
            log.info("creating node for document " + doc.getName());
            Transaction tx = db.beginTx();
            try
            {
               String directoryPath = Util.getBeginningOfPath(doc.getName());
               Node directoryNode = ensureDirectoryNodes(db, versionNode, directoryPath);

               docNode = db.createNode();
               docNode.setProperty(DOCUMENT_PATH_KEY, doc.getName());
               docNode.setProperty("name", Util.getEndOfPath(doc.getName()));

               docNode.createRelationshipTo(directoryNode, RelTypes.DIRECTORY);

               docIndex.add(docNode, DOCUMENT_PATH_KEY, doc.getName());

               tx.success();
            }
            finally
            {
               tx.finish();
            }
         }
         else
         {
            log.info("found existing node for document " + doc.getName());
         }

         // set properties
      }
   }

   /**
    * Check for server root node and create if not present.
    * 
    * When this is complete, the database will have a reference node for servers, as well as any
    * nodes that were already in the database.
    * 
    * @param db
    * @return the server reference node
    */
   private Node ensureServerRef(GraphDatabaseService db)
   {
      Relationship relationshipRefToServerRef =
            db.getReferenceNode().getSingleRelationship(RelTypes.SERVER_ROOT, Direction.OUTGOING);
      Node serverRef;
      if (relationshipRefToServerRef == null)
      {
         Transaction tx = db.beginTx();
         try
         {
            serverRef = db.createNode();
            serverRef.setProperty("name", "servers");
            db.getReferenceNode().createRelationshipTo(serverRef, RelTypes.SERVER_ROOT);
            tx.success();
         }
         finally
         {
            tx.finish();
         }
      }
      else
      {
         serverRef = relationshipRefToServerRef.getEndNode();
      }
      return serverRef;
   }

   private Node getServerNode(ServerInfo server)
   {
      String url = server.getServerUrl().toString();
      GraphDatabaseService db = databaseProvider.get();
      Index<Node> serverIndex = db.index().forNodes(SERVERS_INDEX_NAME);
      return serverIndex.get(SERVER_URL_KEY, url).getSingle();
   }

   private Node getProjectNode(ServerInfo server, Project project)
   {
      ExecutionEngine ee = new ExecutionEngine(databaseProvider.get());
      ExecutionResult result =
            ee.execute("START server=node:servers(url={serverUrl}), " +
                       "project=node:projects(id={projectId}) " +
                       "WHERE server<--project " +
                       "RETURN project",
                       paramMap("serverUrl", server.getServerUrl().toString(), "projectId",
                                project.getId()));
      return getSingleResult(result, "project");
   }

   private Node getVersionNode(ServerInfo server, Project project, ProjectIteration version)
   {
      ExecutionEngine ee = new ExecutionEngine(databaseProvider.get());

      ExecutionResult result =
            ee.execute("START server=node:servers(url={serverUrl}), " +
                       "project=node:projects(id={projectId}), " +
                       "version=node:projects(id={versionId}) " +
                       "WHERE server<--project<--version " +
                       "RETURN version",
                       paramMap("serverUrl", server.getServerUrl(), "projectId", project.getId(),
                                "versionId", version.getId()));

      return getSingleResult(result, "version");
   }

   /**
    * Requires neo4j transaction.
    * 
    * @param db
    * @param ee
    * @param versionNode
    * @return
    */
   private Node ensureTopLevelNode(GraphDatabaseService db, ExecutionEngine ee, Node versionNode)
   {
      ExecutionResult result = ee.execute("START version=node({versionNode}) " +
                                          "MATCH version<-[:PROJECT]-topLevelDir " +
                                          "RETURN topLevelDir",
                                          paramMap("versionNode", versionNode));

      Node topLevelDirNode = getSingleResult(result, "topLevelDir");

      if (topLevelDirNode == null)
      {
         topLevelDirNode = db.createNode();
         topLevelDirNode.setProperty("path", "");
         topLevelDirNode.setProperty("name", "/");
         topLevelDirNode.createRelationshipTo(versionNode, RelTypes.PROJECT);
      }
      return topLevelDirNode;
   }

   /**
    * Requires neo4j transaction.
    * 
    * @param db
    * @param versionNode
    * @param directoryPath
    * @return
    * 
    * @see {@link org.davidmason.zayf.controller.impl.DocumentsController#addPathNodes(...)}
    */
   private Node
         ensureDirectoryNodes(GraphDatabaseService db, Node versionNode, String directoryPath)
   {
      ExecutionEngine ee = new ExecutionEngine(db);

      if (directoryPath.isEmpty())
      {
         return ensureTopLevelNode(db, ee, versionNode);
      }

      String dirName = Util.getEndOfPath(directoryPath);
      String parentPath = Util.getBeginningOfPath(directoryPath);

      Node parentDirNode = ensureDirectoryNodes(db, versionNode, parentPath);

      ExecutionResult result =
            ee.execute("START parentDir=node({parentNode}) " +
                       "MATCH parentDir<-[:DIRECTORY]-dir " +
                       "WHERE dir.path = {dirPath} " +
                       "RETURN dir",
                       paramMap("parentNode", parentDirNode, "dirPath", directoryPath));

      Node dirNode = getSingleResult(result, "dir");

      if (dirNode == null)
      {
         dirNode = db.createNode();
         dirNode.setProperty("path", directoryPath);
         dirNode.setProperty("name", dirName);

         dirNode.createRelationshipTo(parentDirNode, RelTypes.DIRECTORY);
      }
      return dirNode;
   }

   private Node getDocumentNode(ExecutionEngine ee, Node versionNode, String fullPathAndName)
   {
      ExecutionResult result =
            ee.execute("START version=node({versionNode}), doc=node:documents(path={docPath}) " +
                       "WHERE version<-[:PROJECT]-()<-[:DIRECTORY*]-doc " +
                       "RETURN doc",
                       paramMap("versionNode", versionNode, "docPath", fullPathAndName));

      Node docNode = getSingleResult(result, "doc");
      return docNode;
   }

   /**
    * Return a single result for a named return parameter. This method expects only a single result
    * and will log errors if additional results are encountered.
    * 
    * @param result
    * @param returnParamName
    * @return the result node if any, otherwise null.
    */
   private Node getSingleResult(ExecutionResult result, String returnParamName)
   {
      Node match = null;
      Iterator<Node> resultIterator = result.<Node>columnAs(returnParamName);
      if (resultIterator.hasNext())
      {
         match = resultIterator.next();
      }
      while (resultIterator.hasNext())
      {
         // FIXME throw exception in this case.
         log.error("found additional match for '" + returnParamName + "'");
         Node extraNode = resultIterator.next();
         for (String key : extraNode.getPropertyKeys())
         {
            log.error("    " + key + ": " + extraNode.getProperty(key));
         }
      }
      return match;
   }

   // TODO replace with chainable method taking just 2 parameters
   private Map<String, Object> paramMap(String key1, Object value1, String key2, Object value2,
                                        String key3, Object value3)
   {
      Map<String, Object> params = paramMap(key1, value1, key2, value2);
      params.put(key3, value3);
      return params;
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
