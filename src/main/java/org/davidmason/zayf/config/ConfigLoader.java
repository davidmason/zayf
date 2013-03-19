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
package org.davidmason.zayf.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DataConfiguration;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.log4j.Logger;
import org.davidmason.zayf.model.ServerInfo;

/**
 * Loads server configuration values from a config file
 * (e.g. default zanata.ini file).
 * 
 * @author David Mason, dr.d.mason@gmail.com
 * 
 */
public class ConfigLoader
{

   private Logger log = Logger.getLogger(ConfigLoader.class);

   private static final String DEFAULT_CONFIG_LOCATION = ".config/zanata.ini";
   private static final String URL_KEY_SUFFIX = ".url";
   private static final String USER_NAME_SUFFIX = ".username";
   private static final String API_KEY_SUFFIX = ".key";

   private File userConfig;

   /**
    * Make a loader that uses the default zanata.ini file.
    */
   public ConfigLoader()
   {
      userConfig = new File(System.getProperty("user.home"), DEFAULT_CONFIG_LOCATION);
      log.info("Config file: " + userConfig.getAbsolutePath());
   }

   public List<ServerInfo> getServerInfo()
   {
      try
      {
         return getServerList();
      }
      catch (ConfigurationException e)
      {
         log.error("Failed to load server list, using empty list.", e);
         return new ArrayList<ServerInfo>();
      }
   }

   private List<ServerInfo> getServerList() throws ConfigurationException
   {
      HierarchicalINIConfiguration config = new HierarchicalINIConfiguration(userConfig);
      SubnodeConfiguration serverConfig = config.getSection("servers");
      DataConfiguration dataConfig = new DataConfiguration(serverConfig);
      List<String> prefixes = findPrefixes(dataConfig);
      return findServerInfoByPrefixes(dataConfig, prefixes);
   }

   private List<String> findPrefixes(DataConfiguration serverConfig)
   {
      List<String> prefixes = new ArrayList<String>();
      Iterator<String> iter = serverConfig.getKeys();
      while (iter.hasNext())
      {
         String key = iter.next();
         if (key.endsWith(URL_KEY_SUFFIX))
         {
            String prefix = key.substring(0, key.length() - URL_KEY_SUFFIX.length());
            if (!prefix.isEmpty())
            {
               prefixes.add(prefix);
            }
         }
      }
      return prefixes;
   }

   private List<ServerInfo> findServerInfoByPrefixes(
                                                     DataConfiguration serverConfig,
                                                     List<String> prefixes)
   {
      List<ServerInfo> servers = new ArrayList<ServerInfo>();
      for (String prefix : prefixes)
      {
         String urlKey = prefix + URL_KEY_SUFFIX;
         String userNameKey = prefix + USER_NAME_SUFFIX;
         String apiKeyKey = prefix + API_KEY_SUFFIX;
         ServerInfo server =
               new ServerInfo(prefix, serverConfig.getURL(urlKey, null),
                              serverConfig.getString(userNameKey, null),
                              serverConfig.getString(apiKeyKey, null));
         servers.add(server);
      }
      return servers;
   }

}
