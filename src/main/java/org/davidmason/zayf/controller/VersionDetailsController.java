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
package org.davidmason.zayf.controller;

import org.davidmason.zayf.view.VersionDetailsView;
import org.zanata.rest.dto.ProjectIteration;

public class VersionDetailsController
{

   private VersionDetailsView view;

   public VersionDetailsController(VersionDetailsView view)
   {
      this.view = view;
   }

   /**
    * Look up and show stats for version, and show buttons for version actions.
    * 
    * @param version
    */
   public void showVersion(ProjectIteration version)
   {
      view.displayVersion(version);
   }

}