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

import org.davidmason.zayf.model.ServerInfo;
import org.davidmason.zayf.view.VersionDetailsView;
import org.zanata.rest.dto.Project;
import org.zanata.rest.dto.ProjectIteration;

import com.google.inject.Inject;

/**
 * Responsible for fetching statistics for a version for display, and responding to various user
 * input at the version level.
 * 
 * @author David Mason, dr.d.mason@gmail.com
 * 
 */
class VersionDetailsController
{

   private VersionDetailsView<?> view;
   private DocumentsController docsController;

   private ServerInfo server;
   private Project project;
   private ProjectIteration version;

   @Inject
   VersionDetailsController(VersionDetailsView<?> view, DocumentsController docsControl)
   {
      this.view = view;
      this.docsController = docsControl;

      view.setShowDocsListener(new ActionListener()
      {

         @Override
         public void actionPerformed(ActionEvent e)
         {
            if (e.getActionCommand().equals("show-documents"))
            {
               docsController.fetchDocumentList(server, project, version);
            }
         }
      });
   }

   /**
    * Look up and show stats for version, and show buttons for version actions.
    * 
    * @param version
    */
   public void showVersion(ServerInfo server, Project project, ProjectIteration version)
   {
      this.server = server;
      this.project = project;
      this.version = version;
      // TODO lookup and display stats and any other relevant info
      view.displayVersion(version);
   }

}
