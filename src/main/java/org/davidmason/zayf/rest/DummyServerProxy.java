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

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.zanata.common.ContentState;
import org.zanata.common.EntityStatus;
import org.zanata.common.LocaleId;
import org.zanata.rest.dto.Project;
import org.zanata.rest.dto.ProjectIteration;
import org.zanata.rest.dto.resource.ResourceMeta;
import org.zanata.rest.dto.resource.TextFlow;
import org.zanata.rest.dto.resource.TextFlowTarget;

public class DummyServerProxy implements ServerProxy
{

   public DummyServerProxy() throws URISyntaxException
   {
      super();
      // TODO Auto-generated constructor stub
   }

   @Override
   public List<Project> getProjectList()
   {
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

   @Override
   public List<ProjectIteration> getVersionList(String projectSlug)
   {
      List<ProjectIteration> versions = new ArrayList<ProjectIteration>();
      ProjectIteration pi1 = new ProjectIteration(projectSlug + "-v1");
      pi1.setStatus(EntityStatus.ACTIVE);
      versions.add(pi1);

      ProjectIteration pi2 = new ProjectIteration(projectSlug + "-v2");
      pi1.setStatus(EntityStatus.ACTIVE);
      versions.add(pi2);

      ProjectIteration pi3 = new ProjectIteration(projectSlug + "-v3");
      pi1.setStatus(EntityStatus.READONLY);
      versions.add(pi3);

      return versions;
   }

   @Override
   public List<ResourceMeta> getDocList(String projectSlug, String versionSlug)
   {
      List<ResourceMeta> docs = new ArrayList<ResourceMeta>();
      docs.add(new ResourceMeta(projectSlug));
      docs.add(new ResourceMeta(versionSlug));
      docs.add(new ResourceMeta("doc1"));
      docs.add(new ResourceMeta("doc2"));
      docs.add(new ResourceMeta("path/doc3"));
      docs.add(new ResourceMeta("path/doc4"));
      return docs;
   }

   @Override
   public List<TextFlow> getTextFlows(String projectSlug, String versionSlug, String docId)
   {
      LocaleId loc = new LocaleId("en-US");
      List<TextFlow> flows = new ArrayList<TextFlow>();
      flows.add(new TextFlow("id1", loc, "Dummy text flow 1 dummy text flow 1 dummy text flow 1"));
      flows.add(new TextFlow("id2", loc, "Dummy text flow 2 dummy text flow 2 dummy text flow 2"));
      flows.add(new TextFlow("id3", loc, "Dummy text flow 3 dummy text flow 3 dummy text flow 3"));
      return flows;
   }

   @Override
   public List<TextFlowTarget> getTargets(String projectSlug, String versionSlug, LocaleId locale,
                                          String docId)
   {
      List<TextFlowTarget> translations = new ArrayList<TextFlowTarget>();

      TextFlowTarget tft1 = new TextFlowTarget("id1");

      tft1.setContent("Dummy german translation 1");
      tft1.setState(ContentState.Approved);
      translations.add(tft1);

      TextFlowTarget tft2 = new TextFlowTarget("id2");
      tft2.setContent("Dummy german translation 2");
      tft2.setState(ContentState.NeedReview);
      translations.add(tft2);

      TextFlowTarget tft3 = new TextFlowTarget("id3");
      tft3.setContent("");
      tft3.setState(ContentState.New);
      translations.add(tft3);

      return translations;
   }

}
