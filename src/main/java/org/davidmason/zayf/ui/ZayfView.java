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

import org.zanata.common.LocaleId;
import org.zanata.rest.dto.*;
import org.zanata.rest.dto.resource.*;
import org.davidmason.zayf.rest.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.DefaultMutableTreeNode;

//import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
import java.net.URL;

/**
 * Swing UI for Zayf client
 * 
 * @author A.S.
 */
@SuppressWarnings({"serial", "unused"})
public class ZayfView extends JFrame
{

   private JMenuBar menuBar;

   private ProjectsTree displayTree;
   private DefaultMutableTreeNode root;
   private JScrollPane treeView;

   private JTextArea textFlowPane, textFlowTargetPane;
   private StatusBar statusBar;
   private Container centrePanel;

   private ServerProxy serverProxy;
   private String url = "http://localhost:8080/zanata/";
   private String userName = "admin";
   private String apiKey = "REDACTED";
   private LocaleId targetLocale = new LocaleId("en-US");

   private List<Project> projects;
   private List<ProjectIteration> iterations;
   private List<ResourceMeta> docs;
   private List<TextFlow> textFlows;
   private List<TextFlowTarget> textFlowTargets;

   public ZayfView() //throws MalformedURLException, URISyntaxException
   {
      setLayout(new BorderLayout()); //use absolute positioning
      setBounds(0, 0, 640, 480);
      setLocationRelativeTo(null); //centre screen

      setUpMenus();
      setUpTree();
      setUpTextPanes();

      addComponents();

      connectToServer();

      setTitle("Zayf v 0.00000001");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setVisible(true);
   }

   /**
    * set up Text fields for displaying text flows and text flow targets
    */
   private void setUpTextPanes()
   {
      centrePanel = new JPanel();
      centrePanel.setLayout(new BoxLayout(centrePanel, BoxLayout.PAGE_AXIS));

      JPanel upperCentrePanel = new JPanel(new BorderLayout());

      JLabel textFlowLabel = new JLabel("Text Flow:"); //TODO: confirm "Text Flow" is correct user-speak
      textFlowPane = new JTextArea();
      textFlowPane.setForeground(Color.gray);
      textFlowPane.setText("Select a document in tree view");
      textFlowPane.setEditable(false);

      upperCentrePanel.add(textFlowLabel, BorderLayout.NORTH);
      upperCentrePanel.add(textFlowPane, BorderLayout.CENTER);

      JPanel lowerCentrePanel = new JPanel(new BorderLayout());

      JLabel textFlowTargetLabel = new JLabel("Text Flow Target:"); //TODO: confirm "Text Flow Target" is correct user-speak
      textFlowTargetPane = new JTextArea();
      textFlowTargetPane.setForeground(Color.gray);
      textFlowTargetPane.setText("Select a document in tree view.\nOnly text flow targets for the \""
                                 + targetLocale.getId() + "\" locale are displayed");
      textFlowTargetPane.setEditable(false);

      lowerCentrePanel.add(textFlowTargetLabel, BorderLayout.NORTH);
      lowerCentrePanel.add(textFlowTargetPane, BorderLayout.CENTER);

      centrePanel.add(upperCentrePanel);
      centrePanel.add(lowerCentrePanel);
   }

   /**
    * set up Tree view
    */
   private void setUpTree()
   {
      root = new DefaultMutableTreeNode(url);

      displayTree = new ProjectsTree(root);
      displayTree.setPreferredSize(new Dimension(200, 200));
      treeView = new JScrollPane(displayTree);

      displayTree.addTreeExpansionListener(new TreeExpansionListener()
      {

         @Override
         public void treeExpanded(TreeExpansionEvent event)
         {
            // TODO Auto-generated method stub
         }

         @Override
         public void treeCollapsed(TreeExpansionEvent event)
         {
            // TODO Auto-generated method stub
         }
      });
   }

   /**
    * add Swing components to frame
    */
   private void addComponents()
   {
      JPanel topPanel = new JPanel(new BorderLayout());

      topPanel.add(menuBar, BorderLayout.NORTH);

      add(topPanel, BorderLayout.NORTH);
      add(treeView, BorderLayout.WEST);
      add(centrePanel, BorderLayout.CENTER);

      statusBar = new StatusBar();
      add(statusBar, BorderLayout.SOUTH);
   }

   /** set up the menu bar */
   private void setUpMenus()
   {
      menuBar = new JMenuBar();

      setUpFileMenu();
   }

   /** set up File menu and add to menu bar */
   private void setUpFileMenu()
   {
      JMenu menu = new JMenu("File");
      menu.setMnemonic(KeyEvent.VK_F);

      JMenuItem menuItem = new JMenuItem("Connect...", KeyEvent.VK_C);
      menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, ActionEvent.CTRL_MASK));
      //TODO: figure out why shortcut only works when menu has focus, fix
      menuItem.addActionListener(new ActionListener()
      {

         public void actionPerformed(ActionEvent e)
         {
            openNewConnectionFrame();
         }
      });

      menu.add(menuItem);

      menuItem = new JMenuItem("Disconnect", KeyEvent.VK_D);
      menuItem.addActionListener(new ActionListener()
      {

         public void actionPerformed(ActionEvent e)
         {
            //TODO:
         }
      });

      menu.add(menuItem);

      menuItem = new JMenuItem("Save Project...", KeyEvent.VK_S);
      menuItem.addActionListener(new ActionListener()
      {

         public void actionPerformed(ActionEvent e)
         {
            //TODO:
         }
      });

      menu.add(menuItem);

      menuItem = new JMenuItem("Exit", KeyEvent.VK_X);
      menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
      menuItem.addActionListener(new ActionListener()
      {

         public void actionPerformed(ActionEvent e)
         {
            quit();
         }
      });

      menu.add(menuItem);

      menuBar.add(menu);
   }

   private void quit()
   {
      //TODO: cleanup
      System.exit(0);
   }

   private void connectToServer()
   {
      setUpServerProxy();

      getProjects();
   }

   /**
    * get projects from server and populate tree
    */
   private void getProjects()
   {
      projects = serverProxy.getProjectList();

      displayTree = new ProjectsTree(root);
      displayTree.setPreferredSize(new Dimension(200, 200));
      treeView = new JScrollPane(displayTree);

      for (Project project : projects)
      {
         DefaultMutableTreeNode projectBranch = new DefaultMutableTreeNode(project);
         root.add(projectBranch);

         //TODO: load child nodes on expansion only.
         for (ProjectIteration iteration : serverProxy.getVersionList(project.getId())) //get iterations from SP
         {
            DefaultMutableTreeNode iterationBranch = new DefaultMutableTreeNode(iteration);
            projectBranch.add(iterationBranch);

            for (ResourceMeta doc : serverProxy.getDocList(project.getId(), iteration.getId())) //get docs from SP
            {
               DefaultMutableTreeNode docBranch = new DefaultMutableTreeNode(doc.getName());
               iterationBranch.add(docBranch);

               /*
               for (TextFlow tf : serverProxy.getTextFlows(project.getId(), iteration.getId(), doc.getName()))
               {
                  DefaultMutableTreeNode tfNode = new DefaultMutableTreeNode(tf.getId());
                  docBranch.add(tfNode);
               }
               
               for (TextFlowTarget tft : serverProxy.getTargets(project.getId(), iteration.getId(), targetLocale, doc.getName()))
               {
                  DefaultMutableTreeNode tftNode = new DefaultMutableTreeNode(tft.getContent());
                  docBranch.add(tftNode);
               }
               //if should only show one language at a time, will use text panes to show TF & TFT
               //for (TextFlowTarget tft : serverProxy.getTargets(project.getId(), iteration.getId(), locale, docId))
                */
            }
         }
      }
   }

   /** opens a modal dialog which allows the user to connect to a database */
   private void openNewConnectionFrame()
   {
      NewConnectionFrame ncf = new NewConnectionFrame();

      if (ncf.connectPressed())
      {
         try
         {
            //TODO: if connected, disconnect (if SP can force disconnect)

            //serverProxy = new ServerProxy(new URL(ncf.getUrl()).toURI(), ncf.getUserName(), ncf.getApiKey());
            serverProxy = new DummyServerProxy();
            statusBar.setConnection("Connected", new Color(0, 120, 0));
         }
         catch (Exception e)
         {
            statusBar.setConnection("Connection Failed", Color.RED);
            JOptionPane.showMessageDialog(null, e.getMessage(), "Connection Failed", 0);
         }
      }
   }

   /**
    * init serverProxy
    */
   private void setUpServerProxy()
   {
      serverProxy = new DummyServerProxy();
      statusBar.setConnection("Connected", new Color(0, 120, 0));
      //ServerProxy sp = new ServerProxy(new URL(url).toURI(), userName, apiKey);
   }

}
