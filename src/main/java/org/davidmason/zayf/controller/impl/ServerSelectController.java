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
package org.davidmason.zayf.controller.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import org.davidmason.zayf.cache.Mirror;
import org.davidmason.zayf.config.ConfigLoader;
import org.davidmason.zayf.controller.ServerConfigLoader;
import org.davidmason.zayf.model.ServerInfo;
import org.davidmason.zayf.view.ServerSelectView;

import com.google.inject.Inject;

/**
 * Responsible for loading server info from configuration and responding to user selection of a
 * server.
 * 
 * @author David Mason, dr.d.mason@gmail.com
 * 
 */
class ServerSelectController implements ServerConfigLoader
{

   private ServerSelectView<?> view;
   private ProjectTreeController projectTreeController;
   private final Mirror mirror;

   @Inject
   ServerSelectController(ServerSelectView<?> sSView,
                          ProjectTreeController projectTreeController,
                          Mirror mirror)
   {
      this.view = sSView;
      this.projectTreeController = projectTreeController;
      this.mirror = mirror;

      this.view.addLoadProjectListener(new ActionListener()
      {

         @Override
         public void actionPerformed(ActionEvent e)
         {
            ServerInfo info = view.getSelectedServerInfo();
            updateServerAndLoadProjects(info);
         }
      });
   }

   public void loadServersFromConfig()
   {
      SwingWorker<List<ServerInfo>, Void> loadServerWorker =
            new SwingWorker<List<ServerInfo>, Void>()
            {

               @Override
               protected List<ServerInfo> doInBackground() throws Exception
               {
                  return getServerInfo();
               }

               @Override
               protected void done()
               {
                  if (!isCancelled())
                  {
                     try
                     {
                        view.showServers(get());
                     }
                     catch (InterruptedException e)
                     {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                     }
                     catch (ExecutionException e)
                     {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                     }
                  }
               }
            };

      loadServerWorker.execute();
   }

   private List<ServerInfo> getServerInfo()
   {
      ConfigLoader loader = new ConfigLoader();
      return loader.getServerInfo();
   }

   private void updateServerAndLoadProjects(ServerInfo info)
   {
      if (info == null)
      {
         // TODO show error dialog (no server selected)
         return;
      }

      // add server to database as a parent for any downloaded or tracked projects
      mirror.addServer(info);

      projectTreeController.fetchProjectList(info);
   }

}
