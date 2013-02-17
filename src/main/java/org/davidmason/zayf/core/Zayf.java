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

import org.davidmason.zayf.controller.ProjectTreeController;
import org.davidmason.zayf.controller.ServerSelectController;
import org.davidmason.zayf.view.DocumentsView;
import org.davidmason.zayf.view.MainWindow;
import org.davidmason.zayf.view.ProjectTreeView;
import org.davidmason.zayf.view.ServerSelectView;

//import org.davidmason.zayf.ui.ZayfView;

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

            ProjectTreeView projectTreeView = new ProjectTreeView();
            ProjectTreeController projectTreeController =
                  new ProjectTreeController(projectTreeView);

            ServerSelectController serverController =
                  new ServerSelectController(projectTreeController);
            ServerSelectView serverSelectView = new ServerSelectView(serverController);

            DocumentsView documentsView = new DocumentsView();

            new MainWindow(serverSelectView, projectTreeView, documentsView);
         }
      });
   }
}
