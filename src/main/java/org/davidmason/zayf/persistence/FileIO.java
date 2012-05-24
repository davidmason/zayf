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
import java.util.List;

//import org.zanata.common.EntityStatus;
import org.zanata.rest.dto.*;
//import org.zanata.rest.dto.resource.ResourceMeta;
//import org.davidmason.zayf.rest.*;
import org.zanata.rest.dto.resource.ResourceMeta;
import org.zanata.rest.dto.resource.TextFlow;

public class FileIO
{

   public static void main(String[] args) throws IOException
   {
      //FileWriter fileWriter = new FileWriter(file);
      //DummyServerProxy sp = new DummyServerProxy();

      //List<Project> projectList = sp.getProjectList();
      Project project = DummyProject.getProject();

      //for (Project project : projectList)
      //{
      //File file = new File(project.getName() + ".txt");

      if (!saveProject(project))
         System.err.println("Project save failed.");
      //}
   }

   /**
    * Save project to disk within default directory structure
    * 
    * @return true if successful
    */
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

            List<TextFlow> flows =
                  DummyProject.getTextFlows(project.getId(), iteration.getId(), doc.getName());
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
   }
}
