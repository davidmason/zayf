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

import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;

/**
 * View interface for displaying a list of projects.
 * 
 * @author David Mason, dr.d.mason@gmail.com
 * 
 * @param <WidgetType>
 *           return type for {{@link #asWidget()}
 */
public interface ProjectTreeView<WidgetType> extends WidgetView<WidgetType>
{

   /**
    * Display a message indicating that projects are currently loading. This message will
    * automatically clear when {@link #showProjectTree(TreeModel)} is called.
    */
   void showProjectsLoading();

   /**
    * Display the given model.
    */
   public void showProjectTree(TreeModel model);

   /**
    * Add a listener for selection of a project from the list.
    */
   public void addSelectionListener(TreeSelectionListener listener);

}
