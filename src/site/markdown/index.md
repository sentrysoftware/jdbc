# JDBC Client

The JDBC Client allows you to execute SQL queries on various databases through JDBC. It handles:

- Loading the correct JDBC driver based on the database URL.
- Executing SQL queries with optional timeouts.
- Returning query results.

# How to run the JDBC Client inside Java

Add JDBC in the list of dependencies in your [Maven **pom.xml**](https://maven.apache.org/pom.html):

```
<dependencies>
	<!-- [...] -->
	<dependency>
		<groupId>org.sentrysoftware</groupId>
		<artifactId>jdbc</artifactId>
		<version>${project.version}</version>
	</dependency>
</dependencies>
```

Invoke the JDBC Client:

```
    public static void main(String[] args) throws Exception {
        // Example database connection details
        final String url = "jdbc:database_url";
        final String username = "username";
        final char[] password = "password".toCharArray();
        final String sqlQuery = "sql_query";
        final boolean showWarnings = true;
        final int timeout = 30; // Timeout in seconds

        // Execute the SQL query and get the results
        final SqlResult sqlResult = JdbcClient.execute(
            url,
            username,
            password,
            sqlQuery,
            showWarnings,
            timeout
        );

        // Display the query results
        System.out.println("Query Results:");
        for (List<String> row : sqlResult.getResults()) {
            for (String column : row) {
                System.out.print(column + "\t");
            }
            System.out.println();
        }

        // Display any warnings
        if (sqlResult.hasWarnings()) {
            System.out.println("\nSQL Warnings:");
            System.out.println(sqlResult.getWarnings().toString());
        }
    }
```