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
package org.davidmason.zayf.core;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.SystemTray;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.davidmason.zayf.controller.DocumentsController;
import org.davidmason.zayf.controller.ProjectDetailsController;
import org.davidmason.zayf.controller.ProjectTreeController;
import org.davidmason.zayf.controller.ServerSelectController;
import org.davidmason.zayf.controller.VersionDetailsController;
import org.davidmason.zayf.view.DocumentsView;
import org.davidmason.zayf.view.MainWindow;
import org.davidmason.zayf.view.ProjectDetailsView;
import org.davidmason.zayf.view.ProjectTreeView;
import org.davidmason.zayf.view.ServerSelectView;
import org.davidmason.zayf.view.VersionDetailsView;
import org.davidmason.zayf.view.ZayfTrayIcon;
import org.davidmason.zayf.view.impl.DocumentsViewImpl;
import org.davidmason.zayf.view.impl.ProjectDetailsViewImpl;
import org.davidmason.zayf.view.impl.ProjectTreeViewImpl;
import org.davidmason.zayf.view.impl.ServerSelectViewImpl;
import org.davidmason.zayf.view.impl.VersionDetailsViewImpl;

/**
 * Zayf entry point, currently responsible for wiring the application
 * (until an appropriate framework is employed)
 */
public class Zayf
{

   public static void main(String[] args)
   {
      javax.swing.SwingUtilities.invokeLater(new Runnable()
      {

         public void run()
         {
            System.out.println("Loading application.");
            runApplication();
         }
      });
   }

   // TODO move this to its own class
   private static void runApplication()
   {

      // documents window
      DocumentsView<Component> docsView = new DocumentsViewImpl();
      DocumentsController docsControl = new DocumentsController(docsView);

      // main window
      VersionDetailsView<Component> verDetailsView = new VersionDetailsViewImpl();
      VersionDetailsController verDetailsControl =
            new VersionDetailsController(verDetailsView, docsControl);

      ProjectDetailsView<Component> projDetailsView = new ProjectDetailsViewImpl();
      ProjectDetailsController projDetailsControl =
            new ProjectDetailsController(projDetailsView, verDetailsControl);

      ProjectTreeView<Component> projTreeView = new ProjectTreeViewImpl();
      ProjectTreeController projTreeControl =
            new ProjectTreeController(projTreeView, projDetailsControl);

      ServerSelectView<Component> serverSelectView = new ServerSelectViewImpl();
      ServerSelectController serverControl =
            new ServerSelectController(serverSelectView, projTreeControl, docsControl);

      final MainWindow mainWindow =
            new MainWindow(serverSelectView.asWidget(), projTreeView.asWidget(),
                           projDetailsView.asWidget(), verDetailsView.asWidget());

      if (SystemTray.isSupported())
      {
         final ZayfTrayIcon icon = new ZayfTrayIcon();
         try
         {
            SystemTray.getSystemTray().add(icon);
         }
         catch (AWTException e)
         {
            System.out.println("Failed to add tray icon");
            e.printStackTrace();
         }
         icon.addExitListener(new ActionListener()
         {

            @Override
            public void actionPerformed(ActionEvent e)
            {
               // FIXME finish file writes and server communication before exit
               System.exit(0);
            }
         });
         icon.addIconActionListener(new ActionListener()
         {

            @Override
            public void actionPerformed(ActionEvent e)
            {
               boolean show = !mainWindow.isVisible();
               if (!show)
               {
                  icon.displayMessage("Zayf is minimized",
                                      "Double-click to restore. Right-click and select 'exit' to close",
                                      MessageType.INFO);
               }
               mainWindow.setVisible(show);
            }
         });
      }
      else
      {
         // FIXME application cannot be closed without the above in its current form
         // make default behaviour close on [X], allow config to change this, and add an application
         // menu with exit option.
         System.out.println("system tray not supported");
      }

      // TODO may want to make this step manual, or sometimes manual.
      serverControl.loadServersFromConfig();
   }
}
