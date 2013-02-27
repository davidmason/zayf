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

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.PopupMenu;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.io.File;

public class ZayfTrayIcon extends TrayIcon
{

   private static final String ZAYF_TRAY_TOOLTIP = "Zayf - Zanata at your Fingertips";
   private static Image zayfIcon = null;
   private static PopupMenu menu;
   private static MenuItem exitItem;

   public ZayfTrayIcon()
   {
      super(getIcon(), getTooltip(), getMenu());
      setImageAutoSize(true);
   }

   private static PopupMenu getMenu()
   {
      menu = new PopupMenu(ZAYF_TRAY_TOOLTIP);
      exitItem = new MenuItem("exit", new MenuShortcut('x'));

      menu.add(exitItem);
      return menu;
   }

   private static String getTooltip()
   {
      return ZAYF_TRAY_TOOLTIP;
   }

   private static Image getIcon()
   {
      String sep = File.separator;
      String executionDir = System.getProperty("user.dir");
      // FIXME find a solution that will work consistently in development and production
      String imagePath =
            executionDir + sep + "src" + sep + "main" + sep + "resources" + sep + "zayf-tray.png";
      zayfIcon = Toolkit.getDefaultToolkit().createImage(imagePath);
      return zayfIcon;
   }

   public void addIconActionListener(ActionListener listener)
   {
      addActionListener(listener);
   }

   public void addExitListener(ActionListener listener)
   {
      exitItem.addActionListener(listener);
   }
}
