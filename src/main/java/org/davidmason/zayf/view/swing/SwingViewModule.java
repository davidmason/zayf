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
package org.davidmason.zayf.view.swing;

import java.awt.Component;

import org.davidmason.zayf.view.DocumentsView;
import org.davidmason.zayf.view.MainWindow;
import org.davidmason.zayf.view.ProjectDetailsView;
import org.davidmason.zayf.view.ProjectTreeView;
import org.davidmason.zayf.view.ServerSelectView;
import org.davidmason.zayf.view.VersionDetailsView;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

/**
 * Guice bindings for the default Swing GUI.
 * 
 * @author David Mason, dr.d.mason@gmail.com
 * 
 */
public class SwingViewModule extends AbstractModule
{

   @Override
   protected void configure()
   {
      bind(MainWindow.class).asEagerSingleton();

      //@formatter:off
      bindSwingView(DocumentsView.class, SwingDocumentsView.class,
                    new TypeLiteral<DocumentsView<?>>() {},
                    new TypeLiteral<DocumentsView<Component>>() {});

      bindSwingView(ProjectDetailsView.class, SwingProjectDetailsView.class,
                    new TypeLiteral<ProjectDetailsView<?>>() {},
                    new TypeLiteral<ProjectDetailsView<Component>>() {});

      bindSwingView(ProjectTreeView.class, SwingProjectTreeView.class,
                    new TypeLiteral<ProjectTreeView<?>>() {},
                    new TypeLiteral<ProjectTreeView<Component>>() {});

      bindSwingView(ServerSelectView.class, SwingServerSelectView.class,
                    new TypeLiteral<ServerSelectView<?>>() {},
                    new TypeLiteral<ServerSelectView<Component>>() {});

      bindSwingView(VersionDetailsView.class, SwingVersionDetailsView.class,
                    new TypeLiteral<VersionDetailsView<?>>() {},
                    new TypeLiteral<VersionDetailsView<Component>>() {});
      //@formatter:on
   }

   /**
    * Chain a few permutations of the generic interface to all use the same instance of the
    * implementation.
    * 
    * @param rawInterface
    *           the top level interface with no type argument
    * @param implementation
    *           the implementation to use (must implement type in specific)
    * @param generic
    *           rawInterface with generic type argument (&lt;?>)
    * @param specific
    *           rawInterface with a specific type argument (e.g. &lt;Composite>)
    */
   private <T, U extends T, V extends U, W extends V> void bindSwingView(Class<T> rawInterface,
                                                                         Class<W> implementation,
                                                                         TypeLiteral<U> generic,
                                                                         TypeLiteral<V> specific)
   {
      bind(rawInterface).to(generic);
      bind(generic).to(specific);
      bind(specific).to(implementation).asEagerSingleton();
   }
}
