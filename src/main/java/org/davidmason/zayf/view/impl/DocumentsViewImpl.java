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
package org.davidmason.zayf.view.impl;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

import org.davidmason.zayf.view.DocumentsView;
import org.zanata.rest.dto.resource.ResourceMeta;

/**
 * Default Swing implementation of {@link DocumentsView}.
 * 
 * @author David Mason, dr.d.mason@gmail.com
 * 
 */
public class DocumentsViewImpl extends JFrame implements DocumentsView<Component>
{

   private static final long serialVersionUID = 1L;

   private static final String TITLE_PREFIX = "Zayf - documents - ";

   private JTree documentsTree;
   private JScrollPane treeView;

   public DocumentsViewImpl()
   {
      buildGui();
   }

   @SuppressWarnings("serial")
   private void buildGui()
   {
      setLayout(new BorderLayout());
      setTitle(TITLE_PREFIX);
      setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
      setBounds(0, 0, 480, 640);
      // TODO position relative to other window
      setLocationRelativeTo(null);

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
               return getEndOfPath(docPathAndName);
            }

            return value.toString();
         }
      };

      documentsTree.setRootVisible(true);
      documentsTree.putClientProperty("JTree.lineStyle", "Angled");

      treeView = new JScrollPane(documentsTree);
      add(treeView, BorderLayout.CENTER);
   }

   @Override
   public void setTitle(String title)
   {
      super.setTitle(TITLE_PREFIX + title);
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
      setVisible(true);
   }

   // FIXME this should be in a util class
   @Override
   public String getEndOfPath(String path)
   {
      int finalSlash = path.lastIndexOf("/");
      if (finalSlash == -1)
      {
         return path;
      }
      if (finalSlash != path.length() - 1)
      {
         return path.substring(finalSlash + 1);
      }
      throw new RuntimeException("Slash on end of path: " + path);
   }

   @Override
   public Component asWidget()
   {
      return this;
   }
}
