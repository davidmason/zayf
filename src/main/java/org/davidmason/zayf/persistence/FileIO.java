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
package org.davidmason.zayf.persistence;

import java.io.*;
//import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

//import org.zanata.common.EntityStatus;
import org.zanata.common.ContentState;
import org.zanata.rest.dto.*;
//import org.zanata.rest.dto.resource.ResourceMeta;
//import org.davidmason.zayf.rest.*;
import org.zanata.rest.dto.resource.ResourceMeta;
import org.zanata.rest.dto.resource.TextFlow;
import org.zanata.rest.dto.resource.TextFlowTarget;

public class FileIO
{

   //TODO: save in proper format/s
   /**
    * Save project as xml files. Placeholder. get save from project tree node working, then save in
    * correct format/s
    * 
    * @param projectBranch
    *           node containing project
    * @return true if successful
    */
   public static boolean saveProject(DefaultMutableTreeNode projectBranch)
   {
      if (!(projectBranch.getUserObject() instanceof Project))
      {
         System.err.println("Assert failed: Branch node does not contain a Project.");
         return false;
      }

      Project project = (Project) projectBranch.getUserObject();

      File projectfile = new File(project.getName());
      projectfile.mkdir(); //make project directory

      //get iterations
      project.setIterations(new ArrayList<ProjectIteration>());
      for (int i = 0, iterationCount = projectBranch.getChildCount(); i < iterationCount; i++)
      {
         DefaultMutableTreeNode iterationBranch =
               (DefaultMutableTreeNode) projectBranch.getChildAt(i);

         if (!(iterationBranch.getUserObject() instanceof ProjectIteration))
         {
            System.err.println("Assert failed: Branch node does not contain a ProjectIteration.");
            return false;
         }

         ProjectIteration iteration = (ProjectIteration) iterationBranch.getUserObject();
         project.getIterations().add(iteration);

         File iterationfile = new File(project.getName() + "/" + iteration.getId());
         iterationfile.mkdir(); //make Project/ProjectIteration directory

         //get resourceMetas
         for (int j = 0, docCount = iterationBranch.getChildCount(); j < docCount; j++)
         {
            DefaultMutableTreeNode docBranch =
                  (DefaultMutableTreeNode) iterationBranch.getChildAt(j);

            if (!(docBranch.getUserObject() instanceof ResourceMeta))
            {
               System.err.println("Assert failed: Branch node does not contain a ResourceMeta.");
               return false;
            }

            ResourceMeta doc = (ResourceMeta) docBranch.getUserObject();

            File docFile = new File(project.getName() + "/" + iteration.getId() + "/"
                                    + doc.getName() + ".po");
            docFile.getParentFile().mkdirs(); //make Project/ProjectIteration/docpath/doc.po directory

            try
            {
               FileWriter fw = new FileWriter(docFile);
               //TODO: fw.write(po file header)

               //get text flows and/or text flow targets
               for (int k = 0, textFlowCount = docBranch.getChildCount(); k < textFlowCount; k++)
               {
                  DefaultMutableTreeNode textFlowLeaf =
                        (DefaultMutableTreeNode) docBranch.getChildAt(k);

                  if (textFlowLeaf.getUserObject() instanceof TextFlow)
                  {
                     TextFlow textFlow = (TextFlow) textFlowLeaf.getUserObject();
                     TextFlowTarget textFlowTarget = null;
                     //TODO: write #: line if zanata stores this info
                     //TODO: write #, to indicate fuzzy?

                     //get textFlowTarget belonging to textFlow
                     outerloop:
                     for (int l = 0, textFlowTargetCount = docBranch.getChildCount(); l < textFlowTargetCount; l++)
                     {
                        DefaultMutableTreeNode textFlowTargetLeaf =
                              (DefaultMutableTreeNode) docBranch.getChildAt(l);

                        if (textFlowTargetLeaf.getUserObject() instanceof TextFlowTarget)
                        {
                           textFlowTarget = (TextFlowTarget) textFlowTargetLeaf.getUserObject();

                           if (textFlowTarget.getResId() == textFlow.getId()) //found corresponding TFT
                              break outerloop;
                        }
                     }

                     if (textFlowTarget == null)
                     {
                        fw.write("msgid \"" + textFlow.getContent() + "\"\n");
                        fw.write("msgstr \"\"\n\n");
                     }
                     else
                     {
                        if (textFlowTarget.getState() == ContentState.NeedReview)
                           fw.write("#, fuzzy\n");
                        fw.write("msgid \"" + textFlow.getContent() + "\"\n");
                        fw.write("msgstr \"" + textFlowTarget.getContent() + "\"\n\n");
                     }
                  }
                  else if (!(textFlowLeaf.getUserObject() instanceof TextFlowTarget))
                  {
                     System.err.println("Assert failed: Branch contains object that is neither a TextFlow nor a TextFlowTarget");
                     return false;
                  }
               }
               fw.close();
            }
            catch (IOException e)
            {
               System.err.println("ResourceMeta save failed: " + e.getMessage());
               return false;
            }

         }
      }

      //save project file after populating project iterations
      projectfile = new File(project.getName() + "/" + project.getName() + ".xml");

      try
      {
         FileWriter fw = new FileWriter(projectfile);
         fw.write(DTOUtil.toXML(project)); //save Project/Project.xml
         fw.close();
      }
      catch (IOException e)
      {
         System.err.println("Project save failed: " + e.getMessage());
         return false;
      }

      return true;
   }
}
