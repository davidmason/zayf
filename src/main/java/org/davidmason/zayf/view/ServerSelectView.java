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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import org.davidmason.zayf.controller.ServerSelectController;
import org.davidmason.zayf.model.ServerInfo;

/**
 * View for selection of a Zanata server
 * 
 * @author David Mason, dr.d.mason@gmail.com
 * 
 */
public class ServerSelectView extends JPanel
{

   private static final String FINDING_SERVERS_IN_CONFIG_FILE = "Finding servers in config file...";

   private static final long serialVersionUID = 1L;

   private ServerSelectController controller;

   private JLabel statusLabel;
   private JComboBox serverCombo;
   private JButton loadProjectsButton;

   public ServerSelectView(ServerSelectController serverController)
   {
      this.controller = serverController;
      buildGui();
      // TODO make a button trigger this?
      loadServersIntoDropdown();
   }

   private void buildGui()
   {
      setLayout(new BorderLayout());

      statusLabel = new JLabel(FINDING_SERVERS_IN_CONFIG_FILE);
      createServerCombo();
      createLoadProjectsButton();

      add(statusLabel, BorderLayout.NORTH);
      add(serverCombo, BorderLayout.CENTER);
      add(loadProjectsButton, BorderLayout.EAST);

      // TODO ability to select different config files, and change default config

      //      setVisible(true);
   }

   private void createServerCombo()
   {
      serverCombo = new JComboBox();
      serverCombo.setRenderer(new DefaultListCellRenderer()
      {

         private static final long serialVersionUID = 1L;

         @Override
         public Component getListCellRendererComponent(JList list,
                                                       Object value, int index, boolean isSelected,
                                                       boolean cellHasFocus)
         {
            ServerInfo serverInfo = (ServerInfo) value;
            value = serverInfo.getUserName() + " | " + serverInfo.getServerUrl();
            return super.getListCellRendererComponent(list, value, index, isSelected,
                                                      cellHasFocus);
         }
      });
   }

   private void createLoadProjectsButton()
   {
      loadProjectsButton = new JButton();
      loadProjectsButton.setText("view projects");
      loadProjectsButton.addActionListener(new ActionListener()
      {

         public void actionPerformed(ActionEvent e)
         {
            ServerInfo info = (ServerInfo) serverCombo.getItemAt(serverCombo.getSelectedIndex());
            controller.buttonPressViewProjects(info);
         }
      });
   }

   private void loadServersIntoDropdown()
   {
      for (ServerInfo info : controller.getServerInfo())
      {
         serverCombo.addItem(info);
      }
      statusLabel.setText("Select a server");
   }

}
