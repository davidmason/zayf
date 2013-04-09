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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;
import org.davidmason.zayf.cache.Mirror;
import org.davidmason.zayf.model.ServerInfo;
import org.davidmason.zayf.rest.ServerProxyProvider;
import org.davidmason.zayf.view.ProjectTreeView;
import org.zanata.rest.dto.Project;

import com.google.inject.Inject;

/**
 * Responsible for fetching a list of projects for display, and responding to user selection of a
 * project.
 * 
 * @author David Mason, dr.d.mason@gmail.com
 * 
 */
class ProjectTreeController
{

   private Logger log = Logger.getLogger(ProjectTreeController.class);

   private ProjectTreeView<?> view;
   private ServerInfo currentServer;
   private final ServerProxyProvider proxyProvider;
   private final Mirror mirror;

   // TODO use interface for this, and change to action listener pattern
   private ProjectDetailsController projectDetailsDisplayer;


   @Inject
   ProjectTreeController(ProjectTreeView<?> view,
                         ProjectDetailsController projectDetailsController,
                         ServerProxyProvider proxyProvider,
                         Mirror mirror)
   {
      this.view = view;
      this.projectDetailsDisplayer = projectDetailsController;
      this.proxyProvider = proxyProvider;
      this.mirror = mirror;

      view.addSelectionListener(buildProjectSelectionListener());
   }

   private TreeSelectionListener buildProjectSelectionListener()
   {
      TreeSelectionListener listener = new TreeSelectionListener()
      {

         @Override
         public void valueChanged(TreeSelectionEvent e)
         {
            DefaultMutableTreeNode node = null;
            TreePath newLeadSelectionPath = e.getNewLeadSelectionPath();
            if (newLeadSelectionPath != null)
            {
               node = (DefaultMutableTreeNode) newLeadSelectionPath.getLastPathComponent();
            }
            if (node == null)
            {
               projectDetailsDisplayer.loadProject(null, null);
               return;
            }

            // only showing projects in tree
            Project project = (Project) node.getUserObject();
            projectDetailsDisplayer.loadProject(currentServer, project);
         }
      };
      return listener;
   }

   /**
    * Fetches the project list for the currently selected server
    */
   public void fetchProjectList(final ServerInfo serverInfo)
   {
      this.currentServer = serverInfo;
      view.showProjectsLoading();

      // TODO cancel existing project list fetches when starting a new one?

      (new FetchProjectsWorker()).execute();
   }

   private TreeModel fetchProjectsAndBuildModel()
   {

      // create model to populate
      DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();

      List<Project> projectList = proxyProvider.get(currentServer).getProjectList();
      Collections.sort(projectList, new Comparator<Project>()
      {

         @Override
         public int compare(Project proj1, Project proj2)
         {
            return Collator.getInstance().compare(proj1.getName(), proj2.getName());
         }
      });

      // TODO persist project list to database
      //      could use linked-list from server to preserve sorted order
      //      but that is arbitrary so may not want to bother storing that info
      //
      //      Make sure it is done in a way that fits with having extra info
      //      on projects that are tracked or have been looked at in more
      //      detail.
      //
      //      Basically store what we've got, note any changes (could show these
      //      to users in an "activity" feed), and make sure it is compatible
      //      with the storage of more information later.

      mirror.addProjectList(currentServer, projectList);

      List<DefaultMutableTreeNode> projectNodes = new ArrayList<DefaultMutableTreeNode>();
      for (Project project : projectList)
      {
         DefaultMutableTreeNode projectNode = new DefaultMutableTreeNode(project);
         projectNodes.add(projectNode);
         rootNode.add(projectNode);

         // TODO: load child nodes on expansion only.

         //         List<ProjectIteration> versionList = server.getVersionList(project
         //               .getId());
         //         System.out.println("Got version list for project "
         //               + project.getId());
         //         for (ProjectIteration version : versionList) {
         //            DefaultMutableTreeNode iterationBranch = new DefaultMutableTreeNode(
         //                  version);
         //            projectBranch.add(iterationBranch);
         //         }
      }

      TreeModel model = new DefaultTreeModel(rootNode);
      return model;
   }

   // Probably won't use this, but keeping the code here for reference on fetching iterations
//   private void populateProjectNodes(List<DefaultMutableTreeNode> projectNodes)
//   {
//      for (DefaultMutableTreeNode node : projectNodes)
//      {
//
//         // TODO: load child nodes on expansion only.
//         String projectId = ((Project) node.getUserObject()).getId();
//
//         List<ProjectIteration> versionList = serverProxy.getVersionList(projectId);
//         log.info("Got version list for project " + projectId);
//         if (versionList != null)
//         {
//            for (ProjectIteration version : versionList)
//            {
//               DefaultMutableTreeNode iterationBranch = new DefaultMutableTreeNode(version);
//               node.add(iterationBranch);
//            }
//         }
//      }
//   }

   private class FetchProjectsWorker extends SwingWorker<TreeModel, Void>
   {

      @Override
      protected TreeModel doInBackground() throws Exception
      {
         return fetchProjectsAndBuildModel();
      }

      @Override
      protected void done()
      {
         try
         {
            view.showProjectTree(get());
         }
         catch (InterruptedException e)
         {
            log.error("interrupted thread while fetching projects", e);
         }
         catch (ExecutionException e)
         {
            // FIXME show information on UI about failed server communication.
            //       may need to check for type of e.getCause() to provide
            //       different behaviour.
            log.error("error in execution while fetching projects", e);
         }
      }
   }
}
