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

import java.awt.BorderLayout;

import javax.swing.JFrame;

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

   /**
    * 
    */
   private static void runApplication()
   {
      DocumentsView docsView = new DocumentsView();
      DocumentsController docsControl = new DocumentsController(docsView);

      // documents window
      // TODO extract to class
      JFrame docsFrame = new JFrame();
      docsFrame.setTitle("Zayf | documents | PROJECT : VERSION");
      docsFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
      docsFrame.setLayout(new BorderLayout());
      docsFrame.setBounds(0, 0, 480, 640);
      docsFrame.setLocationRelativeTo(null);
      docsFrame.add(docsView, BorderLayout.CENTER);
      docsFrame.setVisible(true);
      // FIXME structure so that controller can show and hide docs window

      // main window
      VersionDetailsView verDetailsView = new VersionDetailsView();
      VersionDetailsController verDetailsControl =
            new VersionDetailsController(verDetailsView, docsControl);

      ProjectDetailsView projDetailsView = new ProjectDetailsView();
      ProjectDetailsController projDetailsControl =
            new ProjectDetailsController(projDetailsView, verDetailsControl);

      ProjectTreeView projTreeView = new ProjectTreeView();
      ProjectTreeController projTreeControl =
            new ProjectTreeController(projTreeView, projDetailsControl);

      ServerSelectController serverControl =
            new ServerSelectController(projTreeControl, docsControl);
      ServerSelectView serverSelectView = new ServerSelectView(serverControl);

      new MainWindow(serverSelectView, projTreeView, projDetailsView, verDetailsView);

   }
}
