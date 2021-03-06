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
package org.davidmason.zayf.view.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.davidmason.zayf.view.ProjectDetailsView;
import org.zanata.common.EntityStatus;
import org.zanata.rest.dto.Project;
import org.zanata.rest.dto.ProjectIteration;

/**
 * Default Swing implementation of {@link ProjectDetailsView}.
 * 
 * @author David Mason, dr.d.mason@gmail.com
 * 
 */
class SwingProjectDetailsView extends JPanel implements ProjectDetailsView<Component>
{

   private static final long serialVersionUID = 1L;

   private static final String NO_PROJECT_SELECTED = "No project selected";
   private static final String NO_VERSIONS = "No versions to display";
   private static final String VERSIONS_LOADING = "Loading versions...";
   private static final String ID_FIELD_NAME = "Project ID: ";
   private static final String NAME_FIELD_NAME = "Name: ";
   private static final String DESC_FIELD_NAME = "Desc: ";

   private JSplitPane projectPanel;
   private JPanel projectDetailPanel, noProjectPanel, versionPanel, noVersionsPanel,
         versionsLoadingPanel;

   private JLabel noProjectLabel, noVersionLabel, idLabel, nameLabel, descLabel,
         versionsLoadingLabel;

   private boolean showingProject;

   private ActionListener versionSelectedListener;

   public SwingProjectDetailsView()
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
      buildVersionsLoadingPanel();
      buildProjectDetailPanel();

      projectPanel =
            new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, projectDetailPanel, noVersionsPanel);
      projectPanel.setDividerLocation(80);
      projectPanel.setDividerSize(3);
      projectPanel.setEnabled(true);
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

   private void buildVersionsLoadingPanel()
   {
      versionsLoadingPanel = new JPanel();
      versionsLoadingLabel = new JLabel(VERSIONS_LOADING);
      versionsLoadingPanel.add(versionsLoadingLabel);
   }

   @Override
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

   @Override
   public void showVersionsLoading()
   {
      removeVersionPanels();
      projectPanel.add(versionsLoadingPanel, JSplitPane.BOTTOM);
      revalidate();
   }

   @Override
   public void showVersions(List<ProjectIteration> versions)
   {
      // clear iterations from display
      versionPanel.removeAll();

      if (versions == null || versions.isEmpty())
      {

         removeVersionPanels();
         projectPanel.add(noVersionsPanel, JSplitPane.BOTTOM);
         revalidate();

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
      revalidate();
   }

   private void removeVersionPanels()
   {
      projectPanel.remove(versionPanel);
      projectPanel.remove(noVersionsPanel);
      projectPanel.remove(versionsLoadingPanel);
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
      // version ID will be used by project controller to look up the version to display.
      tile.setActionCommand(version.getId());
      tile.addActionListener(versionSelectedListener);
      return tile;
   }

   @Override
   public void setVersionSelectedListener(ActionListener listener)
   {
      this.versionSelectedListener = listener;
   }

   @Override
   public Component asWidget()
   {
      return this;
   }
}
