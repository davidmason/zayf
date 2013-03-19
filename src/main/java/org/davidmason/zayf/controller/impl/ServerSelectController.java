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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import org.davidmason.zayf.config.ConfigLoader;
import org.davidmason.zayf.controller.ServerConfigLoader;
import org.davidmason.zayf.model.ServerInfo;
import org.davidmason.zayf.rest.ServerProxy;
import org.davidmason.zayf.rest.ServerProxyImpl;
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
   private DocumentsController docsController;

   @Inject
   ServerSelectController(ServerSelectView<?> sSView,
                          ProjectTreeController projectTreeController,
                          DocumentsController docsController)
   {
      this.view = sSView;
      this.projectTreeController = projectTreeController;
      this.docsController = docsController;

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

   // FIXME should have a single server proxy reference for all components
   private void updateServerAndLoadProjects(ServerInfo info)
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
      ServerProxy proxy = new ServerProxyImpl(uri, info.getUserName(), info.getApiKey());
      // TODO hand this to project display controller
      System.out.println("Show projects for " + info.getServerUrl());
      projectTreeController.setServer(proxy);
      projectTreeController.fetchProjectList();
      // FIXME normalize how server proxy is accessed
      docsController.setServer(proxy);
   }

}
