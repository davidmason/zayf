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
package org.davidmason.zayf.view;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

import com.google.inject.Inject;

/**
 * Main window of application. This class is responsible for laying out the
 * panels for server, project and version selection.
 * 
 * @author David Mason, dr.d.mason@gmail.com
 */
public class MainWindow extends JFrame
{

   private static final long serialVersionUID = 1L;

   private static final String WINDOW_TITLE = "Zayf | Zanata at your Fingertips";

   // TODO get these from config
   private static final int WINDOW_HEIGHT = 768;
   private static final int WINDOW_WIDTH = 1024;

   private Component serverSelect;
   private Component projectTree;
   private Component projectDetailsView;
   private Component versionDetailsView;

   private JSplitPane projectPane, projectDetailsPane;

   @Inject
   public MainWindow(ServerSelectView<Component> serverSelect,
                     ProjectTreeView<Component> projectTreeView,
                     ProjectDetailsView<Component> projectDetailsView,
                     VersionDetailsView<Component> versionDetailsView)
   {
      this.serverSelect = serverSelect.asWidget();
      this.projectTree = projectTreeView.asWidget();
      this.projectDetailsView = projectDetailsView.asWidget();
      this.versionDetailsView = versionDetailsView.asWidget();

      //      this.docsView = documentsView;

      buildGui();
   }

   private void buildGui()
   {
      setTitle(WINDOW_TITLE);
      setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

      setLayout(new BorderLayout());
      setBounds(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
      setLocationRelativeTo(null); // centre screen

      projectDetailsPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false,
                                          projectDetailsView, versionDetailsView);
      projectDetailsPane.setDividerLocation(120);
      projectDetailsPane.setDividerSize(3);
      projectDetailsPane.setEnabled(true);

      projectPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                   false, projectTree, projectDetailsPane);
      projectPane.setDividerLocation(300);
      projectPane.setDividerSize(3);
      projectPane.setEnabled(true);

      add(serverSelect, BorderLayout.PAGE_START);
      add(projectPane, BorderLayout.CENTER);

      setVisible(true);
   }
}
