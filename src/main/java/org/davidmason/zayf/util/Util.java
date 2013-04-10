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
package org.davidmason.zayf.util;

/**
 * Utility methods for the main Zayf application.
 * 
 * @author David Mason, dr.d.mason@gmail.com
 * 
 */
public class Util
{

   /**
    * 
    * @param path
    * @return everything after the final '/' in path, or the entire path if path contains no '/'.
    */
   public static String getEndOfPath(String path)
   {
      int finalSlash = path.lastIndexOf("/");
      if (finalSlash == -1)
      {
         return path;
      }
      if (finalSlash != path.length() - 1)
      {
         return path.substring(finalSlash + 1);
      }
      throw new RuntimeException("Slash on end of path: " + path);
   }

   /**
    * 
    * @param path
    * @return path with the final '/' and everything following it omitted, or an empty string if
    *         the string contains no '/'.
    */
   public static String getBeginningOfPath(String path)
   {
      int finalSlash = path.lastIndexOf('/');
      if (finalSlash == -1)
      {
         return "";
      }
      if (finalSlash != path.length() - 1 && finalSlash != 0)
      {
         return path.substring(0, finalSlash);
      }
      throw new RuntimeException("Slash on start or end of path: " + path);
   }
}
