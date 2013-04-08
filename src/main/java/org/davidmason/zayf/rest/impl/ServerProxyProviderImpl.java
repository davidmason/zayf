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

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.davidmason.zayf.model.ServerInfo;
import org.davidmason.zayf.rest.ServerProxy;
import org.davidmason.zayf.rest.ServerProxyProvider;


public class ServerProxyProviderImpl implements ServerProxyProvider
{

   private Logger log = Logger.getLogger(ServerProxyProviderImpl.class);

   private final Map<URL, ServerProxy> proxies;

   public ServerProxyProviderImpl()
   {
      proxies = new HashMap<URL, ServerProxy>();
   }

   @Override
   public ServerProxy get(ServerInfo info)
   {
      ServerProxy proxy = proxies.get(info.getServerUrl());
      if (proxy == null)
      {
         URI uri;
         try
         {
            uri = info.getServerUrl().toURI();
         }
         catch (URISyntaxException e)
         {
            // FIXME throw this exception to be handled by the calling class,
            //       or change ServerInfo to have a URI so that this can be
            //       handled when the server is first loaded from config.
            //       (could then display server as invalid in the UI with an
            //       appropriate tooltip).
            // TODO show failure message in UI
            // TODO clear projects tree, or add a heading so it shows which server is being displayed.
            log.error("invalid URL for selected server, url: " + info.getServerUrl());
            return null;
         }
         proxy = new ServerProxyImpl(uri, info.getUserName(), info.getApiKey());
         proxies.put(info.getServerUrl(), proxy);
      }
      return proxy;
   }

}
