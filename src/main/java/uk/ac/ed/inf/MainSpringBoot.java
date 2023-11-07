package uk.ac.ed.inf;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import uk.ac.ed.inf.controller.RestController;

import java.net.MalformedURLException;
import java.net.URL;

@SpringBootApplication
public class MainSpringBoot {
	public static void main(String[] args) {
		// Check if the correct number of command-line arguments is provided
		if (args.length != 2) {
			System.out.println("Usage: java -jar PizzaDronz-1.0-SNAPSHOT.jar <date> <URL>");
			System.exit(1);
		}

		// Take data and REST URL to use elsewhere in the application
		String date = args[0];
		String restServerUrl = args[1];

		try {
			if (isValidDate(date) && isValidUrl(restServerUrl)) {
				SpringApplication.run(MainSpringBoot.class, date, restServerUrl);
			} else {
				System.out.println("Invalid date or URL provided.");
				System.exit(1);
			}
		} catch (Exception e) {
			System.out.println("An error occurred: " + e.getMessage());
			System.exit(1);
		}
	}
	/**
	 * Check if the date is valid
	 * @param date the date to check
	 * @return true if the date is valid, otherwise false
	 */
	private static boolean isValidDate(String date) {
		// "YYYY-MM-DD" format
		String datePattern = "^\\d{4}-\\d{2}-\\d{2}$";
		return date.matches(datePattern);
	}
	/**
	 * Check if the URL is valid
	 * @param url the URL to check
	 * @return true if the URL is valid, otherwise false
	 */
	private static boolean isValidUrl(String url) {
		try {
			// Attempt to create a URL object from the given URL string
			new URL(url);
			return true; // URL is valid
		} catch (MalformedURLException e) {
			return false; // URL is not valid
		}
	}
	@Bean
	public CommandLineRunner configureRest(RestController restController) {
		return args -> {
			String date = args[0];
			String restServerUrl = args[1];

			// Pass date and restServerUrl to the controller
			restController.setConfiguration(date, restServerUrl);
		};
	}
	//run flightpath calculator here
}







