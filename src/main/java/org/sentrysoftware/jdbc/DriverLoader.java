package org.sentrysoftware.jdbc;

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

/**
 * A Singleton class to load JDBC drivers only once and in a thread-safe manner.
 */
public class DriverLoader {

	/**
	 * List of loaded JDBC drivers.
	 */
	private static final List<String> LOADED_DRIVERS = Collections.synchronizedList(new ArrayList<>());

	/**
	 * SingletonHelper class to implement the Singleton pattern.
	 */
	private static class SingletonHelper {

		private static final DriverLoader INSTANCE = new DriverLoader();
	}

	/**
	 * Private constructor to implement the Singleton pattern.
	 */
	private DriverLoader() {}

	/**
	 * Returns the Singleton instance of DriverLoader.
	 *
	 * @return DriverLoader instance
	 */
	public static DriverLoader getInstance() {
		return SingletonHelper.INSTANCE;
	}

	/**
	 * Retrieves a list of loaded JDBC drivers.
	 * @return a list of fully qualified names of the loaded JDBC drivers.
	 */
	public static List<String> getLoadedDrivers() {
		return LOADED_DRIVERS;
	}

	/**
	 * Loads a JDBC driver if it hasn't been loaded yet.
	 *
	 * @param driverClassName The fully qualified name of the driver class
	 * @param disableLogs     Specifies whether to disable logging for the driver (true to disable, false to enable).
	 * @throws ClassNotFoundException If the driver class cannot be found
	 */
	public synchronized void loadDriver(final String driverClassName, final boolean disableLogs)
		throws ClassNotFoundException {
		if (!LOADED_DRIVERS.contains(driverClassName)) {
			if (disableLogs) {
				DatabaseLogUtils.disableLogging(driverClassName);
			}

			Class.forName(driverClassName);
			LOADED_DRIVERS.add(driverClassName);
		}
	}

	/**
	 * Loads the appropriate driver for a given JDBC URL using
	 * DriverLoader.
	 *
	 * @param url The JDBC URL for which to load the driver
	 * @throws SQLException If the driver cannot be found or loaded
	 */
	public static void loadDriverForUrl(final String url) throws SQLException {
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
		} catch (Exception e) {
			throw new SQLException("Unable to load JDBC driver for URL: " + url, e);
		}
	}
}
