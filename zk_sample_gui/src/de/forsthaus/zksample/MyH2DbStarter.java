package de.forsthaus.zksample;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;

import javax.servlet.ServletContextEvent;

import org.h2.server.web.DbStarter;

/**
 * Class for reading a sql-textfile that holds the ddl script for creating the <br>
 * tables and the sample data records. <br>
 * This class is called automatically by starting the project under tomcat. <br>
 * Please have a look at the web.xml. <br>
 * <br>
 * < listener > <br>
 * < listener-class >de.forsthaus.zksample.MyDbStarter< /listener-class > <br>
 * < /listener > <br>
 * 
 * @author Stephan Gerth / sge(at)forsthaus(de)
 * @changes
 * 
 */
public class MyH2DbStarter extends DbStarter {

	public void contextInitialized(ServletContextEvent servletContextEvent) {
		super.contextInitialized(servletContextEvent);

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

			System.out.println("count updates : " + i);

		} catch (Exception e) {
			System.out.println(e + " / " + e.getMessage());
			throw new RuntimeException(e);
		}

	}

}
