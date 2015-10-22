/*
 * Copyright © 2015 Universidad Icesi
 * 
 * This file is part of the Amelia library.
 * 
 * The Amelia library is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
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
package org.pascani.deployment.amelia.commands;

import static net.sf.expectit.matcher.Matchers.regexp;

import java.util.concurrent.Callable;

import net.sf.expectit.Expect;
import net.sf.expectit.Result;

import org.pascani.deployment.amelia.DeploymentException;
import org.pascani.deployment.amelia.descriptors.Compilation;
import org.pascani.deployment.amelia.descriptors.Host;
import org.pascani.deployment.amelia.util.Log;
import org.pascani.deployment.amelia.util.ShellUtils;
import org.pascani.deployment.amelia.util.Strings;

/**
 * @author Miguel Jiménez - Initial contribution and API
 */
public class Compile extends Command<Boolean> {

	public Compile(final Host host, final Compilation descriptor) {
		super(host, descriptor);
	}

	public Boolean call() throws Exception {

		Compilation descriptor = (Compilation) super.descriptor;
		Expect expect = this.host.ssh().expect();
		String prompt = ShellUtils.ameliaPromptRegexp();

		// Perform the compilation
		expect.sendLine(descriptor.toCommandString());
		String compile = expect.expect(regexp(prompt)).getBefore();

		String[] _404 = { "No existe el fichero o el directorio",
				"No such file or directory" };
		String[] _denied = { "Permission denied", "Permiso denegado" };

		if (Strings.containsAnyOf(compile, _404)) {
			String message = "No such file or directory '"
					+ descriptor.sourceDirectory() + "'";

			Log.error(super.host, message);
			throw new DeploymentException(message);
		} else if (Strings.containsAnyOf(compile, _denied)) {
			String message = "Permission denied to access '"
					+ descriptor.sourceDirectory() + "'";

			Log.error(super.host, message);
			throw new DeploymentException(message);
		}

		return true;
	}

	/**
	 * As there is not such "decompile" command, the rollback functionality
	 * consist of removing the compiled file. Also, the shell should be in the
	 * same working directory.
	 */
	@Override
	public Callable<Void> rollback() throws Exception {

		final Host host = super.host;
		final Expect expect = host.ssh().expect();
		final Compilation descriptor = (Compilation) super.descriptor;
		final String prompt = ShellUtils.ameliaPromptRegexp();

		return new Callable<Void>() {
			public Void call() throws Exception {

				String jarFile = descriptor.outputFile() + ".jar";

				expect.sendLine("rm " + jarFile);
				Result rm = expect.expect(regexp(prompt));

				String[] errors = { "Not such file or directory",
						"Permission denied",
						"No existe el fichero o el directorio",
						"Permiso denegado" };

				if (!Strings.containsAnyOf(rm.getBefore(), errors))
					Log.info(host, "File removed: " + jarFile);
				else
					Log.warning(host, "Could not remove file " + jarFile);

				return null;
			}
		};
	}

}
