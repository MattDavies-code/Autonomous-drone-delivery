package uk.ac.ed.inf.functional.unit.validator;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import uk.ac.ed.inf.validator.ArgumentValidator;

public class ArgumentValidatorTest {
    @Test
    void testIsValidDate_ValidDate_ReturnsTrue() {
        ArgumentValidator validator = new ArgumentValidator();
        assertTrue(validator.isValidDate("2022-01-05"));
    }

    @Test
    void testIsValidDate_InvalidDate_ReturnsFalse() {
        ArgumentValidator validator = new ArgumentValidator();
        assertFalse(validator.isValidDate("2022/01/05"));
    }

    @Test
    void testIsValidUrl_ValidUrl_ReturnsTrue() {
        ArgumentValidator validator = new ArgumentValidator();
        assertTrue(validator.isValidUrl("https://example.com"));
    }

    @Test
    void testIsValidUrl_InvalidUrl_ReturnsFalse() {
        ArgumentValidator validator = new ArgumentValidator();
        assertFalse(validator.isValidUrl("invalid_url"));
    }
}

