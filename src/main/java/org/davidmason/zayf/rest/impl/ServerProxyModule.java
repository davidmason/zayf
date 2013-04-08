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
package org.davidmason.zayf.rest.impl;

import org.davidmason.zayf.rest.ServerProxyProvider;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;


public class ServerProxyModule extends AbstractModule
{

   @Override
   protected void configure()
   {
      bind(ServerProxyProvider.class).to(ServerProxyProviderImpl.class).in(Scopes.SINGLETON);

   }

   // Decision: inject a provider for server proxies that keeps a map of
   //           proxies, mapped against server URL. Standard lookup is
   //           by providing a ServerInfo from which the URL will be
   //           looked up. This could later include credentials in the
   //           lookup, or use ServerInfo.equals() (make sure it behaves
   //           as expected first).

}
