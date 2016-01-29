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
package org.amelia.dsl.lib.commands;

import static net.sf.expectit.matcher.Matchers.regexp;
import net.sf.expectit.Expect;

import org.amelia.dsl.lib.descriptors.Compilation;
import org.amelia.dsl.lib.descriptors.Host;
import org.amelia.dsl.lib.util.Log;
import org.amelia.dsl.lib.util.ShellUtils;
import org.amelia.dsl.lib.util.Strings;

/**
 * @author Miguel Jiménez - Initial contribution and API
 */
public class Compile extends Command<Boolean> {

	public Compile(final Host host, final Compilation descriptor) {
		super(host, descriptor);
	}

	public Boolean call() throws Exception {

		Host host = super.host;
		Compilation descriptor = (Compilation) super.descriptor;
		Expect expect = host.ssh().expect();
		String prompt = ShellUtils.ameliaPromptRegexp();

		// Perform the compilation
		expect.sendLine(descriptor.toCommandString());
		String compile = expect.expect(regexp(prompt)).getBefore();

		String[] _404 = { "No existe el fichero o el directorio",
				"No such file or directory" };
		String[] _denied = { "Permission denied", "Permiso denegado" };

		if (Strings.containsAnyOf(compile, _404)) {
			String message = "No such file or directory '"
					+ descriptor.sourceDirectory() + "' in host " + host;

			Log.error(host, message);
			throw new Exception(message);
		} else if (Strings.containsAnyOf(compile, _denied)) {
			String message = "Permission denied to access '"
					+ descriptor.sourceDirectory() + "' in host " + host;

			Log.error(host, message);
			throw new Exception(message);
		} else {
			Log.ok(host, descriptor.doneMessage());
		}

		return true;
	}

}