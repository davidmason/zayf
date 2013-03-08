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
import java.util.List;

import org.davidmason.zayf.model.ServerInfo;

/**
 * View interface for displaying a list of servers for user selection.
 * 
 * @author David Mason, dr.d.mason@gmail.com
 * 
 * @param <WidgetType>
 *           return type for {{@link #asWidget()}
 */
public interface ServerSelectView<WidgetType> extends WidgetView<WidgetType>
{

   /**
    * Add listener for user indicating that the project list for a server should be shown.
    */
   void addLoadProjectListener(ActionListener listener);

   /**
    * Display a list of servers from which user can choose.
    * 
    * @param servers
    */
   void showServers(List<ServerInfo> servers);

   /**
    * Retrieve info for the server that is currently selected by the user.
    */
   ServerInfo getSelectedServerInfo();

}
