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
/**
 * 
 */
package org.davidmason.zayf.cache;

import org.davidmason.zayf.model.ServerInfo;

/**
 * <p>
 * Primary interface to classes that store an on-disk copy of tracked projects.
 * </p>
 * 
 * <p>
 * This interface only describes storage and retrieval to and from disk, it does not describe any
 * server communications.
 * </p>
 * 
 * @author David Mason, dr.d.mason@gmail.com
 * 
 */
public interface Mirror
{
   // Might want to store:

   // project list               List<Project>
   // This would only be useful for caching a list of projects available on the server to
   // reduce number of server requests and allow them to be viewed offline.
   // May be nice if users can keep browsing around any data they've looked at without
   // generating extra server traffic, even if a connection is not available. It would suck
   // to lose a connection while looking at projects and have the stuff you were browsing
   // pulled out from under you.

   // version list for project   List<ProjectIteration>

   // doc list for version       List<ResourceMeta>

   // text flows (document)      List<TextFlow>

   // targets (document)         List<TextFlowTarget>

   /**
    * Add a server to the mirror.
    * 
    * @param serverInfo
    */
   void addServer(ServerInfo serverInfo);
}
