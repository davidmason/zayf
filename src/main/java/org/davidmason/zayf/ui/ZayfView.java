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
import javax.swing.tree.DefaultMutableTreeNode;

import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.*;

/**
 * Swing UI for Zayf client
 * 
 * @author A.S.
 */
public class ZayfView extends JFrame
{

   private JMenuBar menuBar;

   private JTree displayTree;
   private DefaultMutableTreeNode root;

   private ServerProxy serverProxy;
   private String url = "http://localhost:8080/zanata/";
   private String userName = "admin";
   private String apiKey = "REDACTED";

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

      addComponents();

      setUpServerProxy();
      getProjects();

      setTitle("Zayf v 0.00000001");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setVisible(true);
   }

   /**
    * set up Tree view
    */
   private void setUpTree()
   {
      root = new DefaultMutableTreeNode(url);

      displayTree = new JTree(root);
      JScrollPane treeView = new JScrollPane(displayTree);

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

      add(displayTree, BorderLayout.WEST);
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

      JMenuItem menuItem = new JMenuItem("Connect...", KeyEvent.VK_N);
      menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, ActionEvent.CTRL_MASK));
      menuItem.addActionListener(new ActionListener()
      {

         public void actionPerformed(ActionEvent e)
         {
            //TODO:
         }
      });

      menu.add(menuItem);

      menuItem = new JMenuItem("Disconnect", KeyEvent.VK_D);
      //menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
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

      displayTree = new JTree(root);
      JScrollPane treeView = new JScrollPane(displayTree);

      for (Project project : projects)
      {
         DefaultMutableTreeNode projectBranch = new DefaultMutableTreeNode(project.getName());
         root.add(projectBranch);

         //TODO: load child nodes on expansion only.
         for (ProjectIteration iteration : serverProxy.getVersionList(project.getId())) //get iterations from SP
         {
            DefaultMutableTreeNode iterationBranch = new DefaultMutableTreeNode(iteration.getId());
            projectBranch.add(iterationBranch);

            for (ResourceMeta doc : serverProxy.getDocList(project.getId(), iteration.getId())) //get docs from SP
            {
               DefaultMutableTreeNode docBranch = new DefaultMutableTreeNode(doc.getName());
               iterationBranch.add(docBranch);

               for (TextFlow tf : serverProxy.getTextFlows(project.getId(), iteration.getId(),
                                                           doc.getName()))
               {
                  DefaultMutableTreeNode tfNode = new DefaultMutableTreeNode(tf.getId());
                  docBranch.add(tfNode);
               }

               //TODO: find out if must/should specify locale when querying for TFT's
               //if should only show one language at a time, will use text panes to show TF & TFT
               //for (TextFlowTarget tft : serverProxy.getTargets(project.getId(), iteration.getId(), locale, docId))
            }
         }
      }
   }

   /**
    * init serverProxy
    */
   private void setUpServerProxy()
   {
      serverProxy = new DummyServerProxy();
      //TODO: update status bar: connecting...
      //ServerProxy sp = new ServerProxy(new URL(url).toURI(), userName, apiKey);
   }

}
