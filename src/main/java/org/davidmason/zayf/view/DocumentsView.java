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

import javax.swing.tree.TreeModel;

/**
 * View interface for displaying a tree of documents.
 * 
 * @author David Mason, dr.d.mason@gmail.com
 * 
 * @param <WidgetType>
 *           return type for {{@link #asWidget()}
 */
public interface DocumentsView<WidgetType> extends WidgetView<WidgetType>
{

   /**
    * Set title for document display area
    */
   public void setTitle(String title);

   /**
    * Display a given tree of documents.
    */
   public void showDocumentsTree(TreeModel model);

   // TODO put this in a util class, or use existing util class if present
   public String getEndOfPath(String path);

}
