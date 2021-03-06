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

import java.awt.event.ActionListener;

import org.zanata.rest.dto.ProjectIteration;

/**
 * View interface for displaying version details and control buttons.
 * 
 * @author David Mason, dr.d.mason@gmail.com
 * 
 * @param <WidgetType>
 *           return type for {{@link #asWidget()}
 */
public interface VersionDetailsView<WidgetType> extends WidgetView<WidgetType>
{

   /**
    * Show basic details for a given version.
    * 
    * @param version
    *           for which to display details.
    */
   public void displayVersion(ProjectIteration version);

   /**
    * Register a listener for a user input to display the document list for the given version.
    * 
    * @param listener
    *           to register.
    */
   public void setShowDocsListener(ActionListener listener);
}
