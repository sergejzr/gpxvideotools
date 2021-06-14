package de.zerr.gui;

import java.time.ZonedDateTime;

/*
 * @(#) ISpeedView.java
 *
 * This code is part of the JAviator project: javiator.cs.uni-salzburg.at
 * Copyright (c) 2009  Clemens Krainer
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

import java.util.Date;

import de.zerr.ContinuousRoute;

/**
 * This interface provides functionality necessary to update a speed view.
 * 
 * @author Clemens Krainer
 */
public interface ICockpit {
	public void setRoute(ContinuousRoute route, ZonedDateTime starttime, ZonedDateTime endtime);

	public void at(ZonedDateTime time);
}
