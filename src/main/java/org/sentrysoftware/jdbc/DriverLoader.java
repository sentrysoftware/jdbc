package org.sentrysoftware.jdbc;

import java.io.OutputStream;
/*-
 * ╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲
 * JDBC Client
 * ჻჻჻჻჻჻
 * Copyright 2023 Sentry Software
 * ჻჻჻჻჻჻
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱╲╱
 */
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Singleton class to load JDBC drivers only once and in a thread-safe manner.
 */
public class DriverLoader {

	// Singleton instance
	private static DriverLoader instance;

	// List of loaded drivers to avoid loading the same driver multiple times
	private static final List<String> LOADED_DRIVERS = Collections.synchronizedList(new ArrayList<>());

	/**
	 * OutputStream to void (useful notably for redirecting Derby's log)
	 */
	public static final OutputStream DEV_NULL = new OutputStream() {
		@Override
		public void write(int b) {}
	};

	/**
	 * Private constructor to implement the Singleton pattern.
	 */
	private DriverLoader() {}

	/**
	 * Returns the Singleton instance of DriverLoader.
	 *
	 * @return DriverLoader instance
	 */
	public static synchronized DriverLoader getInstance() {
		if (instance == null) {
			instance = new DriverLoader();
		}
		return instance;
	}

	/**
	 * Retrieves a list of loaded JDBC drivers.
	 * @return a list of fully qualified names of the loaded JDBC drivers.
	 */
	public static List<String> getLoadeddrivers() {
		return LOADED_DRIVERS;
	}

	/**
	 * Loads a JDBC driver if it hasn't been loaded yet.
	 *
	 * @param driverClassName The fully qualified name of the driver class
	 * @param disableLogs     Specifies whether to disable logging for the driver (true to disable, false to enable).
	 * @throws ClassNotFoundException If the driver class cannot be found
	 */
	public synchronized void loadDriver(String driverClassName, boolean disableLogs) throws ClassNotFoundException {
		if (!LOADED_DRIVERS.contains(driverClassName)) {
			if (disableLogs) {
				disableLogging(driverClassName);
			}

			Class.forName(driverClassName);
			LOADED_DRIVERS.add(driverClassName);
		}
	}

	/**
	 * Disables or redirects logging for specific JDBC drivers.
	 *
	 * @param driverClassName The fully qualified name of the driver class
	 */
	public static void disableLogging(String driverClassName) {
		if (driverClassName.startsWith("com.microsoft.sqlserver.jdbc.SQLServerDriver")) {
			// MS SQL Server: Disable logging
			Logger logger = Logger.getLogger("com.microsoft.sqlserver.jdbc");
			logger.setLevel(Level.OFF);
		} else if (driverClassName.startsWith("org.apache.derby.jdbc.EmbeddedDriver")) {
			// Derby: Redirect logging (derby.log) to void
			System.setProperty("derby.stream.error.field", DriverLoader.class.getCanonicalName() + ".DEV_NULL");
		}
	}

	/**
	 * Loads the appropriate driver for a given JDBC URL using
	 * DriverLoader.
	 *
	 * @param url The JDBC URL for which to load the driver
	 * @throws SQLException If the driver cannot be found or loaded
	 */
	public static void loadDriverForUrl(String url) throws SQLException {
		String driverClass = null;

		// Determine which driver to load based on the URL
		if (url.startsWith("jdbc:jtds:")) {
			driverClass = "net.sourceforge.jtds.jdbc.Driver";
		} else if (url.startsWith("jdbc:sqlserver:")) {
			driverClass = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		} else if (url.startsWith("jdbc:mysql:")) {
			driverClass = "com.mysql.cj.jdbc.Driver";
		} else if (url.startsWith("jdbc:oracle:thin:")) {
			driverClass = "oracle.jdbc.driver.OracleDriver";
		} else if (url.startsWith("jdbc:postgresql:")) {
			driverClass = "org.postgresql.Driver";
		} else if (url.startsWith("jdbc:informix-sqli:") || url.startsWith("jdbc:informix-direct:")) {
			driverClass = "com.informix.jdbc.IfxDriver";
		} else if (url.startsWith("jdbc:derby:")) {
			driverClass = "org.apache.derby.jdbc.EmbeddedDriver";
		} else if (url.startsWith("jdbc:h2:")) {
			driverClass = "org.h2.Driver";
		}

		if (driverClass == null) {
			throw new SQLException("No suitable driver found for the provided JDBC URL: " + url);
		}

		try {
			DriverLoader.getInstance().loadDriver(driverClass, true);
		} catch (ClassNotFoundException e) {
			throw new SQLException("Unable to load JDBC driver for URL: " + url, e);
		}
	}
}
