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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jboss.resteasy.client.ClientResponse;
import org.zanata.common.EntityStatus;
import org.zanata.common.LocaleId;
import org.zanata.rest.client.ITranslationResources;
import org.zanata.rest.client.ZanataProxyFactory;
import org.zanata.rest.dto.Project;
import org.zanata.rest.dto.VersionInfo;
import org.zanata.rest.dto.resource.Resource;
import org.zanata.rest.dto.resource.ResourceMeta;
import org.zanata.rest.dto.resource.TextFlow;
import org.zanata.rest.dto.resource.TextFlowTarget;
import org.zanata.rest.dto.resource.TranslationsResource;

/**
 * Provides a proxy to a Zanata server
 */
public class ServerProxy
{

   private static final String SERVER_VERSION = "1.5.0";
   private static final String SERVER_BUILD_TIMESTAMP = "unknown";

   private URI serverURI;

   private ZanataProxyFactory requestFactory;

   /**
    * Create a new server proxy for a given server
    * 
    * @param serverURI
    *           the base URI for the server
    * @param userName
    *           valid username for the given server
    * @param apiKey
    *           api key for the given username on the given server
    */
   public ServerProxy(URI serverURI, String userName, String apiKey)
   {
      this.serverURI = serverURI;

      requestFactory =
            new ZanataProxyFactory(serverURI, userName, apiKey,
                                   new VersionInfo(SERVER_VERSION, SERVER_BUILD_TIMESTAMP));
      /*
              new ZanataProxyFactory(new URL("http://localhost:8080/zanata/").toURI(), 
                                     "admin",
                                     "REDACTED", 
                                     new VersionInfo("1.5.0", "unknown"));
      */
   }

   // TODO add constructor with flag to make a pure dummy version, or make a
   // subclass with dummy implementation

   /**
    * Get a list of projects on the server.
    * 
    * FIXME Currently returns dummy data
    * 
    * @return a list of all active and read-only projects on the server
    */
   public List<Project> getProjectList()
   {
      /* FIXME this should work, but gives 404, may need to update library code
      URI projectsUri = getProjectsURI(serverURI);
      System.out.println("Projects URI: " + projectsUri);
      IProjectsResource projectListResource = requestFactory.getProjects(serverURI);

      ClientResponse<Project[]> projectListResponse = projectListResource.get();
      if (projectListResponse.getStatus() >= 399)
      {
         //TODO throw specific useful exception
          System.out.println("Got error response code: " + projectListResponse.getStatus());
          throw new RuntimeException("Got error response code retrieving project list: " + projectListResponse.getStatus());
      }
      else
      {
          return Arrays.asList(projectListResponse.getEntity()); //new ArrayList<Project>
      }
      */

      ArrayList<Project> projects = new ArrayList<Project>();

      Project proj1 = new Project();
      proj1.setId("dummy1");
      proj1.setName("Dummy Project 1");
      proj1.setDescription("A dummy project created for testing");
      proj1.setStatus(EntityStatus.ACTIVE);
      projects.add(proj1);

      Project proj2 = new Project();
      proj2.setId("dummy2");
      proj2.setName("Dummy Project 2");
      proj2.setDescription("Another dummy project created for testing");
      proj2.setStatus(EntityStatus.ACTIVE);
      projects.add(proj2);

      Project proj3 = new Project();
      proj3.setId("dummy3");
      proj3.setName("Dummy Project 3");
      proj3.setDescription("A read-only dummy project created for testing");
      proj3.setStatus(EntityStatus.READONLY);
      projects.add(proj3);
      return projects;
   }

   /**
    * Get a list of versions for a project
    * 
    * @param projectSlug
    *           identifying slug for the project
    * @return list of version slugs
    */
   public List<String> getVersionList(String projectSlug)
   {
      // FIXME looks like our rest interface doesn't have any way to discover this
      // information. Will need to update the interface to provide it
      List<String> versions = new ArrayList<String>();
      versions.add(projectSlug + "-v1");
      versions.add(projectSlug + "-v2");
      versions.add(projectSlug + "-v3");
      return versions;
   }

   /**
    * Get a list of documents in a particular project-version
    * 
    * @param projectSlug
    *           identifying slug for project
    * @param versionSlug
    *           identifying slug for version within project
    * @return a list of all documents in the project-version, or an empty list
    *         if no documents are in the project-version
    */
   public List<ResourceMeta> getDocList(String projectSlug, String versionSlug)
   {
      ITranslationResources translationResources =
            requestFactory.getTranslationResources(projectSlug, versionSlug);
      ClientResponse<List<ResourceMeta>> getResponse = translationResources.get(null);
      if (getResponse.getStatus() >= 399)
      {
         //FIXME use more specific exception class
         throw new RuntimeException("Error response code for document list request: "
                                    + getResponse.getStatus());
      }
      return getResponse.getEntity();
   }

   public List<TextFlow> getTextFlows(String projectSlug, String versionSlug, String docId)
   {
      ITranslationResources translationResources =
            requestFactory.getTranslationResources(projectSlug, versionSlug);
      ClientResponse<Resource> response =
            translationResources.getResource(prepareDocId(docId), null);
      Resource res = response.getEntity();
      return res.getTextFlows();
   }

   public List<TextFlowTarget> getTargets(String projectSlug, String versionSlug, LocaleId locale,
                                          String docId)
   {
      ITranslationResources translationResources =
            requestFactory.getTranslationResources(projectSlug, versionSlug);
      ClientResponse<TranslationsResource> response =
            translationResources.getTranslations(prepareDocId(docId), locale, null);
      TranslationsResource res = response.getEntity();
      return res.getTextFlowTargets();
   }

   //TODO get list of supported locales for server, project, version

   private static String RESOURCE_PREFIX = "rest";

   //TODO move this method into ZanataProxyFactory
   public static URI getProjectsURI(URI baseURI)
   {
      String spec = RESOURCE_PREFIX + "/projects";
      try
      {
         return new URL(baseURI.toURL(), spec).toURI();
      }
      catch (MalformedURLException e)
      {
         throw new RuntimeException(e);
      }
      catch (URISyntaxException e)
      {
         throw new RuntimeException(e);
      }
   }

   private String prepareDocId(String docId)
   {
      return docId.replace('/', ',');
   }
}
