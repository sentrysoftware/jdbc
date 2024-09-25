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
import java.util.List;

/**
 * This class represents the result of a SQL query,
 * including data and any associated warnings.
 */
public class SqlResult {

	private List<List<String>> results;
	private StringBuilder warnings;

	/**
	 * Constructs an empty {@code SqlResult}.
	 */
	public SqlResult() {
		this.results = new ArrayList<>();
		this.warnings = new StringBuilder();
	}

	/**
	 * Returns the results.
	 * @return the list of results.
	 */
	public List<List<String>> getResults() {
		return results;
	}

	/**
	 * Returns the warnings.
	 * @return the warnings as a StringBuilder.
	 */
	public StringBuilder getWarnings() {
		return warnings;
	}

	/**
	 * Appends a warning message to the warnings with a "Warning: " prefix.
	 *
	 * @param message the warning message to append.
	 */
	public void appendWarnings(final String message) {
		if (message != null && !message.isEmpty()) {
			warnings.append("Warning: ").append(message).append("\n");
		}
	}

	/**
	 * Checks if there are any warnings.
	 *
	 * @return true if there are warnings, false otherwise.
	 */
	public boolean hasWarnings() {
		return warnings.length() > 0;
	}
}
