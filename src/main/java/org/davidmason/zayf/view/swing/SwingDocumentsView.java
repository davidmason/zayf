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
package org.davidmason.zayf.view.swing;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

import org.davidmason.zayf.util.Util;
import org.davidmason.zayf.view.DocumentsView;
import org.davidmason.zayf.view.MainWindow;
import org.zanata.rest.dto.resource.ResourceMeta;

import com.google.inject.Inject;

/**
 * Default Swing implementation of {@link DocumentsView}.
 * 
 * @author David Mason, dr.d.mason@gmail.com
 * 
 */
class SwingDocumentsView extends JFrame implements DocumentsView<Component>
{

   private static final long serialVersionUID = 1L;

   private static final String TITLE_PREFIX = "Zayf - documents - ";
   private static final String LOADING_DOCUMENTS = "Loading documents...";

   private JTree documentsTree;
   private JScrollPane treeView;
   private JPanel loadingDocumentsPanel;
   private JLabel loadingDocumentsLabel;

   private final MainWindow mainWindow;

   @Inject
   public SwingDocumentsView(MainWindow mainWindow)
   {
      this.mainWindow = mainWindow;
      buildGui();
   }

   private void buildGui()
   {
      setLayout(new BorderLayout());
      setTitle(TITLE_PREFIX);
      setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
      setBounds(0, 0, 480, 640);
      // TODO position relative to other window
      setLocationRelativeTo(mainWindow);

      buildDocumentsTree();
      buildLoadingDocumentsPanel();

      // TODO don't show treeview initially
      add(treeView, BorderLayout.CENTER);
   }

   private void buildLoadingDocumentsPanel()
   {
      loadingDocumentsLabel = new JLabel(LOADING_DOCUMENTS);
      loadingDocumentsPanel = new JPanel();
      loadingDocumentsPanel.add(loadingDocumentsLabel);
   }

   @SuppressWarnings("serial")
   private void buildDocumentsTree()
   {
      documentsTree = new JTree()
      {

         @Override
         public String convertValueToText(Object value, boolean selected, boolean expanded,
                                          boolean leaf, int row, boolean hasFocus)
         {
            if (((DefaultMutableTreeNode) value).getUserObject() instanceof ResourceMeta)
            {
               String docPathAndName =
                     ((ResourceMeta) ((DefaultMutableTreeNode) value).getUserObject()).getName();
               return Util.getEndOfPath(docPathAndName);
            }

            return value.toString();
         }
      };

      documentsTree.setRootVisible(true);
      documentsTree.putClientProperty("JTree.lineStyle", "Angled");

      treeView = new JScrollPane(documentsTree);
   }

   @Override
   public void setTitle(String title)
   {
      super.setTitle(TITLE_PREFIX + title);
   }

   @Override
   public void showDocumentsLoading()
   {
      setVisible(false);
      remove(loadingDocumentsPanel);
      remove(treeView);

      add(loadingDocumentsPanel, BorderLayout.CENTER);
      loadingDocumentsPanel.revalidate();
      loadingDocumentsPanel.repaint();
      setLocationRelativeTo(mainWindow);
      setVisible(true);
   }

   @Override
   public void showDocumentsTree(TreeModel model)
   {
      documentsTree.setModel(model);
      // expand all nodes (for demonstration purposes)
      for (int row = 0; row < documentsTree.getRowCount(); row++)
      {
         documentsTree.expandRow(row);
      }

      remove(loadingDocumentsPanel);
      remove(treeView);

      add(treeView, BorderLayout.CENTER);
      treeView.revalidate();
      treeView.repaint();
      setLocationRelativeTo(mainWindow);
      setVisible(true);
   }

   @Override
   public Component asWidget()
   {
      return this;
   }
}
