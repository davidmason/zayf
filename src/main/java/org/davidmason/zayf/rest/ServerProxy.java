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
import java.util.Arrays;
import java.util.List;

import org.jboss.resteasy.client.ClientResponse;
import org.zanata.common.LocaleId;
import org.zanata.rest.client.IProjectResource;
import org.zanata.rest.client.IProjectsResource;
import org.zanata.rest.client.ISourceDocResource;
import org.zanata.rest.client.ITranslatedDocResource;
import org.zanata.rest.client.ZanataProxyFactory;
import org.zanata.rest.dto.Project;
import org.zanata.rest.dto.ProjectIteration;
import org.zanata.rest.dto.VersionInfo;
import org.zanata.rest.dto.resource.Resource;
import org.zanata.rest.dto.resource.ResourceMeta;
import org.zanata.rest.dto.resource.TextFlow;
import org.zanata.rest.dto.resource.TextFlowTarget;
import org.zanata.rest.dto.resource.TranslationsResource;

/**
 * Provides a proxy to a Zanata server
 * 
 * @see {@link ServerProxyExample}
 */
public class ServerProxy
{

   private static final String SERVER_VERSION = "1.5.0";
   private static final String SERVER_BUILD_TIMESTAMP = "unknown";

   private URI serverURI;

   private ZanataProxyFactory requestFactory;

   /**
    * Included to allow dummy proxy instantiation without server request
    */
   ServerProxy()
   {}

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
      this(serverURI, userName, apiKey, false);
   }

   public ServerProxy(URI serverURI, String userName, String apiKey, boolean debugLogging)
   {
      this.serverURI = serverURI;

      requestFactory =
            new ZanataProxyFactory(serverURI, userName, apiKey,
                                   new VersionInfo(SERVER_VERSION, SERVER_BUILD_TIMESTAMP),
                                   debugLogging);
   }

   /**
    * Get a list of projects on the server.
    * 
    * @return a list of all active and read-only projects on the server
    */
   public List<Project> getProjectList()
   {
      IProjectsResource projectListResource = requestFactory.getProjects(getRestURI(serverURI));

      ClientResponse<Project[]> projectListResponse = projectListResource.get();
      if (projectListResponse.getStatus() >= 399)
      {
         //FIXME throw specific useful exception
         System.out.println("Got error response code: " + projectListResponse.getStatus());
         throw new RuntimeException("Got error response code retrieving project list: "
                                    + projectListResponse.getStatus());
      }
      else
      {
         return Arrays.asList(projectListResponse.getEntity());
      }

   }

   /**
    * Get a list of versions for a project
    * 
    * @param projectSlug
    *           identifying slug for the project
    * @return list of version slugs
    */
   public List<ProjectIteration> getVersionList(String projectSlug)
   {
      IProjectResource projectResource = requestFactory.getProject(projectSlug);
      ClientResponse<Project> response = projectResource.get();
      if (response.getStatus() >= 399)
      {
         //FIXME throw specific useful exception
         System.out.println("Got error response code: " + response.getStatus());
         throw new RuntimeException("Got error response code retrieving projec: "
                                    + response.getStatus());
      }
      else
      {
         return response.getEntity().getIterations();
      }
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
      ISourceDocResource resource = requestFactory.getSourceDocResource(projectSlug, versionSlug);
      ClientResponse<List<ResourceMeta>> getResponse = resource.get(null);
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
      ISourceDocResource resource = requestFactory.getSourceDocResource(projectSlug, versionSlug);
      ClientResponse<Resource> response = resource.getResource(prepareDocId(docId), null);
      Resource res = response.getEntity();
      return res.getTextFlows();
   }

   public List<TextFlowTarget> getTargets(String projectSlug, String versionSlug, LocaleId locale,
                                          String docId)
   {
      ITranslatedDocResource resource =
            requestFactory.getTranslatedDocResource(projectSlug, versionSlug);
      ClientResponse<TranslationsResource> response =
            resource.getTranslations(prepareDocId(docId), locale, null);
      TranslationsResource res = response.getEntity();
      return res.getTextFlowTargets();
   }

   //TODO get list of supported locales for server, project, version

   private static String RESOURCE_PREFIX = "rest";

   //TODO move this method into ZanataProxyFactory
   public static URI getRestURI(URI baseURI)
   {
      try
      {
         return new URL(baseURI.toURL(), RESOURCE_PREFIX).toURI();
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
