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

//standard libs
import java.net.URI;
import java.net.URL;
import java.util.List;

//zanata-rest-client
import org.zanata.rest.client.ZanataProxyFactory;

//zanata-common-api
import org.zanata.rest.dto.VersionInfo;
import org.zanata.rest.dto.Project;
import org.zanata.rest.client.IProjectResource;
import org.zanata.rest.client.ITranslationResources;
import org.zanata.rest.dto.resource.ResourceMeta;

//resteasy-jaxrs
import org.jboss.resteasy.client.ClientResponse;

/**
 * Provides a proxy to a Zanata server
 */
public class ServerProxy
{

   private URI serverURI;
   private String userName;
   private String apiKey;

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
      this.userName = userName;
      this.apiKey = apiKey;

      requestFactory = new ZanataProxyFactory(serverURI, userName, apiKey,
                                              new VersionInfo("1.5.0", "unknown"));
      /*
              new ZanataProxyFactory(new URL("http://localhost:8080/zanata/").toURI(), 
                                     "admin",
                                     "REDACTED", 
                                     new VersionInfo("1.5.0", "unknown"));
      */
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

}
