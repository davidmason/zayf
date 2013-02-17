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
package org.davidmason.zayf.controller;

import java.util.List;

import org.davidmason.zayf.view.ProjectDetailsView;
import org.zanata.rest.dto.Project;
import org.zanata.rest.dto.ProjectIteration;
import org.davidmason.zayf.rest.ServerProxy;

public class ProjectDetailsController
{

   private ProjectDetailsView view;

   public ProjectDetailsController(ProjectDetailsView view)
   {
      this.view = view;
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
      view.showProjectDetails(project);

      List<ProjectIteration> versionList = server.getVersionList(project.getId());
      view.showVersions(versionList);
   }
}
