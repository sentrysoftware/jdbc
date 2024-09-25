package org.sentrysoftware.jdbc;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class DriverLoaderTest {

	@Test
	public void testLoadValidDriver() {
		DriverLoader driverLoader = DriverLoader.getInstance();

		// Test loading a valid driver (H2 in this case)
		assertDoesNotThrow(() -> {
			driverLoader.loadDriver("org.h2.Driver");
		});
	}

	@Test
	public void testLoadInvalidDriverThrowsClassNotFoundException() {
		DriverLoader driverLoader = DriverLoader.getInstance();

		// Test loading an invalid driver (which should throw ClassNotFoundException)
		assertThrows(ClassNotFoundException.class, () -> {
			driverLoader.loadDriver("com.invalid.Driver");
		});
	}
}
