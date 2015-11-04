/*
 * Copyright © 2015 Universidad Icesi
 * 
 * This file is part of the Amelia library.
 * 
 * The Amelia library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Amelia library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the Amelia library. If not, see <http://www.gnu.org/licenses/>.
 */
package org.pascani.deployment.amelia;

import java.util.Map;

import org.pascani.deployment.amelia.util.Log;

/**
 * @author Miguel Jiménez - Initial contribution and API
 */
public class Subsystem {

	public abstract static class Deployment extends OpenObservable {
		public abstract void deploy(Map<String, Subsystem> dependencies)
				throws Exception;
	}

	private final String alias;

	private final Deployment deployment;

	public Subsystem(final String alias, final Deployment deployment) {
		this.alias = alias;
		this.deployment = deployment;
	}

	public void start() {
		Log.subheading("\n  Deploying subsystem " + alias + "\n");
	}

	public void error() {
		Log.error(null, "Error deploying subsystem " + alias);
	}

	public void done() {
		Log.info(null, "Subsystem " + alias + " deployed successfully");
	}

	@Override
	public String toString() {
		return this.alias;
	}

	public Deployment deployment() {
		return this.deployment;
	}

	public String alias() {
		return this.alias;
	}

}
