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
package org.davidmason.zayf.model;

import java.net.URL;

/**
 * Holds url, username and API key for an available server.
 * 
 * @author David Mason, <a href="mailto:damason@redhat.com">damason@redhat.com</a>
 * 
 */
public class ServerInfo
{

   private String serverName;
   private URL serverUrl;
   private String userName;
   private String apiKey;

   public ServerInfo(String serverName, URL serverUrl, String userName, String apiKey)
   {
      this.serverName = serverName;
      this.serverUrl = serverUrl;
      this.userName = userName;
      this.apiKey = apiKey;
   }

   public String getServerName()
   {
      return serverName;
   }

   public URL getServerUrl()
   {
      return serverUrl;
   }

   public String getUserName()
   {
      return userName;
   }

   public String getApiKey()
   {
      return apiKey;
   }

   @Override
   public String toString()
   {
      return "Name: " + serverName + ", User: " + userName + ", Key: " + apiKey + ", Url: "
             + serverUrl;
   }
}
