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

/**
 * Superinterface for views that are implemented using underlying widgets. Provides an
 * {@link #asWidget()} method to retrieve a strongly typed widget without the need for casting or
 * reflection.
 * 
 * @author David Mason, dr.d.mason@gmail.com
 * 
 * @param <WidgetType>
 *           the underlying type of this widget
 */
public interface WidgetView<WidgetType>
{

   /**
    * @return this view as a widget for use with the underlying framework.
    */
   WidgetType asWidget();

}
