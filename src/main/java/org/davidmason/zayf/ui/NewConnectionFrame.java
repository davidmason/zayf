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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

@SuppressWarnings(value = "serial")
public class NewConnectionFrame extends JDialog
{

   private static final String settingsFile = "cfg/zayf.cfg";

   private JButton okButton, cancelButton;
   private JTextField urlField, userNameField;
   private JTextField passwordField;

   private String url, userName, apiKey;
   private boolean connectButtonPressed = false;

   /**
    * Creates and displays a modal dialog for setting up a database connection.
    * Loads settings from config file if possible.
    */
   NewConnectionFrame()
   {
      setTitle("Connect to server");
      setLayout(null);
      setBounds(0, 0, 320, 190);
      setLocationRelativeTo(null); //centre screen
      this.setResizable(false);
      this.setModal(true);

      okButton = new JButton("Connect");
      okButton.addActionListener(new ActionListener()
      {

         public void actionPerformed(ActionEvent e)
         {
            connect();
         }
      });

      cancelButton = new JButton("Cancel");
      cancelButton.addActionListener(new ActionListener()
      {

         public void actionPerformed(ActionEvent e)
         {
            cancel();
         }
      });

      urlField = new JTextField("");
      userNameField = new JTextField("");
      passwordField = new JPasswordField(""); //TODO: not a password field?

      JLabel urlLabel = new JLabel("URL:", SwingConstants.RIGHT);
      JLabel userNameLabel = new JLabel("Username:", SwingConstants.RIGHT);
      JLabel passwordLabel = new JLabel("API Key:", SwingConstants.RIGHT);

      add(urlLabel);
      urlLabel.setBounds(5, 25, 80, 20);
      add(urlField);
      urlField.setBounds(95, 25, 200, 20);

      add(userNameLabel);
      userNameLabel.setBounds(5, 50, 80, 20);
      add(userNameField);
      userNameField.setBounds(95, 50, 200, 20);

      add(passwordLabel);
      passwordLabel.setBounds(5, 75, 80, 20);
      add(passwordField);
      passwordField.setBounds(95, 75, 200, 20);

      add(okButton);
      okButton.setBounds(95, 100, 110, 30);
      add(cancelButton);
      cancelButton.setBounds(215, 100, 79, 30);

      try
      {
         File file = new File(settingsFile);
         FileReader fr = new FileReader(file);
         BufferedReader reader = new BufferedReader(fr);
         urlField.setText(reader.readLine());
         userNameField.setText(reader.readLine());
         passwordField.setText(reader.readLine());
         reader.close();
      }
      catch (Exception e)
      {
         urlField.setText("");
         userNameField.setText("");
         passwordField.setText("");
      }

      setVisible(true);
   }

   /** @return url typed in url field */
   public String getUrl()
   {
      return url;
   }

   /** @return username typed in username field */
   public String getUserName()
   {
      return userName;
   }

   /** @return password typed in password field */
   public String getApiKey()
   {
      return apiKey;
   }

   /** @return true if frame was closed via connect button */
   public boolean connectPressed()
   {
      return connectButtonPressed;
   }

   /** sets attributes for connection, saves to config file, and closes the frame */
   private void connect()
   {
      url = urlField.getText();
      userName = userNameField.getText();
      apiKey = passwordField.getText();

      connectButtonPressed = true;

      try
      {
         File file = new File(settingsFile);
         FileWriter fw = new FileWriter(file);
         fw.write(urlField.getText());
         fw.write("\n");
         fw.write(userNameField.getText());
         fw.write("\n");
         fw.write(passwordField.getText());
         fw.close();
      }
      catch (Exception e)
      {}

      dispose();
   }

   /** sets attribute for no connection and closes the frame */
   private void cancel()
   {
      connectButtonPressed = false;
      dispose();
   }

}
