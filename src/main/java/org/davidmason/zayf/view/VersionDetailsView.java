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

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.zanata.rest.dto.ProjectIteration;

public class VersionDetailsView extends JPanel
{

   private static final long serialVersionUID = 1L;

   private static final String NO_VERSION_MESSAGE = "No version selected.";
   private static final String FAKE_STATS_MESSAGE =
         "<html>Documents: XX<br/>" +
               "Text Flows: YYY (ZZZ untranslated)<br/>" +
               "Words: AAAA (BBBB untranslated)<br/><br/>" +
               "This is a placeholder, real implementation of stats details will come later.<br/>" +
               "You should be able to view the documents with a button below.</html>";

   private JPanel noVersionPanel, versionPanel, buttonPanel;
   private JLabel noVersionLabel, idLabel, fakeStatsLabel;

   private boolean showingVersion;

   public VersionDetailsView()
   {
      buildGui();
   }

   private void buildGui()
   {
      setLayout(new BorderLayout());

      noVersionPanel = new JPanel();
      noVersionLabel = new JLabel(NO_VERSION_MESSAGE);
      noVersionPanel.add(noVersionLabel);

      versionPanel = new JPanel();
      versionPanel.setLayout(new BorderLayout());

      idLabel = new JLabel("");
      fakeStatsLabel = new JLabel(FAKE_STATS_MESSAGE);
      buttonPanel = new JPanel();
      buttonPanel.add(new JButton("test"));
      buttonPanel.add(new JButton("test"));
      buttonPanel.add(new JButton("test"));
      buttonPanel.add(new JButton("test"));

      versionPanel.add(idLabel, BorderLayout.PAGE_START);
      versionPanel.add(fakeStatsLabel, BorderLayout.CENTER);
      versionPanel.add(buttonPanel, BorderLayout.PAGE_END);

      add(noVersionPanel, BorderLayout.CENTER);
      showingVersion = false;
   }

   public void displayVersion(ProjectIteration version)
   {
      if (version == null)
      {
         if (showingVersion)
         {
            remove(versionPanel);
            add(noVersionPanel, BorderLayout.CENTER);
            showingVersion = false;
            // FIXME looks like parent container needs to be refreshed here.
            // resize makes it display properly
            validate();
         }
         return;
      }

      // update stats, if there were any
      // buttons should generally not need to change, controller should handle that.
      idLabel.setText("Version ID: " + version.getId());

      if (!showingVersion)
      {
         remove(noVersionPanel);
         add(versionPanel, BorderLayout.CENTER);
         showingVersion = true;
         validate();
      }
   }
}
