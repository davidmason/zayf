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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;
import javax.swing.tree.TreeModel;

import org.davidmason.zayf.rest.ServerProxy;
import org.davidmason.zayf.view.ProjectDetailsView;
import org.zanata.rest.dto.Project;
import org.zanata.rest.dto.ProjectIteration;

import com.google.inject.Inject;

/**
 * Responsible for fetching version info for a project, sending this for display, and responding to
 * selection of a version.
 * 
 * @author David Mason, dr.d.mason@gmail.com
 * 
 */
class ProjectDetailsController
{

   private ProjectDetailsView<?> view;
   private final VersionDetailsController versionDisplayer;
   private List<ProjectIteration> versionList;
   private Project project;
   private ServerProxy server;

   @Inject
   ProjectDetailsController(ProjectDetailsView<?> view,
                            VersionDetailsController versionDetailsController)
   {
      this.view = view;
      this.versionDisplayer = versionDetailsController;
      versionList = null;
      setupVersionSelectionListener();
   }

   private void setupVersionSelectionListener()
   {
      view.setVersionSelectedListener(new ActionListener()
      {

         @Override
         public void actionPerformed(ActionEvent e)
         {
            String versionId = e.getActionCommand();
            if (versionList != null)
            {
               for (ProjectIteration version : versionList)
               {
                  if (version.getId().equals(versionId))
                  {
                     versionDisplayer.showVersion(project, version);
                     return;
                  }
               }
               // didn't find version
               // TODO some kind of error
               System.out.println("Didn't find expected version: " + versionId);
            }
         }
      });
   }

   /**
    * Display slug, name and description for a project, and look up versions
    * for display.
    * 
    * @param project
    *           for which to show details, or null to show no project.
    */
   public void loadProject(Project project, ServerProxy server)
   {
      this.project = project;
      this.server = server;
      view.showProjectDetails(project);

      if (project == null)
      {
         view.showVersions(null);
      }
      else
      {
         view.showVersionsLoading();

         (new FetchVersionListWorker(server, project.getId())).execute();
      }

      // clear version display to avoid confusion
      // since no version is selected
      versionDisplayer.showVersion(project, null);
   }

   private class FetchVersionListWorker extends SwingWorker<List<ProjectIteration>, Void>
   {

      private ServerProxy server;
      private String projectId;

      public FetchVersionListWorker(ServerProxy server, String projectId)
      {
         this.server = server;
         this.projectId = projectId;
      }

      @Override
      protected List<ProjectIteration> doInBackground() throws Exception
      {
         return server.getVersionList(projectId);
      }

      @Override
      protected void done()
      {
         try
         {
            versionList = get();
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
         view.showVersions(versionList);
      }
   }
}
