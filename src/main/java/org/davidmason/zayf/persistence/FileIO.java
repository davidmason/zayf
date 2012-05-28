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
import org.zanata.rest.dto.*;
//import org.zanata.rest.dto.resource.ResourceMeta;
//import org.davidmason.zayf.rest.*;
import org.zanata.rest.dto.resource.ResourceMeta;
import org.zanata.rest.dto.resource.TextFlow;
import org.zanata.rest.dto.resource.TextFlowTarget;

public class FileIO
{

   //public static void main(String[] args) throws IOException
   //{
   //FileWriter fileWriter = new FileWriter(file);
   //DummyServerProxy sp = new DummyServerProxy();

   //List<Project> projectList = sp.getProjectList();
   //Project project = DummyProject.getProject();

   //for (Project project : projectList)
   //{
   //File file = new File(project.getName() + ".txt");

   //   if (!saveProject(project))
   //      System.err.println("Project save failed.");
   //}
   //}

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
                                    + doc.getName() + ".xml");
            docFile.getParentFile().mkdirs(); //make Project/ProjectIteration/docpath/doc.xml directory

            try
            {
               FileWriter fw = new FileWriter(docFile);
               fw.write(DTOUtil.toXML(doc)); //save Project/ProjectIteration/docpath/doc.xml
               fw.close();
            }
            catch (Exception e)
            {
               System.err.println("ResourceMeta save failed: " + e.getMessage());
               return false;
            }

            //get text flows and/or text flow targets
            for (int k = 0, textFlowCount = docBranch.getChildCount(); k < textFlowCount; k++)
            {
               DefaultMutableTreeNode textFlowLeaf =
                     (DefaultMutableTreeNode) docBranch.getChildAt(k);

               if (textFlowLeaf.getUserObject() instanceof TextFlow)
               {
                  TextFlow textFlow = (TextFlow) textFlowLeaf.getUserObject();

                  File tfFile =
                        new File(docFile.getParentFile() + "/" + "TF_" + textFlow.getId() + ".xml");

                  try
                  {
                     FileWriter fw = new FileWriter(tfFile);
                     fw.write(DTOUtil.toXML(textFlow)); //save Project/ProjectIteration/docpath/tf.xml
                     fw.close();
                  }
                  catch (Exception e)
                  {
                     System.err.println("TextFlow save failed: " + e.getMessage());
                     return false;
                  }
               }
               else if (textFlowLeaf.getUserObject() instanceof TextFlowTarget)
               {
                  TextFlowTarget textFlowTarget = (TextFlowTarget) textFlowLeaf.getUserObject();

                  File tftFile =
                        new File(docFile.getParentFile() + "/" + "TFT_" + textFlowTarget.getResId()
                                 + ".xml");

                  try
                  {
                     FileWriter fw = new FileWriter(tftFile);
                     fw.write(DTOUtil.toXML(textFlowTarget)); //save Project/ProjectIteration/docpath/tft.xml
                     fw.close();
                  }
                  catch (Exception e)
                  {
                     System.err.println("TextFlowTarget save failed: " + e.getMessage());
                     return false;
                  }
               }
               else
               {
                  System.err.println("Assert failed: Branch contains neither a TextFlow nor a TextFlowTarget");
                  return false;
               }
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

   ///**
   // * Save project to disk within default directory structure
   // * @return true if successful
   // */
   /*
   public static boolean saveProject(Project project)
   {
      File file = new File(project.getName());
      file.mkdir(); //make project directory
      file = new File(project.getName() + "/" + project.getName() + ".xml");
      
      try
      {
         FileWriter fw = new FileWriter(file);
         fw.write(DTOUtil.toXML(project)); //save Project/Project.xml
         fw.close();
      }
      catch (IOException e)
      {
         System.err.println("Project save failed: " + e.getMessage());
         return false;
      }
      
         
      List<ProjectIteration> iterations = project.getIterations(true);
      for (ProjectIteration iteration : iterations)
      {
         file = new File(project.getName() + "/" + iteration.getId());
         file.mkdir(); //make Project/ProjectIteration directory
         
         List<ResourceMeta> docList = DummyProject.getDocList(project.getId(), iteration.getId());
         for (ResourceMeta doc : docList)
         {
            file = new File(project.getName() + "/" + iteration.getId() + "/"
                    + doc.getName() + ".xml"); 
            file.getParentFile().mkdirs(); //make Project/ProjectIteration/docpath/doc.xml directory
            
            try
            {
               FileWriter fw = new FileWriter(file);
               fw.write(DTOUtil.toXML(doc)); //save Project/ProjectIteration/docpath/doc.xml
               fw.close();
            }
            catch (Exception e)
            {
               System.err.println("ResourceMeta save failed: " + e.getMessage());
               return false;
            }

            List<TextFlow> flows = DummyProject.getTextFlows(project.getId(), iteration.getId(), doc.getName());
            for (TextFlow tf : flows)
            {
               File docFile = new File(file.getParentFile() + "/" + tf.getId() + ".xml");
               
               try
               {
                  FileWriter fw = new FileWriter(docFile);
                  //TODO: save as .po file instead of xml placeholder
                  fw.write(DTOUtil.toXML(tf)); //save Project/ProjectIteration/docpath/tf.xml
                  fw.close();
               }
               catch (Exception e)
               {
                  System.err.println("TextFlow save failed: " + e.getMessage());
                  return false;
               }
            }

         }
         
         
      }

      return true;
   }*/
}
