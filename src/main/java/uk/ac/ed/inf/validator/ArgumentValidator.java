package uk.ac.ed.inf.validator;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Class to validate arguments passed into the main method
 */
public class ArgumentValidator {

    /**
     * Check if the date is valid
     * @param date the date to check
     * @return true if the date is valid, otherwise false
     */
    public boolean isValidDate(String date) {
        // "YYYY-MM-DD" format
        String datePattern = "^\\d{4}-\\d{2}-\\d{2}$";
        return date.matches(datePattern);
    }
    /**
     * Check if the URL is valid
     * @param url the URL to check
     * @return true if the URL is valid, otherwise false
     */
    public boolean isValidUrl(String url) {
        try {
            // Attempt to create a URL object from the given URL string
            new URL(url);
            return true; // URL is valid
        } catch (MalformedURLException e) {
            return false; // URL is not valid
        }
    }
}
