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

import org.davidmason.zayf.rest.ServerProxy;
import org.davidmason.zayf.view.ProjectTreeView;
import org.zanata.rest.dto.Project;
import org.zanata.rest.dto.ProjectIteration;

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

   private ProjectTreeView<?> view;
   private ServerProxy server;

   // TODO use interface for this, and change to action listener pattern
   private ProjectDetailsController projectDetailsDisplayer;

   @Inject
   ProjectTreeController(ProjectTreeView<?> view,
                         ProjectDetailsController projectDetailsController)
   {
      this.view = view;
      this.projectDetailsDisplayer = projectDetailsController;

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
            projectDetailsDisplayer.loadProject(project, server);
         }
      };
      return listener;
   }

   /**
    * Fetches the project list for the currently selected server
    */
   public void fetchProjectList(final ServerProxy server)
   {
      this.server = server;

      view.showProjectsLoading();

      // TODO cancel existing project list fetches when starting a new one?

      (new FetchProjectsWorker()).execute();
   }

   private TreeModel fetchProjectsAndBuildModel(ServerProxy server)
   {
      // create model to populate
      DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();

      List<Project> projectList = server.getProjectList();
      Collections.sort(projectList, new Comparator<Project>()
      {

         @Override
         public int compare(Project proj1, Project proj2)
         {
            return Collator.getInstance().compare(proj1.getName(), proj2.getName());
         }
      });

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
   private void populateProjectNodes(List<DefaultMutableTreeNode> projectNodes)
   {
      for (DefaultMutableTreeNode node : projectNodes)
      {

         // TODO: load child nodes on expansion only.
         String projectId = ((Project) node.getUserObject()).getId();

         List<ProjectIteration> versionList = server.getVersionList(projectId);
         System.out.println("Got version list for project " + projectId);
         if (versionList != null)
         {
            for (ProjectIteration version : versionList)
            {
               DefaultMutableTreeNode iterationBranch = new DefaultMutableTreeNode(version);
               node.add(iterationBranch);
            }
         }
      }
   }

   private class FetchProjectsWorker extends SwingWorker<TreeModel, Void>
   {

      @Override
      protected TreeModel doInBackground() throws Exception
      {
         return fetchProjectsAndBuildModel(server);
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
