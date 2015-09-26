package org.pascani.deployment.amelia.process;

import static net.sf.expectit.filter.Filters.removeColors;
import static net.sf.expectit.filter.Filters.removeNonPrintable;

import java.io.IOException;

import net.sf.expectit.Expect;
import net.sf.expectit.ExpectBuilder;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SessionHandler {

	private final int timeout;

	private final String host;

	private final int port;

	private final String user;

	private final String password;

	private Session session;

	private Channel channel;

	private Expect expect;

	public SessionHandler(final String host, final int port, final String user,
			final String password) {
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;

		// TODO: get from .properties
		this.timeout = 10000;
	}

	public Expect run() {
		try {

			connect();
			initialize();

		} catch (JSchException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return this.expect;
	}

	private void connect() throws JSchException {
		JSch jsch = new JSch();

		// TODO: get from .properties
		String home = System.getProperty("user.home");

		jsch.addIdentity(home + "/.ssh/id_rsa");
		jsch.setKnownHosts(home + "/.ssh/known_hosts");

		this.session = jsch.getSession(this.user, this.host, this.port);
		this.session.setPassword(this.password);
		this.session.connect(this.timeout);

		this.channel = session.openChannel("shell");
		this.channel.setInputStream(System.in);
		this.channel.setOutputStream(System.out);
		this.channel.connect(this.timeout);
	}

	private void initialize() throws IOException {
		this.expect = new ExpectBuilder()
				.withOutput(this.channel.getOutputStream())
				.withInputs(this.channel.getInputStream(),
						this.channel.getExtInputStream())
				.withEchoInput(System.out)
//				.withEchoOutput(System.out)
				.withInputFilters(removeColors(), removeNonPrintable())
				.withExceptionOnFailure().build();
	}

	public void close() throws IOException {
		expect.close();
		channel.disconnect();
		session.disconnect();
	}

}