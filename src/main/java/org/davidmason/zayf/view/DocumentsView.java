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

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

import org.zanata.rest.dto.resource.ResourceMeta;

public class DocumentsView extends JPanel
{

   private static final long serialVersionUID = 1L;

   private JLabel fooLabel;
   private JTree documentsTree;
   private JScrollPane treeView;

   public DocumentsView()
   {
      buildGui();
   }

   private void buildGui()
   {
      setLayout(new BorderLayout());
      fooLabel = new JLabel("Documents will go here.");
      add(fooLabel, BorderLayout.CENTER);

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

      documentsTree.setRootVisible(false);
      documentsTree.putClientProperty("JTree.lineStyle", "Angled");

      treeView = new JScrollPane(documentsTree);
      add(treeView, BorderLayout.CENTER);
   }

   public void showDocumentsTree(TreeModel model)
   {
      documentsTree.setModel(model);
   }

   // TODO put this in a util class, or use existing util class if present
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
}
