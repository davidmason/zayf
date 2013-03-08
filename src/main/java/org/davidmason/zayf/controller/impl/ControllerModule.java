/*
 * Zayf (Zanata at your Fingertips) - a Zanata client for unstable connections
 * Copyright (C) 2013  Alister Symons and David Mason
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
package org.davidmason.zayf.controller.impl;


import org.davidmason.zayf.controller.ServerConfigLoader;

import com.google.inject.AbstractModule;

/**
 * Guice bindings for controller classes.
 * 
 * @author David Mason, dr.d.mason@gmail.com
 * 
 */
public class ControllerModule extends AbstractModule
{

   @Override
   protected void configure()
   {
      bind(DocumentsController.class).asEagerSingleton();
      bind(ProjectDetailsController.class).asEagerSingleton();
      bind(ProjectTreeController.class).asEagerSingleton();
      bind(ServerConfigLoader.class).to(ServerSelectController.class).asEagerSingleton();
      bind(VersionDetailsController.class).asEagerSingleton();
   }

}
