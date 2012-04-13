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
package org.davidmason.zayf.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.*;

/**
 * Swing UI for Zayf client
 */
public class ZayfView extends JFrame
{

   private JTextField urlTextField;
   private JButton refreshProjectsButton, refreshVersionsButton, refreshDocsButton;
   private JList projectsList, versionsList, docsList;
   private JLabel ProjectsLabel, VersionsLabel, DocsLabel;

   public ZayfView()
   {
      setLayout(null); //use absolute positioning
      setBounds(0, 0, 640, 480);
      setResizable(false);
      setLocationRelativeTo(null); //centre screen

      urlTextField = new JTextField("http://localhost:8080/zanata/");
      urlTextField.setPreferredSize(new Dimension(300, 20));
      urlTextField.setBounds(10, 10, 300, 20);

      setUpLists();
      setUpButtons();
      setUpLabels();
      addElements();
   }

   /**
    * add Swing components to frame
    */
   private void addElements()
   {
      add(urlTextField);
      add(projectsList);
      add(versionsList);
      add(docsList);
      add(refreshProjectsButton);
      add(refreshVersionsButton);
      add(refreshDocsButton);
      add(ProjectsLabel);
      add(VersionsLabel);
      add(DocsLabel);
   }

   /**
    * create buttons and add action listeners
    */
   private void setUpButtons()
   {
      refreshProjectsButton = new JButton("Refresh");
      refreshProjectsButton.addActionListener(new ActionListener()
      {

         public void actionPerformed(ActionEvent e)
         {
            System.exit(0);
         }
      });

      refreshVersionsButton = new JButton("Refresh");
      refreshVersionsButton.addActionListener(new ActionListener()
      {

         public void actionPerformed(ActionEvent e)
         {
            System.exit(0);
         }
      });

      refreshDocsButton = new JButton("Refresh");
      refreshDocsButton.addActionListener(new ActionListener()
      {

         public void actionPerformed(ActionEvent e)
         {
            System.exit(0);
         }
      });

      int buttonsY = 70, refreshProjectsButtonX = 70, refreshVersionsButtonX = 270, refreshDocsButtonX =
            470, buttonsSizeX = 80, buttonsSizeY = 20;

      refreshProjectsButton.setBounds(refreshProjectsButtonX, buttonsY, buttonsSizeX, buttonsSizeY);
      refreshVersionsButton.setBounds(refreshVersionsButtonX, buttonsY, buttonsSizeX, buttonsSizeY);
      refreshDocsButton.setBounds(refreshDocsButtonX, buttonsY, buttonsSizeX, buttonsSizeY);
   }

   /**
    * set up labels
    */
   private void setUpLabels()
   {
      ProjectsLabel = new JLabel("Projects");
      VersionsLabel = new JLabel("Versions");
      DocsLabel = new JLabel("Documents");

      int labelsY = 40, projectsLabelX = 85, versionsLabelX = 285, docsLabelX = 480, labelsSizeX =
            80, labelsSizeY = 20;

      ProjectsLabel.setBounds(projectsLabelX, labelsY, labelsSizeX, labelsSizeY);
      VersionsLabel.setBounds(versionsLabelX, labelsY, labelsSizeX, labelsSizeY);
      DocsLabel.setBounds(docsLabelX, labelsY, labelsSizeX, labelsSizeY);
   }

   /**
    * set up JLists
    */
   private void setUpLists()
   {
      projectsList = new JList();
      versionsList = new JList();
      docsList = new JList();

      int listsY = 100, projectsListX = 40, versionsListX = 240, docsListX = 440, listsSizeX = 150, listsSizeY =
            300;

      projectsList.setBounds(projectsListX, listsY, listsSizeX, listsSizeY);
      versionsList.setBounds(versionsListX, listsY, listsSizeX, listsSizeY);
      docsList.setBounds(docsListX, listsY, listsSizeX, listsSizeY);
   }

   /**
    * set text in URL field
    * 
    * @param url
    *           new server URL
    */
   public void setUrl(String url)
   {
      this.urlTextField.setText(url);
   }

}
