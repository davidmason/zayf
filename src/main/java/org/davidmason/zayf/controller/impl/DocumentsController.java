/*
 * Zayf (Zanata at your Fingertips) - a Zanata client for unstable connections
 * Copyright (C) 2012  Alister Symons and David Mason
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
package org.davidmason.zayf.controller.impl;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.davidmason.zayf.rest.ServerProxy;
import org.davidmason.zayf.util.Util;
import org.davidmason.zayf.view.DocumentsView;
import org.zanata.rest.dto.Project;
import org.zanata.rest.dto.ProjectIteration;
import org.zanata.rest.dto.resource.ResourceMeta;

import com.google.inject.Inject;

/**
 * Responsible for fetching a list of documents for a project-version for display.
 * 
 * @author David Mason, dr.d.mason@gmail.com
 */
class DocumentsController
{

   private ServerProxy server;
   private DocumentsView<?> view;

   @Inject
   DocumentsController(DocumentsView<?> view)
   {
      this.view = view;
   }

   public void setServer(ServerProxy server)
   {
      this.server = server;
   }

   // TODO put server in this method signature
   public void fetchDocumentList(Project project, ProjectIteration version)
   {
      String projectId = project.getId();
      String versionId = version.getId();
      view.setTitle(project.getName() + " : " + versionId);
      view.showDocumentsLoading();
      (new FetchDocumentListWorker(projectId, versionId, server)).execute();
   }

   private static DefaultTreeModel buildTreeModel(String projectId, String versionId,
                                                  List<ResourceMeta> documents)
   {
      // TODO make this more representative of project-version?
      // will probably be hidden anyway though.
      DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(projectId + " : " + versionId);

      // make sure documents are in alphabetical order
      Collections.sort(documents, new Comparator<ResourceMeta>()
      {

         @Override
         public int compare(ResourceMeta doc1, ResourceMeta doc2)
         {
            return Collator.getInstance().compare(doc1.getName(), doc2.getName());
         }
      });

      /*
       * Could offer view options:
       *    - flat or hierarchical view
       *    - filtering by arbitrary string (exact, end of path, anywhere in path)
       *    - different sorting (ascending vs. descending, etc.)
       *    - directories all appear before documents, or after documents
       */

      // could also treat maven module identifiers as directories

      Map<String, DefaultMutableTreeNode> pathNodes = new HashMap<String, DefaultMutableTreeNode>();

      for (ResourceMeta doc : documents)
      {
         //         String localName = getEndOfPath(doc.getName());
         // TODO display with short doc name
         DefaultMutableTreeNode docNode = new DefaultMutableTreeNode(doc, false);

         DefaultMutableTreeNode pathNode =
               addPathNodes(rootNode, pathNodes, getBeginningOfPath(doc.getName()));
         pathNode.add(docNode);
      }

      return new DefaultTreeModel(rootNode);
   }

   // FIXME cutting characters off path
   // FIXME docs in root dir are put under blank folder

   private static DefaultMutableTreeNode
         addPathNodes(DefaultMutableTreeNode rootNode,
                      Map<String, DefaultMutableTreeNode> pathNodes,
                      String pathNoTrailingSlash)
   {
      // 1. is it the root node? (return root node)
      if (pathNoTrailingSlash.isEmpty())
      {
         return rootNode;
      }

      // 1. does this node already exist? (return it)
      DefaultMutableTreeNode pathNode = pathNodes.get(pathNoTrailingSlash);
      if (pathNode != null)
      {
         return pathNode;
      }

      String endOfPath = Util.getEndOfPath(pathNoTrailingSlash);
      pathNode = new DefaultMutableTreeNode(endOfPath, true);
      pathNodes.put(pathNoTrailingSlash, pathNode);

      DefaultMutableTreeNode parentNode;
      String parentPath = getBeginningOfPath(pathNoTrailingSlash);
      if (parentPath.isEmpty())
      {
         parentNode = rootNode;
      }
      else
      {
         parentNode = addPathNodes(rootNode, pathNodes, parentPath);
      }
      parentNode.add(pathNode);
      return pathNode;
   }

   private static String getBeginningOfPath(String path)
   {
      int finalSlash = path.lastIndexOf('/');
      if (finalSlash == -1)
      {
         return "";
      }
      if (finalSlash != path.length() - 1 && finalSlash != 0)
      {
         return path.substring(0, finalSlash);
      }
      throw new RuntimeException("Slash on start or end of path: " + path);
   }

   private class FetchDocumentListWorker extends SwingWorker<TreeModel, Void>
   {

      private String projectId;
      private String versionId;
      private ServerProxy server;

      public FetchDocumentListWorker(String projectId, String versionId, ServerProxy server)
      {
         this.projectId = projectId;
         this.versionId = versionId;
         this.server = server;
      }

      @Override
      protected TreeModel doInBackground() throws Exception
      {
         List<ResourceMeta> documents = server.getDocList(projectId, versionId);
         return buildTreeModel(projectId, versionId, documents);
      }

      @Override
      protected void done()
      {
         TreeModel model;
         try
         {
            model = get();
            view.showDocumentsTree(model);
         }
         catch (InterruptedException e)
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
         catch (ExecutionException e)
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
   }
}
