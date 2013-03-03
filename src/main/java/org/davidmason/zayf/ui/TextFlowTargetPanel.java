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
import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.zanata.rest.dto.resource.TextFlowTarget;
import org.zanata.common.ContentState;
import org.zanata.common.LocaleId;

public class TextFlowTargetPanel extends JPanel
{

   private TextFlowTarget textFlowTarget;
   private JLabel idLabel, revisionLabel, stateLabel;
   //TODO: show resource revision, description, person ?
   private JTextArea textFlowTargetPane;
   private LocaleId targetLocale;

   public TextFlowTargetPanel(LocaleId targetLocale)
   {
      textFlowTarget = null;
      this.targetLocale = targetLocale;
      setLayout(new BorderLayout());
      idLabel = new JLabel("ID:");
      revisionLabel = new JLabel("Revision:");
      stateLabel = new JLabel("Status:");
      textFlowTargetPane = new JTextArea();
      textFlowTargetPane.setEditable(false);

      JPanel labelsPanel = new JPanel(null);
      labelsPanel.setPreferredSize(new Dimension(100, 70));

      JLabel textFlowLabel = new JLabel("Text Flow Target");
      textFlowLabel.setFont(new Font("System", Font.BOLD, 12));
      labelsPanel.add(textFlowLabel);
      textFlowLabel.setBounds(new Rectangle(0, -7, 100, 30));

      Font attributesFont = new Font("Arial", Font.PLAIN, 10);

      labelsPanel.add(idLabel);
      idLabel.setBounds(new Rectangle(2, 0 + 17, 100, 16));
      idLabel.setFont(attributesFont);

      labelsPanel.add(revisionLabel);
      revisionLabel.setBounds(2, 16 + 17, 100, 16);
      revisionLabel.setFont(attributesFont);

      labelsPanel.add(stateLabel);
      stateLabel.setBounds(2, 32 + 17, 120, 16);
      stateLabel.setFont(attributesFont);

      add(labelsPanel, BorderLayout.NORTH);
      add(textFlowTargetPane, BorderLayout.CENTER);
   }

   public void update(TextFlowTarget textFlowTarget)
   {
      this.textFlowTarget = textFlowTarget;

      idLabel.setText("ID: " + textFlowTarget.getResId());

      int revision = textFlowTarget.getRevision() == null ? 0 : textFlowTarget.getRevision();
      revisionLabel.setText("Revision: " + revision);

      String status =
            (textFlowTarget.getState() == ContentState.NeedReview) ? "Needs Review"
                                                                  : textFlowTarget.getState()
                                                                                  .toString();
      stateLabel.setText("Status: " + status);

      textFlowTargetPane.setText(textFlowTarget.getContent());
      textFlowTargetPane.setForeground(Color.black);

      textFlowTargetPane.setText(textFlowTarget.getContent());

      if (textFlowTarget.getContent().isEmpty())
      {
         textFlowTargetPane.setForeground(Color.gray);
         textFlowTargetPane.setText("Empty");
      }

      if (textFlowTarget.getState() == ContentState.New)
      {
         textFlowTargetPane.setForeground(new Color(0, 120, 0));
         textFlowTargetPane.setText("New");
      }
      else if (textFlowTarget.getState() == ContentState.Approved)
         textFlowTargetPane.setForeground(new Color(0, 0, 120));
      else if (textFlowTarget.getState() == ContentState.NeedReview)
         textFlowTargetPane.setForeground(new Color(120, 0, 0));
   }

   public void clear()
   {
      textFlowTargetPane.setForeground(Color.gray);
      textFlowTargetPane.setText("Select a text flow or text flow target in tree view\nOnly text flow targets for the \""
                                 + targetLocale.getId() + "\" locale are displayed");
   }

   public void notFoundError()
   {
      textFlowTargetPane.setForeground(Color.gray);
      textFlowTargetPane.setText("Text Flow Target not found");
   }
}
