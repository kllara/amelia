package org.pascani.deployment.amelia.descriptors;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Host {

	private final String identifier;

	private final String hostname;

	private final int ftpPort;

	private final int sshPort;

	private final String username;

	private final String password;

	/**
	 * The logger
	 */
	private final static Logger logger = LogManager.getLogger(Host.class);

	public Host(final String hostname,
			final int ftpPort, final int sshPort, final String username,
			final String password, final String identifier) {
		this.identifier = identifier;
		this.hostname = hostname;
		this.ftpPort = ftpPort;
		this.sshPort = sshPort;
		this.username = username;
		this.password = password;
	}

	public Host(final String hostname, final int ftpPort, final int sshPort,
			final String username, final String password) {
		this.identifier = UUID.randomUUID().toString();
		this.hostname = hostname;
		this.ftpPort = ftpPort;
		this.sshPort = sshPort;
		this.username = username;
		this.password = password;
	}

	public static Host[] fromFile(String pathname) throws IOException {
		List<Host> hosts = new ArrayList<Host>();

		InputStream in = new FileInputStream(pathname);
		InputStreamReader streamReader = null;
		BufferedReader bufferedReader = null;

		try {
			streamReader = new InputStreamReader(in);
			bufferedReader = new BufferedReader(streamReader);

			String line;
			int l = 1;
			while ((line = bufferedReader.readLine()) != null) {

				String[] d = line.split("\t");

				if (d.length == 5) {
					int ftpPort = Integer.parseInt(d[1]);
					int sshPort = Integer.parseInt(d[2]);
					hosts.add(new Host(d[0], ftpPort, sshPort, d[3], d[4]));

				} else if (d.length == 6) {
					int ftpPort = Integer.parseInt(d[1]);
					int sshPort = Integer.parseInt(d[2]);
					hosts.add(new Host(d[0], ftpPort, sshPort, d[3], d[4], d[5]));

				} else {
					String message = "Bad format in hosts file: [" + l + "] " + line;
					RuntimeException e = new RuntimeException(message);
					logger.error(message, e);
					
					throw e;
				}

				++l;
			}
		} finally {
			in.close();
			streamReader.close();
			bufferedReader.close();
		}

		return hosts.toArray(new Host[0]);
	}
	
	public String identifier() {
		return this.identifier;
	}

	public String hostname() {
		return this.hostname;
	}

	public int ftpPort() {
		return this.ftpPort;
	}

	public int sshPort() {
		return this.sshPort;
	}

	public String username() {
		return this.username;
	}

	public String password() {
		return this.password;
	}

	@Override
	public String toString() {
		return this.username + "@" + this.hostname;
	}

}