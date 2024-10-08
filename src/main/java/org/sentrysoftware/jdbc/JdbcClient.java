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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This class provides functionality to execute SQL queries via JDBC and return
 * the results in a structured format.
 */
public class JdbcClient {

	private JdbcClient() {}

	static {
		// Set the default locale to US to ensure that the SQL query results are formatted correctly
		Locale.setDefault(new Locale("en", "US"));
	}

	/**
	 * Executes a SQL query via JDBC and returns the results and any SQL warnings in
	 * a {@link SqlResult} object.
	 *
	 * @param url          The JDBC URL to connect to the database
	 * @param username     The username for the database connection
	 * @param password     The password as a char array for security
	 * @param sqlQuery     The SQL query to be executed
	 * @param showWarnings If true, SQL warnings are added to the {@link SqlResult}.
	 * @param timeout      The timeout in seconds for the query
	 * @return A {@link SqlResult} object with the query results and warnings.
	 * @throws SQLException If an error occurs with the database
	 */
	public static SqlResult execute(
		String url,
		String username,
		char[] password,
		String sqlQuery,
		boolean showWarnings,
		int timeout
	) throws SQLException {
		if (url == null || url.isEmpty()) {
			throw new IllegalArgumentException("JDBC URL cannot be null or empty");
		}

		if (sqlQuery == null || sqlQuery.isEmpty()) {
			throw new IllegalArgumentException("SQL query cannot be null or empty");
		}

		try (
			Connection databaseConnection = username == null || password == null
				? DriverManager.getConnection(url)
				: DriverManager.getConnection(url, username, new String(password));
			Statement queryStatement = databaseConnection.createStatement()
		) {
			queryStatement.setQueryTimeout(timeout);
			boolean isResultSet = queryStatement.execute(sqlQuery);

			final SqlResult sqlResult = new SqlResult();

			// Process the (maybe many) result sets
			do {
				if (isResultSet) {
					processResultSet(queryStatement, sqlResult);
				} else {
					// If the result is an update count (e.g., after an UPDATE or DELETE query).
					if (queryStatement.getUpdateCount() == -1) {
						// Exit if there are no more results
						break;
					}
				}
				// Now, up to the next result set! (if any)
				isResultSet = queryStatement.getMoreResults();
			} while (isResultSet || queryStatement.getUpdateCount() != -1);

			// Now, read the PRINT statements!
			appendWarnings(showWarnings, queryStatement, sqlResult);

			return sqlResult;
		} catch (SQLException e) {
			throw new SQLException("Error executing query: " + e.getMessage(), e);
		}
	}

	/**
	 * Processes the SQL warnings and appends them to the {@link SqlResult}.
	 *
	 * @param showWarnings   If true, SQL warnings are added to the {@link SqlResult}.
	 * @param queryStatement The statement to process the warnings.
	 * @param sqlResult      The {@link SqlResult} to add the warnings to.
	 * @throws SQLException If an error occurs with the database.
	 */
	private static void appendWarnings(boolean showWarnings, Statement queryStatement, SqlResult sqlResult)
		throws SQLException {
		if (showWarnings) {
			SQLWarning warning = queryStatement.getWarnings();
			while (warning != null) {
				sqlResult.appendWarnings(warning.getMessage());
				warning = warning.getNextWarning();
			}
		}
	}

	/**
	 * Processes the result set and adds the results to the {@link SqlResult}.
	 *
	 * @param queryStatement The statement to process the result set.
	 * @param sqlResult      The {@link SqlResult} to add the results to.
	 * @throws SQLException If an error occurs with the database.
	 */
	private static void processResultSet(final Statement queryStatement, final SqlResult sqlResult) throws SQLException {
		try (ResultSet queryRecordSet = queryStatement.getResultSet()) {
			// Get the number of columns in the query output
			ResultSetMetaData metadata = queryRecordSet.getMetaData();
			int numberOfColumns = metadata.getColumnCount();

			// Build the result by parsing the rows
			while (queryRecordSet.next()) {
				final List<String> row = new ArrayList<>();
				for (int i = 1; i < numberOfColumns + 1; i++) {
					row.add(queryRecordSet.getString(i));
				}
				sqlResult.getResults().add(row);
			}
		}
	}
}
