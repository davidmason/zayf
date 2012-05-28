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
package org.davidmason.zayf.ui;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

import org.zanata.rest.dto.Project;
import org.zanata.rest.dto.ProjectIteration;
import org.zanata.rest.dto.resource.ResourceMeta;
import org.zanata.rest.dto.resource.TextFlow;
import org.zanata.rest.dto.resource.TextFlowTarget;

@SuppressWarnings("serial")
public class ProjectsTree extends JTree
{

   ProjectsTree(TreeModel treeModel)
   {
      super(treeModel);
   }

   @Override
   public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf,
                                    int row, boolean hasFocus)
   {
      if (((DefaultMutableTreeNode) value).getUserObject() instanceof Project)
         return ((Project) ((DefaultMutableTreeNode) value).getUserObject()).getName();

      if (((DefaultMutableTreeNode) value).getUserObject() instanceof ProjectIteration)
         return ((ProjectIteration) ((DefaultMutableTreeNode) value).getUserObject()).getId();

      if (((DefaultMutableTreeNode) value).getUserObject() instanceof ResourceMeta)
         return ((ResourceMeta) ((DefaultMutableTreeNode) value).getUserObject()).getName();

      if (((DefaultMutableTreeNode) value).getUserObject() instanceof TextFlow)
         return "TF: " + ((TextFlow) ((DefaultMutableTreeNode) value).getUserObject()).getId();

      if (((DefaultMutableTreeNode) value).getUserObject() instanceof TextFlowTarget)
         return "TFT: "
                + ((TextFlowTarget) ((DefaultMutableTreeNode) value).getUserObject()).getResId();

      return value.toString();
   }

}
