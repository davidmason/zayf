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

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import org.zanata.rest.dto.resource.TextFlow;

public class TextFlowPanel extends JPanel
{

   private TextFlow textFlow;
   private JLabel idLabel, revisionLabel, languageLabel;
   private JTextArea textFlowPane;

   public TextFlowPanel()
   {
      textFlow = null;
      setLayout(new BorderLayout());
      idLabel = new JLabel("ID:");
      revisionLabel = new JLabel("Revision:");
      languageLabel = new JLabel("Locale:");
      textFlowPane = new JTextArea();
      textFlowPane.setEditable(false);

      JPanel labelsPanel = new JPanel(null);
      labelsPanel.setPreferredSize(new Dimension(100, 70));

      JLabel textFlowLabel = new JLabel("Text Flow");
      textFlowLabel.setFont(new Font("System", Font.BOLD, 12));
      labelsPanel.add(textFlowLabel);
      textFlowLabel.setBounds(new Rectangle(0, -7, 100, 30));

      Font attributesFont = new Font("Arial", Font.PLAIN, 10);

      labelsPanel.add(idLabel);
      idLabel.setBounds(new Rectangle(2, 0 + 17, 100, 16));
      idLabel.setFont(attributesFont);

      labelsPanel.add(languageLabel);
      languageLabel.setBounds(2, 16 + 17, 100, 16);
      languageLabel.setFont(attributesFont);

      labelsPanel.add(revisionLabel);
      revisionLabel.setBounds(2, 32 + 17, 100, 16);
      revisionLabel.setFont(attributesFont);

      add(labelsPanel, BorderLayout.NORTH);
      add(textFlowPane, BorderLayout.CENTER);
   }

   public void update(TextFlow textFlow)
   {
      this.textFlow = textFlow;

      idLabel.setText("ID: " + textFlow.getId());

      int revision = textFlow.getRevision() == null ? 0 : textFlow.getRevision();
      revisionLabel.setText("Revision: " + revision);
      languageLabel.setText("Locale: " + textFlow.getLang().toString());

      textFlowPane.setText(textFlow.getContent());
      textFlowPane.setForeground(Color.black);
   }

   public void clear()
   {
      idLabel.setText("ID:");
      revisionLabel.setText("Revision");
      languageLabel.setText("Language:");
      textFlowPane.setForeground(Color.gray);
      textFlowPane.setText("Select a text flow or text flow target in tree view");
   }

   public void notFoundError()
   {
      textFlowPane.setForeground(Color.red);
      textFlowPane.setText("Corresponding Text Flow not found");
   }
}
