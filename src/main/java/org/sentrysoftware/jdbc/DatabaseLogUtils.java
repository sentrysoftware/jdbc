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

import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides utility methods to disable or redirect logging for specific JDBC drivers.
 */
public class DatabaseLogUtils {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private DatabaseLogUtils() {}

	/**
	 * An OutputStream that does nothing to redirect logging to void.
	 */
	public static final OutputStream NOOP_STREAM = new OutputStream() {
		@Override
		public void write(int b) {
			// Do nothing to redirect logging to void
		}
	};

	/**
	 * Disables or redirects logging for specific JDBC drivers.
	 *
	 * @param driverClassName The fully qualified name of the driver class
	 */
	public static void disableLogging(final String driverClassName) {
		if (driverClassName.startsWith("com.microsoft.sqlserver.jdbc.SQLServerDriver")) {
			// MS SQL Server: Disable logging
			Logger.getLogger("com.microsoft.sqlserver.jdbc").setLevel(Level.OFF);
		} else if (driverClassName.startsWith("org.apache.derby.jdbc.EmbeddedDriver")) {
			// Derby: Redirect logging (derby.log) to void
			System.setProperty("derby.stream.error.field", DatabaseLogUtils.class.getCanonicalName() + ".NOOP_STREAM");
		}
	}
}
