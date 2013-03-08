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
package org.davidmason.zayf.view.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.davidmason.zayf.view.VersionDetailsView;
import org.zanata.rest.dto.ProjectIteration;

/**
 * Default Swing implementation of {@link VersionDetailsView}
 * 
 * @author David Mason, dr.d.mason@gmail.com
 *
 */
class SwingVersionDetailsView extends JPanel implements VersionDetailsView<Component>
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
   private JButton docsButton;

   private boolean showingVersion;

   public SwingVersionDetailsView()
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
      docsButton = new JButton("Documents");
      docsButton.setActionCommand("show-documents");
      buttonPanel.add(docsButton);
      JButton trackProjectButton = new JButton("Track Project");
      trackProjectButton.setToolTipText("<html>Download a copy of this project, and generate a workspace with all the documents.<br/>"
                                        +
                                        "Will periodically synchronize changes between the server and workspace.</html>");
      trackProjectButton.setEnabled(false);
      buttonPanel.add(trackProjectButton);
      JButton checkServerButton = new JButton("Check Server");
      checkServerButton.setEnabled(false);
      checkServerButton.setToolTipText("Check for recent changes on the server, update workspace with changes.");
      buttonPanel.add(checkServerButton);
      JButton checkWorkspaceButton = new JButton("Check Workspace");
      checkWorkspaceButton.setToolTipText("Check for any changes in the workspace, push changes to the server.");
      checkWorkspaceButton.setEnabled(false);
      buttonPanel.add(checkWorkspaceButton);

      versionPanel.add(idLabel, BorderLayout.PAGE_START);
      versionPanel.add(fakeStatsLabel, BorderLayout.CENTER);
      versionPanel.add(buttonPanel, BorderLayout.PAGE_END);

      add(noVersionPanel, BorderLayout.CENTER);
      showingVersion = false;
   }

   @Override
   public void displayVersion(ProjectIteration version)
   {
      if (version == null)
      {
         if (showingVersion)
         {
            remove(versionPanel);
            add(noVersionPanel, BorderLayout.CENTER);
            showingVersion = false;
            revalidate();
            repaint();
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
      }
      revalidate();
      repaint();
   }

   @Override
   public void setShowDocsListener(ActionListener listener)
   {
      docsButton.addActionListener(listener);
   }

   @Override
   public Component asWidget()
   {
      return this;
   }
}
