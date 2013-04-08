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

//import org.apache.log4j.lf5.viewer.categoryexplorer.TreeModelAdapter; //TODO: wat?
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.URISyntaxException;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.davidmason.zayf.persistence.FileIO;
import org.davidmason.zayf.rest.DummyServerProxy;
import org.davidmason.zayf.rest.ServerProxy;
import org.davidmason.zayf.rest.impl.ServerProxyImpl;
import org.zanata.common.LocaleId;
import org.zanata.rest.dto.Project;
import org.zanata.rest.dto.ProjectIteration;
import org.zanata.rest.dto.resource.ResourceMeta;
import org.zanata.rest.dto.resource.TextFlow;
import org.zanata.rest.dto.resource.TextFlowTarget;

/**
 * Swing UI for Zayf client
 * 
 * @author A.S.
 */
@SuppressWarnings({"serial", "unused"})
public class ZayfView extends JFrame
{

   private static boolean USE_DUMMY_SERVER = false;

   private JMenuBar menuBar;

   private ProjectsTree displayTree;
   private DefaultMutableTreeNode rootNode;
   private DefaultTreeModel treeModel;
   private JScrollPane treeView;

   private TextFlowPanel textFlowPanel;
   private TextFlowTargetPanel textFlowTargetPanel;
   private StatusBar statusBar;
   private JPanel centrePanel;

   private ServerProxy serverProxy;
   private String url = "http://localhost:8080/zanata/";
   private String userName = "damason"; //"admin";
   private String apiKey =
         /* "REDACTED"; */"REDACTED";
   private LocaleId targetLocale = new LocaleId("de");

   /*
   private List<Project> projects;
   private List<ProjectIteration> iterations;
   private List<ResourceMeta> docs;
   private List<TextFlow> textFlows;
   private List<TextFlowTarget> textFlowTargets;
   */
   public ZayfView() //throws MalformedURLException, URISyntaxException
   {
      setLayout(new BorderLayout()); //use absolute positioning
      setBounds(0, 0, 640, 480);
      setLocationRelativeTo(null); //centre screen

      setUpMenus();
      setUpTree();
      setUpTextFlowPanels();
      addComponents();

      setUpServerProxy();
      getProjects();

      setTitle("Zayf v 0.00000001");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setVisible(true);
   }

   /**
    * set up Text fields for displaying text flows and text flow targets
    */
   private void setUpTextFlowPanels()
   {
      centrePanel = new JPanel();
      centrePanel.setLayout(new BoxLayout(centrePanel, BoxLayout.PAGE_AXIS));

      textFlowPanel = new TextFlowPanel();
      textFlowTargetPanel = new TextFlowTargetPanel(targetLocale);

      clearTextPanes();

      centrePanel.add(textFlowPanel);
      centrePanel.add(textFlowTargetPanel);
   }

   private void clearTextPanes()
   {
      textFlowPanel.clear();

   }

   /**
    * set up Tree view
    */
   private void setUpTree()
   {
      rootNode = new DefaultMutableTreeNode(url);
      treeModel = new DefaultTreeModel(rootNode);
      displayTree = new ProjectsTree(treeModel);
      displayTree.setPreferredSize(new Dimension(200, 200));

      treeView = new JScrollPane(displayTree);

      /*displayTree.addTreeExpansionListener(new TreeExpansionListener()
      {
         @Override
         public void treeExpanded(TreeExpansionEvent event)
         {
            //System.exit(0);
            // TODO Auto-generated method stub
         }

         @Override
         public void treeCollapsed(TreeExpansionEvent event)
         {
            // TODO Auto-generated method stub
         }
      });*/
      displayTree.addTreeSelectionListener(new TreeSelectionListener()
      {

         @Override
         public void valueChanged(TreeSelectionEvent e)
         {
            DefaultMutableTreeNode node =
                  (DefaultMutableTreeNode) e.getNewLeadSelectionPath().getLastPathComponent();

            if (node == null)
               return;

            Object nodeObject = node.getUserObject();

            if (nodeObject instanceof TextFlow)
            {
               //show text flow content
               TextFlow tf = (TextFlow) nodeObject;
               textFlowPanel.update(tf);
               int childCount = node.getParent().getChildCount();

               outerLoop:
               for (int i = 0; i < childCount; i++)
               {
                  DefaultMutableTreeNode TFTnode =
                        (DefaultMutableTreeNode) node.getParent().getChildAt(i);
                  if (TFTnode.getUserObject() instanceof TextFlowTarget)
                  {
                     //show text flow target content
                     TextFlowTarget tft = (TextFlowTarget) TFTnode.getUserObject();

                     if (tft.getResId() == tf.getId())
                     {
                        textFlowTargetPanel.update(tft);

                        break outerLoop;
                     }

                     textFlowTargetPanel.notFoundError();
                  }
               }
            }
            else if (nodeObject instanceof TextFlowTarget)
            {
               //show text flow target content
               TextFlowTarget tft = (TextFlowTarget) nodeObject;
               textFlowTargetPanel.update(tft);
               int childCount = node.getParent().getChildCount();

               outerLoop:
               for (int i = 0; i < childCount; i++)
               {
                  DefaultMutableTreeNode TFnode =
                        (DefaultMutableTreeNode) node.getParent().getChildAt(i);
                  if (TFnode.getUserObject() instanceof TextFlow)
                  {
                     //show text flow content
                     TextFlow tf = (TextFlow) TFnode.getUserObject();

                     if (tft.getResId() == tf.getId())
                     {
                        textFlowPanel.update(tf);

                        break outerLoop;
                     }

                     textFlowPanel.notFoundError(); //theoretically impossible
                  }
               }

            }
            else
               clearTextPanes();
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

      menuItem = new JMenuItem("Save Project 1", KeyEvent.VK_S);
      menuItem.addActionListener(new ActionListener()
      {

         public void actionPerformed(ActionEvent e)
         {
            if (!FileIO.saveProject((DefaultMutableTreeNode) rootNode.getChildAt(0)))
               System.err.println("Project save failed.");
         }
      });

      menu.add(menuItem);

      menuItem = new JMenuItem("Load Dummy Project 1", KeyEvent.VK_S);
      menuItem.addActionListener(new ActionListener()
      {

         public void actionPerformed(ActionEvent e)
         {
            if (!FileIO.loadProject(new java.io.File("Dummy Project 1/Dummy Project 1.xml")))
               System.err.println("Project load failed.");
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

   /** cleanup and exit program */
   private void quit()
   {
      //TODO: cleanup
      System.exit(0);
   }

   /**
    * get projects from server and populate tree
    */
   private void getProjects()
   {
      List<Project> projectList = serverProxy.getProjectList();
      System.out.println("Got server list");
      for (Project project : projectList)
      {
         DefaultMutableTreeNode projectBranch = new DefaultMutableTreeNode(project);
         rootNode.add(projectBranch);

         //TODO: load child nodes on expansion only.

         List<ProjectIteration> versionList = serverProxy.getVersionList(project.getId());
         System.out.println("Got version list for project " + project.getId());
         for (ProjectIteration version : versionList)
         {
            DefaultMutableTreeNode iterationBranch = new DefaultMutableTreeNode(version);
            projectBranch.add(iterationBranch);

            List<ResourceMeta> docList = serverProxy.getDocList(project.getId(), version.getId());
            System.out.println("Got document list for version " + version.getId());
            for (ResourceMeta doc : docList) //get docs from SP
            {
               DefaultMutableTreeNode docBranch = new DefaultMutableTreeNode(doc);
               iterationBranch.add(docBranch);

               List<TextFlow> textFlows =
                     serverProxy.getTextFlows(project.getId(), version.getId(), doc.getName());
               System.out.println("Got text flows for document " + doc.getName());
               for (TextFlow tf : textFlows)
               {
                  DefaultMutableTreeNode tfNode = new DefaultMutableTreeNode(tf);
                  docBranch.add(tfNode);
               }

               List<TextFlowTarget> targets =
                     serverProxy.getTargets(project.getId(), version.getId(), targetLocale,
                                            doc.getName());
               System.out.println("Got targets for document " + doc.getName());
               for (TextFlowTarget tft : targets)
               {
                  DefaultMutableTreeNode tftNode = new DefaultMutableTreeNode(tft);
                  docBranch.add(tftNode);
               }
            }
         }
      }

      treeModel = new DefaultTreeModel(rootNode);
      displayTree = new ProjectsTree(treeModel);
      displayTree.setPreferredSize(new Dimension(200, 200));
      treeView = new JScrollPane(displayTree);

      ((DefaultTreeModel) displayTree.getModel()).reload();
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
            if (USE_DUMMY_SERVER)
               serverProxy = new ServerProxyImpl();
            //               serverProxy = new ServerProxy(new URL(ncf.getUrl()).toURI(), ncf.getUserName(), ncf.getApiKey());
            else
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
      if (USE_DUMMY_SERVER)
      {
         try
         {
            serverProxy = new DummyServerProxy();
         }
         catch (URISyntaxException e)
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
      else
      {
         //         URI uri;
         //         try
         //         {
         //            uri = new URL(url).toURI();
         //         }
         //         catch (MalformedURLException e)
         //         {
         //            // FIXME show user message about invalid URL
         //            e.printStackTrace();
         //            return;
         //         }
         //         catch (URISyntaxException e)
         //         {
         //            // FIXME show user message about invalid URL
         //            e.printStackTrace();
         //            return;
         //         }
         //         serverProxy = new ServerProxy(uri, userName, apiKey);
         try
         {
            serverProxy = new ServerProxyImpl();
         }
         catch (URISyntaxException e)
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
         System.out.println("Connected to real server");
      }
      statusBar.setConnection("Connected", new Color(0, 120, 0));
   }

}
