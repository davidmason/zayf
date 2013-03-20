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
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;

import org.davidmason.zayf.ui.ProjectsTree;
import org.davidmason.zayf.view.ProjectTreeView;

/**
 * Default Swing implementation of {@link ProjectTreeView}.
 * 
 * @author David Mason, dr.d.mason@gmail.com
 * 
 */
class SwingProjectTreeView extends JPanel implements ProjectTreeView<Component>
{

   private static final String NO_SERVER_SELECTED = "No server selected.";
   private static final String LOADING_PROJECTS = "Loading projects...";

   private static final long serialVersionUID = 1L;

   private JScrollPane treeView;
   private ProjectsTree projectsTree;

   private JLabel noServerLabel, loadingProjectsLabel;
   private JPanel noServerPanel, loadingProjectsPanel;

   public SwingProjectTreeView()
   {
      buildGui();
   }

   private void buildGui()
   {
      setLayout(new BorderLayout());
      //      rootNode = new DefaultMutableTreeNode(controller.getServerUrl());
      //      treeModel = new DefaultTreeModel(rootNode);
      // TODO hide root node?

      // Note: this does not show anything initially
      //       instead, it just waits until instructed by the controller
      //       to load up a tree
      projectsTree = new ProjectsTree();
      treeView = new JScrollPane(projectsTree);

      buildNoServerPanel();
      buildLoadingProjectsPanel();

      add(noServerPanel, BorderLayout.CENTER);
   }

   private void buildNoServerPanel()
   {
      noServerPanel = new JPanel();
      noServerLabel = new JLabel(NO_SERVER_SELECTED);
      noServerPanel.add(noServerLabel);
   }

   private void buildLoadingProjectsPanel()
   {
      loadingProjectsPanel = new JPanel();
      loadingProjectsLabel = new JLabel(LOADING_PROJECTS);
      loadingProjectsPanel.add(loadingProjectsLabel);
   }

   @Override
   public void showProjectsLoading()
   {
      removeAll();
      add(loadingProjectsPanel, BorderLayout.CENTER);
      revalidate();
      repaint();
   }

   @Override
   public void showProjectTree(TreeModel model)
   {
      projectsTree.setModel(model);

      removeAll();
      add(treeView, BorderLayout.CENTER);
      revalidate();
      repaint();
   }

   @Override
   public void addSelectionListener(TreeSelectionListener listener)
   {
      projectsTree.addTreeSelectionListener(listener);
   }

   @Override
   public Component asWidget()
   {
      return this;
   }
}
