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

import org.zanata.rest.dto.*;
import org.zanata.rest.dto.resource.*;
import org.davidmason.zayf.rest.*;

import javax.swing.*;
import javax.swing.event.*;

import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import java.net.*;

/**
 * Swing UI for Zayf client
 * 
 * @author A.S.
 */
public class ZayfView extends JFrame
{

   private JTextField urlTextField;
   //TODO: add status bar

   private JButton connectButton;
   private JComboBox projectsComboBox, versionsComboBox, docsComboBox;
   private JLabel ProjectsLabel, VersionsLabel, DocsLabel;
   private DummyServerProxy serverProxy;
   private List<Project> projects;
   private List<ProjectIteration> versions;
   private List<ResourceMeta> docs;

   public ZayfView() //throws MalformedURLException, URISyntaxException
   {
      setLayout(null); //use absolute positioning
      setBounds(0, 0, 640, 480);
      setResizable(false);
      setLocationRelativeTo(null); //centre screen

      urlTextField = new JTextField("http://localhost:8080/zanata/");
      urlTextField.setBounds(10, 10, 300, 20);

      setUpButtons();
      setUpComboBoxes();
      setUpLabels();
      addElements();

      setUpServerProxy();
   }

   /**
    * add Swing components to frame
    */
   private void addElements()
   {
      add(urlTextField);
      add(connectButton);
      add(ProjectsLabel);
      add(VersionsLabel);
      add(DocsLabel);
      add(projectsComboBox);
      add(versionsComboBox);
      add(docsComboBox);
   }

   /**
    * set up buttons
    */
   private void setUpButtons()
   {
      connectButton = new JButton("Connect");
      connectButton.setBounds(312, 10, 100, 20);
      connectButton.addActionListener(new ActionListener()
      {

         public void actionPerformed(ActionEvent e)
         {
            connectToServer();
         }
      });

   }

   private void connectToServer()
   {
      setUpServerProxy();

      getProjects();
   }

   /**
    * get projects from server and populate comboBox
    */
   private void getProjects()
   {
      //if (serverProxy.not connected) return;

      projects = serverProxy.getProjectList();

      projectsComboBox.setEnabled(true);
      projectsComboBox.removeAllItems();
      for (Project project : projects)
         projectsComboBox.addItem(project.getName());
   }

   /**
    * get versions from server and populate comboBox
    */
   private void getVersions()
   {
      if (projectsComboBox.getSelectedIndex() < 0)
         return;

      versions = serverProxy.getVersionList(
                            projectsComboBox.getSelectedItem().toString());

      versionsComboBox.setEnabled(true);
      versionsComboBox.removeAllItems();
      for (ProjectIteration version : versions)
         versionsComboBox.addItem(version.getId());

   }

   /**
    * get docs from server and populate comboBox
    */
   private void getDocs()
   {
      if (versionsComboBox.getSelectedIndex() < 0)
         return;

      docs = serverProxy.getDocList(projectsComboBox.getSelectedItem().toString(),
                                    versionsComboBox.getSelectedItem().toString());

      docsComboBox.setEnabled(true);
      docsComboBox.removeAllItems();
      for (ResourceMeta doc : docs)
         docsComboBox.addItem(doc.getName());
   }

   /**
    * set up comboBoxes
    */
   private void setUpComboBoxes()
   {
      projectsComboBox = new JComboBox();
      projectsComboBox.setBounds(50, 70, 150, 20);
      projectsComboBox.addItem(new String("Projects"));

      projectsComboBox.addActionListener(new ActionListener()
      {

         public void actionPerformed(ActionEvent e)
         {
            getVersions();
         }
      });

      projectsComboBox.setEnabled(false);
      /*projectsComboBox.addPopupMenuListener(new PopupMenuListener() {
         public void popupMenuWillBecomeVisible(PopupMenuEvent e) {dynamic drop-down would go here}
         public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
         public void popupMenuCanceled(PopupMenuEvent e) {}
      });*/

      versionsComboBox = new JComboBox();
      versionsComboBox.setBounds(250, 70, 150, 20);
      versionsComboBox.addItem(new String("Versions"));

      versionsComboBox.addActionListener(new ActionListener()
      {

         public void actionPerformed(ActionEvent e)
         {
            getDocs();
         }
      });

      versionsComboBox.setEnabled(false);

      docsComboBox = new JComboBox();
      docsComboBox.setBounds(450, 70, 150, 20);
      docsComboBox.addItem(new String("Documents"));

      docsComboBox.addActionListener(new ActionListener()
      {

         public void actionPerformed(ActionEvent e)
         {
            /**/}
      });

      docsComboBox.setEnabled(false);
   }

   /**
    * set up labels
    */
   private void setUpLabels()
   {
      ProjectsLabel = new JLabel("Project");
      VersionsLabel = new JLabel("Version");
      DocsLabel = new JLabel("Document");

      int labelsY = 50, projectsLabelX = 85, versionsLabelX = 285, docsLabelX = 480, labelsSizeX =
            80, labelsSizeY = 20;

      ProjectsLabel.setBounds(projectsLabelX, labelsY, labelsSizeX, labelsSizeY);
      VersionsLabel.setBounds(versionsLabelX, labelsY, labelsSizeX, labelsSizeY);
      DocsLabel.setBounds(docsLabelX, labelsY, labelsSizeX, labelsSizeY);
   }

   /**
    * init serverProxy
    */
   private void setUpServerProxy()
   {
      serverProxy = new DummyServerProxy();
      //TODO: update status bar: connecting...
      //ServerProxy sp = new ServerProxy(new URL("http://localhost:8080/zanata/").toURI(), "admin", "REDACTED");
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

   /**
    * get projects from server
    */
   public void refreshProjects()
   {
      /*projectsListModel.clear();
      versionsListModel.clear();
      docsListModel.clear();*/

      projects = serverProxy.getProjectList();

      /*for (Project project : projects)
         projectsListModel.addElement(project.getName());*/

      /*
      projectsList.setEnabled(true);
      versionsList.setEnabled(false);
      docsList.setEnabled(false);
      */

      /* this code not functionally compatible with selection events - cascades
      projectsList.requestFocusInWindow();
      if (projectsListModel.getSize() > 0)
         projectsList.setSelectedIndex(0);
      */
   }

   /**
    * get versions for project
    */
   public void refreshVersions()
   {
      /*versionsListModel.clear();
      docsListModel.clear();
      
      if (projectsList.getSelectedIndex() == -1) return; //TODO: error dialog - must select a project
      versions = serverProxy.getVersionList(projects.get(projectsList.getSelectedIndex()).getId());

      for (ProjectIteration version : versions)
         versionsListModel.addElement(version.getId());
      */
      /*
      projectsList.setEnabled(false);
      versionsList.setEnabled(true);
      docsList.setEnabled(false);
      */

      /* this code not functionally compatible with selection events - cascades
      versionsList.requestFocusInWindow();
      if (versionsListModel.getSize() > 0)
         versionsList.setSelectedIndex(0);
      */
   }

   /**
    * get documents for version
    */
   public void refreshDocs()
   {
      /*docsListModel.clear();

      if (versionsList.getSelectedIndex() == -1) return; //TODO: error dialog - must select a version
      docs = serverProxy.getDocList(   projects.get(projectsList.getSelectedIndex()).getId(),
                              versions.get(versionsList.getSelectedIndex()).getId());
      
      for (ResourceMeta doc : docs)
         docsListModel.addElement(doc.getName());
      */
      /*
      projectsList.setEnabled(false);
      versionsList.setEnabled(false);
      docsList.setEnabled(true);
      */

      /* this code not functionally compatible with selection events - cascades
      docsList.requestFocusInWindow();
      if (docsListModel.getSize() > 0)
         docsList.setSelectedIndex(0);
      */
   }
}
