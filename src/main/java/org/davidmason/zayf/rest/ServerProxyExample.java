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
package org.davidmason.zayf.rest;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.zanata.rest.dto.Project;
import org.zanata.rest.dto.ProjectIteration;
import org.zanata.rest.dto.resource.ResourceMeta;
import org.zanata.rest.dto.resource.TextFlow;

public class ServerProxyExample
{

   /**
    * Invoke using mvn exec:java -Dexec.mainClass=org.davidmason.zayf.rest.ServerProxyExample
    * 
    * @param args
    * @throws MalformedURLException
    * @throws URISyntaxException
    */
   public static void main(String[] args) throws MalformedURLException, URISyntaxException
   {
      //      ServerProxy sp = new DummyServerProxy();
      ServerProxy sp =
            new ServerProxy(new URL("http://localhost:8080/zanata/").toURI(), "admin",
                            "REDACTED");

      System.out.println("\nGetting project list");
      List<Project> projects = sp.getProjectList();
      for (Project project : projects)
      {
         System.out.println("\tid: '" + project.getId() + "' name: '" + project.getName()
                            + "' desc: '" + project.getDescription() + "'");
      }

      System.out.println("\nGetting version list for '" + projects.get(0).getId() + "'");
      List<ProjectIteration> versions = sp.getVersionList(projects.get(0).getId());
      for (ProjectIteration version : versions)
      {
         System.out.println("\tversion: '" + version.getId() + "'");
      }

      System.out.println("\nGetting document list for project '" + projects.get(0).getId()
                         + "' version '" + versions.get(0).getId() + "'");
      List<ResourceMeta> docs = sp.getDocList(projects.get(0).getId(), versions.get(0).getId());
      System.out.println("\tDocuments: " + docs.size());
      for (ResourceMeta doc : docs)
      {
         System.out.println("\t\t" + doc.getName());
      }

      System.out.println("\nGetting text flows for document '" + docs.get(0).getName() + "'");
      List<TextFlow> flows =
            sp.getTextFlows(projects.get(0).getId(), versions.get(0).getId(), docs.get(0).getName());
      for (TextFlow tf : flows)
      {
         System.out.println("\tcontent: '" + tf.getContent() + "'");
      }

      /* temporarily disabled, need to sort out encoding issue
      System.out.println("\ngetting translations for document '" + realDocumentOnServer + "'");
      List<TextFlowTarget> targets = sp.getTargets(realProjectOnServer, realVersionOnServer, new LocaleId(realLocaleIdOnServer) , realDocumentOnServer);
      for (TextFlowTarget tft : targets)
      {
         System.out.println("content: " + tft.getContent());
      }
      */

      System.out.println("\nfinished");
   }

}
