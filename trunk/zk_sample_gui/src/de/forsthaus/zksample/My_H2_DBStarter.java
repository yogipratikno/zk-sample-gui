package de.forsthaus.zksample;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.h2.tools.Server;
import org.h2.util.StringUtils;

/**
 * This class can be used to start the H2 TCP server (or other H2 servers, for
 * example the PG server) inside a web application container such as Tomcat or
 * Jetty. It can also open a database connection.
 * 
 * Reading a sql-textfile that holds the ddl script for creating the <br>
 * tables and the sample data records. <br>
 * This class is called automatically by starting the project under tomcat. <br>
 * Please have a look at the web.xml. <br>
 * <br>
 * < listener > <br>
 * < listener-class >de.forsthaus.zksample.My_H2_DbStarter< /listener-class > <br>
 * < /listener > <br>
 * 
 * 
 * @changes Stephan Gerth / sge(at)forsthaus(de)
 */
public class My_H2_DBStarter implements ServletContextListener {

	private Connection conn;
	private Server server;

	public void contextInitialized(ServletContextEvent servletContextEvent) {
		try {
			org.h2.Driver.load();

			// This will get the setting from a context-param in web.xml if
			// defined:
			ServletContext servletContext = servletContextEvent.getServletContext();
			String url = getParameter(servletContext, "db.url", "jdbc:h2:~/test");
			String user = getParameter(servletContext, "db.user", "sa");
			String password = getParameter(servletContext, "db.password", "sa");

			conn = DriverManager.getConnection(url, user, password);
			servletContext.setAttribute("connection", conn);

			// Start the server if configured to do so
			String serverParams = getParameter(servletContext, "db.tcpServer", null);
			if (serverParams != null) {
				String[] params = StringUtils.arraySplit(serverParams, ' ', true);

				try {
					server = Server.createTcpServer(params);
					server.start();
					System.out.println("H2 DB Server started");

					connectAndCreateDemoData();

				} catch (Exception e) {
					// TODO: handle exception
					System.out.println("H2 DB Server already started");
				}

			}
			// To access the database in server mode, use the database URL:
			// jdbc:h2:tcp://localhost/~/test

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void connectAndCreateDemoData() {

		try {
			// reads the sql-file from the classpath
			InputStream inputStream = getClass().getResourceAsStream("/de/forsthaus/sampledata/createSampleData.sql");

			Connection conn = getConnection();
			Statement stat = conn.createStatement();

			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			String str = "";
			StringBuilder sb = new StringBuilder();
			while (str != null) {
				sb.append(str);
				// make a linefeed at each readed line
				sb.append("\n");
				str = in.readLine();
			}

			stat.addBatch(sb.toString());

			int[] ar = stat.executeBatch();
			int i = 0;
			i = ar.length;

			System.out.println("Create DemoData");
			System.out.println("count batch updates : " + i);

		} catch (Exception e) {
			System.out.println(e + " / " + e.getMessage());
			throw new RuntimeException(e);
		}
	}

	private String getParameter(ServletContext servletContext, String key, String defaultValue) {
		String value = servletContext.getInitParameter(key);
		return value == null ? defaultValue : value;
	}

	/**
	 * Get the connection.
	 * 
	 * @return the connection
	 */
	public Connection getConnection() {
		return conn;
	}

	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		try {
			conn.createStatement().execute("SHUTDOWN");
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (server != null) {
			server.stop();
			server = null;
		}
	}

}
