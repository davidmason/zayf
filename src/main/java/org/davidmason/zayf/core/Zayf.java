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
import java.awt.SystemTray;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.davidmason.zayf.cache.neo.NeoMirrorModule;
import org.davidmason.zayf.controller.ServerConfigLoader;
import org.davidmason.zayf.controller.impl.ControllerModule;
import org.davidmason.zayf.rest.impl.ServerProxyModule;
import org.davidmason.zayf.view.MainWindow;
import org.davidmason.zayf.view.ZayfTrayIcon;
import org.davidmason.zayf.view.swing.SwingViewModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Zayf entry point.
 */
public class Zayf
{

   private static Logger log = Logger.getLogger(Zayf.class);

   public static void main(String[] args)
   {
      SwingUtilities.invokeLater(new Runnable()
      {

         public void run()
         {
            log.info("Loading application.");
            runApplication();
         }
      });
   }

   // TODO move this to its own class
   private static void runApplication()
   {
      final ZayfTrayIcon icon = makeTrayIcon();

      Injector injector =
            Guice.createInjector(new SwingViewModule(), new ControllerModule(),
                                 new NeoMirrorModule(), new ServerProxyModule());
      final MainWindow mainWindow2 = injector.getInstance(MainWindow.class);

      addIconActionListener(mainWindow2, icon);

      final ServerConfigLoader configLoader = injector.getInstance(ServerConfigLoader.class);

      // TODO may want to make this step manual, or sometimes manual
      configLoader.loadServersFromConfig();
   }

   private static ZayfTrayIcon makeTrayIcon()
   {
      if (SystemTray.isSupported())
      {
         final ZayfTrayIcon icon = new ZayfTrayIcon();
         try
         {
            SystemTray.getSystemTray().add(icon);
         }
         catch (AWTException e)
         {
            log.error("Failed to add tray icon", e);
         }
         icon.addExitListener(new ActionListener()
         {

            @Override
            public void actionPerformed(ActionEvent e)
            {
               log.info("Shutting down application.");
               // FIXME finish file writes and server communication before exit
               System.exit(0);
            }
         });
         return icon;
      }
      else
      {
         // FIXME application cannot be closed without the above in its current form
         // make default behaviour close on [X], allow config to change this, and add an application
         // menu with exit option.
         log.warn("System tray not supported. There is currently no alternative implementation.");
         return null;
      }
   }

   private static void addIconActionListener(final MainWindow mainWindow, final ZayfTrayIcon icon)
   {
      icon.addIconActionListener(new ActionListener()
      {

         @Override
         public void actionPerformed(ActionEvent e)
         {
            boolean show = !mainWindow.isVisible();
            if (!show)
            {
               // FIXME this will not display when the [X] is clicked. Change so that it is.
               icon.displayMessage("Zayf is minimized",
                                   "Double-click to restore. Right-click and select 'exit' to close",
                                   MessageType.INFO);
            }
            mainWindow.setVisible(show);
         }
      });
   }
}
