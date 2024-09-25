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

	public static List<String> getLoadeddrivers() {
		return LOADED_DRIVERS;
	}

	/**
	 * Loads a JDBC driver if it hasn't been loaded yet.
	 *
	 * @param driverClassName The fully qualified name of the driver class
	 * @throws ClassNotFoundException If the driver class cannot be found
	 */
	public synchronized void loadDriver(String driverClassName) throws ClassNotFoundException {
		if (!LOADED_DRIVERS.contains(driverClassName)) {
			// Disable logging or configure special properties for certain drivers
			disableLogging(driverClassName);

			Class.forName(driverClassName);
			LOADED_DRIVERS.add(driverClassName);
		}
	}

	/**
	 * Disables or redirects logging for specific JDBC drivers.
	 *
	 * @param driverClassName The fully qualified name of the driver class
	 */
	private void disableLogging(String driverClassName) {
		if (driverClassName.startsWith("com.microsoft.sqlserver.jdbc.SQLServerDriver")) {
			// MS SQL Server: Disable logging
			Logger logger = Logger.getLogger("com.microsoft.sqlserver.jdbc");
			logger.setLevel(Level.OFF);
		} else if (driverClassName.startsWith("org.apache.derby.jdbc.EmbeddedDriver")) {
			// Derby: Redirect logging (derby.log) to void
			System.setProperty("derby.stream.error.field", this.getClass().getCanonicalName() + ".DEV_NULL");
		}
	}
}
