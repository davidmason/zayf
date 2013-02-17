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
package org.davidmason.zayf.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.davidmason.zayf.config.ConfigLoader;
import org.davidmason.zayf.model.ServerInfo;
import org.davidmason.zayf.rest.ServerProxy;

public class ServerSelectController
{

   private ProjectTreeController projectTreeController;

   public ServerSelectController(ProjectTreeController projectTreeController)
   {
      this.projectTreeController = projectTreeController;
   }

   public List<ServerInfo> getServerInfo()
   {
      ConfigLoader loader = new ConfigLoader();
      return loader.getServerInfo();
   }

   public void buttonPressViewProjects(ServerInfo info)
   {
      URI uri;
      try
      {
         uri = info.getServerUrl().toURI();
      }
      catch (URISyntaxException e)
      {
         // TODO show failure message
         // TODO clear projects tree, or add a heading so it shows which server is being displayed.
         System.out.println("invalid URL for selected server");
         return;
      }
      ServerProxy proxy = new ServerProxy(uri, info.getUserName(), info.getApiKey());
      // TODO hand this to project display controller
      System.out.println("Show projects for " + info.getServerUrl());
      projectTreeController.setServer(proxy);
      projectTreeController.fetchProjectList();
   }

}
