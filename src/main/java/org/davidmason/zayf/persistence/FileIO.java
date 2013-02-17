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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.zanata.common.ContentState;
import org.zanata.rest.dto.DTOUtil;
import org.zanata.rest.dto.Project;
import org.zanata.rest.dto.ProjectIteration;
import org.zanata.rest.dto.resource.ResourceMeta;
import org.zanata.rest.dto.resource.TextFlow;
import org.zanata.rest.dto.resource.TextFlowTarget;

public class FileIO
{

   private static List<File> getIterationFiles(File iterationDirectory)
   {
      List<File> resultList = new ArrayList<File>();

      FileFilter poFilter = new FileFilter()
      {

         @Override
         public boolean accept(File pathname)
         {
            if (pathname.isDirectory())
               return true;
            if (pathname.getName().endsWith(".po"))
               return true;

            return false;
         }
      };

      File[] list = iterationDirectory.listFiles(poFilter);

      for (int i = 0; i < list.length; i++)
      {
         if (list[i].isFile())
            resultList.add(list[i]);
         else
            resultList.addAll(getIterationFiles(list[i]));
      }

      return resultList;
   }

   /**
    * load project from hard disk
    * 
    * @param projectFile
    * @return true if successful
    */
   public static boolean loadProject(File projectFile)
   {
      try
      {
         JAXBContext context = JAXBContext.newInstance(Project.class);
         Unmarshaller unmarshaller = context.createUnmarshaller();

         Project project = (Project) unmarshaller.unmarshal(projectFile);

         DefaultMutableTreeNode projectBranch = new DefaultMutableTreeNode(project);

         //System.out.println(project.toString());
         for (ProjectIteration iteration : project.getIterations())
         {
            DefaultMutableTreeNode iterationBranch = new DefaultMutableTreeNode(iteration);
            projectBranch.add(iterationBranch);

            //System.out.println(iteration.toString());
            File iterationDirectory = new File(project.getName() + "/" + iteration.getId());
            List<File> iterationFiles = getIterationFiles(iterationDirectory);

            for (File file : iterationFiles)
            {
               ResourceMeta resourceMeta = new ResourceMeta(file.getName()); //TODO:
               DefaultMutableTreeNode docBranch = new DefaultMutableTreeNode(resourceMeta);
               iterationBranch.add(docBranch);

               //resourceMeta.set
               //TODO: get header info

               //TODO: convert files to resourceMetas containing textFlows and textFlowTargets

               //System.out.println("File: " + file.getName());

               FileReader fr = new FileReader(file);
               BufferedReader reader = new BufferedReader(fr);

               String line, msgid = null, msgstr = null;

               while (true)
               {
                  line = reader.readLine();

                  if (line == null)
                     break;

                  line = line.trim();

                  if (line.startsWith("msgid"))
                     msgid = line.substring(line.indexOf('"') + 1, line.length() - 1);

                  if (line.startsWith("msgstr"))
                     msgstr = line.substring(line.indexOf('"') + 1, line.length() - 1);

                  if ((msgid != null) && (msgstr != null))
                  {
                     //TextFlow textFlow = new TextFlow(id, lang, content) //TODO: populate tf/tft and add
                     //TextFlowTarget textFlowTarget = new TextFlowTarget(resId)

                     //System.out.println(" msgid: '" + msgid + "'");
                     //System.out.println(" msgstr: '" + msgstr + "'");

                     msgid = null;
                     msgstr = null;
                  }
               }

               reader.close();
               fr.close();

            }
         }
      }
      catch (Exception e)
      {
         System.err.println("Project load failed: " + e.getMessage());
         return false;
      }

      return true;
   }

   // FIXME libraries from Zanata should be used to write po files
   /**
    * Saves project as xml meta files and po text flows
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
