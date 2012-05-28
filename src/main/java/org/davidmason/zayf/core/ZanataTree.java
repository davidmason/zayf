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
/*package org.davidmason.zayf.core;

import java.util.List;
import java.util.ArrayList;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.zanata.rest.dto.Project;
import org.zanata.rest.dto.ProjectIteration;
import org.zanata.rest.dto.resource.ResourceMeta;
import org.zanata.rest.dto.resource.TextFlow;
import org.zanata.rest.dto.resource.TextFlowTarget;
import org.davidmason.zayf.core.ZanataTree.ProjectNode.ProjectIterationNode;
import org.davidmason.zayf.core.ZanataTree.ProjectNode.ProjectIterationNode.ResourceMetaNode;
import org.davidmason.zayf.core.ZanataTree.ProjectNode.ProjectIterationNode.ResourceMetaNode.TextFlowNode;
import org.davidmason.zayf.core.ZanataTree.ProjectNode.ProjectIterationNode.ResourceMetaNode.TextFlowTargetNode;

public class ZanataTree
{
   public class ProjectNode
   {
      private Project project;
      private List<ProjectIterationNode> projectIterations;
      
      public ProjectNode(Project project)
      {
         this.project = project;
      }
      
      public class ProjectIterationNode
      {
         private ProjectNode parent;
         private ProjectIteration projectIteration;
         private List<ResourceMetaNode> resourceMetas;
         
         public class ResourceMetaNode
         {
            private ProjectIterationNode parent;
            private ResourceMeta resourceMeta;
            private List<TextFlowNode> textFlows;
            private List<TextFlowTargetNode> textFlowTargets;
            
            public class TextFlowNode
            {
               private ResourceMetaNode parent; 
               private TextFlow textFlow;
            }
            public class TextFlowTargetNode
            {
               private ResourceMetaNode parent;
               private TextFlowTarget textFlowTarget;
            }
         }
      }
   }

   private String url;
   public List<ProjectNode> projects;

   public ZanataTree(String url)
   {
      this.url = url;
   }
   
   public DefaultTreeModel toTreeModel()
   {
      DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(url);
      DefaultTreeModel tm = new DefaultTreeModel(rootNode);
      
      for (ProjectNode projectNode : projects)
      {
         DefaultMutableTreeNode projectBranch = new DefaultMutableTreeNode(projectNode.project);
         rootNode.add(projectBranch);

         for (ProjectIterationNode iterationNode : projectNode.projectIterations)
         {
            DefaultMutableTreeNode iterationBranch = new DefaultMutableTreeNode(iterationNode.projectIteration);
            projectBranch.add(iterationBranch);
            
            for (ResourceMetaNode docNode : iterationNode.resourceMetas)
            {
               DefaultMutableTreeNode docBranch = new DefaultMutableTreeNode(docNode.resourceMeta);
               iterationBranch.add(docBranch);
               
               for (TextFlowNode tfNode : docNode.textFlows)
               {
                  DefaultMutableTreeNode tfLeaf = new DefaultMutableTreeNode(tfNode.textFlow);
                  docBranch.add(tfLeaf);
               }
               
               for (TextFlowTargetNode tftNode : docNode.textFlowTargets)
               {
                  DefaultMutableTreeNode tftLeaf = new DefaultMutableTreeNode(tftNode.textFlowTarget);
                  docBranch.add(tftLeaf);
               }
            }
         
         }
      }
      return tm;
   }
}
*/
