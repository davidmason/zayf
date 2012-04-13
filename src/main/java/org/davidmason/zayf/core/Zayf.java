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
package org.davidmason.zayf.core;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import javax.swing.JFrame;

import org.davidmason.zayf.ui.ZayfView;

/**
 * Zayf entry point
 */
public class Zayf
{

   private static final String ZAYF_VERSION = "0.00001";

   public static void main(String[] args) throws MalformedURLException, URISyntaxException
   {
      ZayfView syncView = new ZayfView();
      syncView.setTitle("Zayf v" + ZAYF_VERSION);
      syncView.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      //syncView.setUrl("http://www.HeyBuddy.com");
      syncView.setVisible(true);

   }
}
