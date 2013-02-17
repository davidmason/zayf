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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.zanata.common.EntityStatus;
import org.zanata.rest.dto.Project;
import org.zanata.rest.dto.ProjectIteration;

/**
 * Responsible for displaying basic details and a list of versions for a
 * project.
 * 
 * @author David Mason, dr.d.mason@gmail.com
 * 
 */
public class ProjectDetailsView extends JPanel
{

   private static final long serialVersionUID = 1L;

   private static final String NO_PROJECT_SELECTED = "No project selected";
   private static final String NO_VERSIONS = "No versions to display";
   private static final String ID_FIELD_NAME = "ID: ";
   private static final String NAME_FIELD_NAME = "Name: ";
   private static final String DESC_FIELD_NAME = "Desc: ";

   private JSplitPane projectPanel;
   private JPanel projectDetailPanel, noProjectPanel, versionPanel, noVersionsPanel;

   private JLabel noProjectLabel, noVersionLabel, idLabel, nameLabel, descLabel;

   private boolean showingProject;
   private boolean showingVersions;

   public ProjectDetailsView()
   {
      buildGui();
   }

   private void buildGui()
   {
      setLayout(new BorderLayout());
      buildProjectPanel();
      buildNoProjectPanel();

      add(noProjectPanel, BorderLayout.CENTER);
      showingProject = false;
   }

   private void buildProjectPanel()
   {
      buildVersionPanel();
      buildNoVersionPanel();
      buildProjectDetailPanel();

      projectPanel =
            new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, projectDetailPanel, noVersionsPanel);
      projectPanel.setDividerLocation(80);
      projectPanel.setDividerSize(3);
      projectPanel.setEnabled(true);

      showingVersions = false;
   }

   private void buildProjectDetailPanel()
   {
      projectDetailPanel = new JPanel();

      idLabel = new JLabel(ID_FIELD_NAME);
      nameLabel = new JLabel(NAME_FIELD_NAME);
      descLabel = new JLabel(DESC_FIELD_NAME);

      projectDetailPanel.add(idLabel);
      projectDetailPanel.add(nameLabel);
      projectDetailPanel.add(descLabel);
   }

   private void buildNoProjectPanel()
   {
      noProjectPanel = new JPanel();
      noProjectLabel = new JLabel(NO_PROJECT_SELECTED);
      noProjectPanel.add(noProjectLabel);
   }

   private void buildVersionPanel()
   {
      // TODO use buttons for each version, flowlayout should be fine
      versionPanel = new JPanel();
      // TODO adjust horizontal and vertical padding
   }

   private void buildNoVersionPanel()
   {
      noVersionsPanel = new JPanel();
      noVersionLabel = new JLabel(NO_VERSIONS);
      noVersionsPanel.add(noVersionLabel);
   }

   /**
    * Display id, name and description for a project.
    * 
    * @param project
    *           for which to show details, or null to show no project.
    */
   public void showProjectDetails(Project project)
   {
      if (project == null)
      {
         if (showingProject)
         {
            remove(projectPanel);
            add(noProjectPanel);
            showingProject = false;
            validate();
         }
         return;
      }

      idLabel.setText(ID_FIELD_NAME + project.getId());
      nameLabel.setText(NAME_FIELD_NAME + project.getName());
      if (project.getDescription() == null)
      {
         descLabel.setText("");
      }
      else
      {
         descLabel.setText(DESC_FIELD_NAME + project.getDescription());
      }

      remove(noProjectPanel);
      add(projectPanel);
      showingProject = true;
      validate();
   }

   public void showVersions(List<ProjectIteration> versions)
   {
      // clear iterations from display
      versionPanel.removeAll();

      if (versions.isEmpty())
      {
         if (showingVersions)
         {
            projectPanel.remove(versionPanel);
            projectPanel.add(noVersionsPanel, JSplitPane.BOTTOM);
            showingVersions = false;
         }
         return;
      }
      // show each version
      for (ProjectIteration version : versions)
      {
         if (version.getStatus() != EntityStatus.OBSOLETE)
         {
            versionPanel.add(buildVersionTile(version));
         }
      }
      projectPanel.remove(noVersionsPanel);
      projectPanel.add(versionPanel, JSplitPane.BOTTOM);
      showingVersions = true;
   }

   private Component buildVersionTile(ProjectIteration version)
   {
      JButton tile = new JButton();
      if (version.getStatus() == EntityStatus.READONLY)
      {
         tile.setBackground(Color.PINK);
         tile.setText(version.getId() + " (read only)");
      }
      else
      {
         tile.setText(version.getId());
      }
      return tile;
   }
}
