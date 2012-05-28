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
package org.davidmason.zayf.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

@SuppressWarnings(value = "serial")
public class StatusBar extends JPanel
{

   private JLabel connectionLabel;

   StatusBar()
   {
      setBorder(new EtchedBorder(EtchedBorder.RAISED));
      setPreferredSize(new Dimension(getWidth(), 20));
      setLayout(new BorderLayout());

      connectionLabel = new JLabel("No connection");
      connectionLabel.setFont(new Font("Verdana", Font.PLAIN, 10));

      JPanel connectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
      connectionPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
      connectionPanel.setPreferredSize(new Dimension(120, 18));
      connectionPanel.add(connectionLabel);

      add(connectionPanel, BorderLayout.WEST);
   }

   /**
    * update the connection status label
    * 
    * @param string
    *           to display in connection panel
    * @param color
    *           text colour
    */
   public void setConnection(String connectionStatus, Color color)
   {
      connectionLabel.setText(connectionStatus);
      connectionLabel.setForeground(color);
   }
}
