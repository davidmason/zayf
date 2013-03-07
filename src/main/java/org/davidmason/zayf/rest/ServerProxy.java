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

import java.util.List;

import org.zanata.common.LocaleId;
import org.zanata.rest.dto.Project;
import org.zanata.rest.dto.ProjectIteration;
import org.zanata.rest.dto.resource.ResourceMeta;
import org.zanata.rest.dto.resource.TextFlow;
import org.zanata.rest.dto.resource.TextFlowTarget;

/**
 * Provides a proxy to a Zanata server.
 * 
 * @author David Mason, dr.d.mason@gmail.com
 * 
 */
public interface ServerProxy
{

   /**
    * Get a list of projects on the server.
    * 
    * @return a list of all active and read-only projects on the server
    */
   public abstract List<Project> getProjectList();

   /**
    * Get a list of versions for a project
    * 
    * @param projectSlug
    *           identifying slug for the project
    * @return list of version slugs
    */
   public abstract List<ProjectIteration> getVersionList(String projectSlug);

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
   public abstract List<ResourceMeta> getDocList(String projectSlug, String versionSlug);

   /**
    * Get a list of all source strings for a document.
    * 
    * @param projectSlug
    *           identifying slug for project
    * @param versionSlug
    *           identifying slug for version within project
    * @param docId
    *           id of document within project
    * @return a list of all source strings
    */
   public abstract List<TextFlow>
         getTextFlows(String projectSlug, String versionSlug, String docId);

   public abstract List<TextFlowTarget> getTargets(String projectSlug, String versionSlug,
                                                   LocaleId locale,
                                                   String docId);

}
