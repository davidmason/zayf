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

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.List;

import javax.swing.*;

/**
 * Swing UI for Zayf client
 * 
 * @author A.S.
 */
public class ZayfView extends JFrame
{

   private JTextField urlTextField;
   //TODO: add status bar
   private JButton refreshProjectsButton, refreshVersionsButton, refreshDocsButton;
   private JList projectsList, versionsList, docsList;
   private DefaultListModel projectsListModel, versionsListModel, docsListModel;
   private JLabel ProjectsLabel, VersionsLabel, DocsLabel;
   private DummyServerProxy serverProxy;
   private List<Project> projects;
   private List<ProjectIteration> versions;
   private List<ResourceMeta> docs;

   public ZayfView() throws MalformedURLException, URISyntaxException
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

      setUpServerProxy();
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
      refreshVersionsButton = new JButton("Refresh");
      refreshDocsButton = new JButton("Refresh");
      //refreshVersionsButton.setEnabled(false);
      //refreshDocsButton.setEnabled(false);
      projectsList.setEnabled(false);
      versionsList.setEnabled(false);

      //TODO: action listeners for double clicking JLists/ListModels
      refreshProjectsButton.addActionListener(new ActionListener()
      {

         public void actionPerformed(ActionEvent e)
         {
            projectsListModel.clear();
            versionsListModel.clear();
            docsListModel.clear();

            projects = serverProxy.getProjectList();

            for (Project project : projects)
               projectsListModel.addElement(project.getName());

            projectsList.setEnabled(true);
            versionsList.setEnabled(false);
            docsList.setEnabled(false);
            //TODO: on select event for projectsList, refreshVersionsButton.setEnabled(true);refreshDocsButton.setEnabled(false);
         }
      });

      /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////   
      refreshVersionsButton.addActionListener(new ActionListener()
      {

         public void actionPerformed(ActionEvent e)
         {
            versionsListModel.clear();
            docsListModel.clear();

            if (projectsList.getSelectedIndex() == -1)
               return; //TODO: error dialog - must select a project
            versions =
                  serverProxy.getVersionList(projects.get(projectsList.getSelectedIndex()).getId());

            for (ProjectIteration version : versions)
               versionsListModel.addElement(version.getId());

            projectsList.setEnabled(false);
            versionsList.setEnabled(true);
            docsList.setEnabled(false);
         }
      });

      /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      refreshDocsButton.addActionListener(new ActionListener()
      {

         public void actionPerformed(ActionEvent e)
         {
            docsListModel.clear();

            if (versionsList.getSelectedIndex() == -1)
               return; //TODO: error dialog - must select a version
            docs = serverProxy.getDocList(projects.get(projectsList.getSelectedIndex()).getId(),
                                          versions.get(versionsList.getSelectedIndex()).getId());

            for (ResourceMeta doc : docs)
               docsListModel.addElement(doc.getName());

            projectsList.setEnabled(false);
            versionsList.setEnabled(false);
            docsList.setEnabled(true);
         }
      });

      /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
      projectsListModel = new DefaultListModel();
      projectsList = new JList(projectsListModel);
      projectsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
      projectsList.setLayoutOrientation(JList.VERTICAL);
      projectsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

      //      JScrollPane listScroller = new JScrollPane(projectsList);
      //   listScroller.setPreferredSize(new Dimension(250, 80));            

      //////////////////////////////////////////////////////////////////////

      versionsListModel = new DefaultListModel();
      versionsList = new JList(versionsListModel);
      versionsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
      versionsList.setLayoutOrientation(JList.VERTICAL);
      versionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

      docsListModel = new DefaultListModel();
      docsList = new JList(docsListModel);
      docsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
      docsList.setLayoutOrientation(JList.VERTICAL);
      docsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

      int listsY = 100, projectsListX = 40, versionsListX = 240, docsListX = 440, listsSizeX = 150, listsSizeY =
            300;

      projectsList.setBounds(projectsListX, listsY, listsSizeX, listsSizeY);
      versionsList.setBounds(versionsListX, listsY, listsSizeX, listsSizeY);
      docsList.setBounds(docsListX, listsY, listsSizeX, listsSizeY);

      //////////////////////////////////////////////////////////////////////

   }

   /**
    * init serverProxy
    */
   private void setUpServerProxy() throws MalformedURLException, URISyntaxException
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

}
