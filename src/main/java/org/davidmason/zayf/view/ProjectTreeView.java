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

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.TreeModel;

import org.davidmason.zayf.ui.ProjectsTree;

public class ProjectTreeView extends JPanel
{

   private static final long serialVersionUID = 1L;

   private JScrollPane treeView;
   private ProjectsTree projectsTree;

   public ProjectTreeView()
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

      add(treeView, BorderLayout.CENTER);
   }

   public void showProjectTree(TreeModel model)
   {
      projectsTree.setModel(model);
   }
}
