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
package org.davidmason.zayf.view;

import java.awt.event.ActionListener;
import java.util.List;

import org.zanata.rest.dto.Project;
import org.zanata.rest.dto.ProjectIteration;

/**
 * View interface for displaying basic project details and a list of versions under the project.
 * 
 * @author David Mason, dr.d.mason@gmail.com
 * 
 * @param <WidgetType>
 *           return type for {{@link #asWidget()}
 */
public interface ProjectDetailsView<WidgetType> extends WidgetView<WidgetType>
{

   /**
    * Display id, name and description for a project.
    * 
    * @param project
    *           for which to show details, or null to show no project.
    */
   public void showProjectDetails(Project project);

   /**
    * Display a message indicating that versions are currently loading. This message will
    * automatically clear when {@link #showVersions(List)} is called.
    */
   public void showVersionsLoading();

   /**
    * Display a list of versions from which a user can select a version.
    * 
    * @see #setVersionSelectedListener(ActionListener)
    */
   public void showVersions(List<ProjectIteration> versions);

   /**
    * Set listener for user selection of a version.
    */
   public void setVersionSelectedListener(ActionListener listener);

}
