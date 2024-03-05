package uk.ac.ed.inf.performance.timing;
import org.junit.jupiter.api.Test;
import uk.ac.ed.inf.main.Main;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static java.time.Duration.ofSeconds;


public class TimingTest {

    @Test
    void testMainPerformance() {
        String[] testDates = {"2023-11-15", "2023-11-16", "2023-11-17", "2023-11-18", "2023-11-19", "2023-11-20"};
        String testUrl = "https://ilp-rest.azurewebsites.net/";

        // Run the main file and check if it completes within 60 seconds for each date
        for (String date : testDates) {
            long startTime = System.currentTimeMillis();
            assertTimeoutPreemptively(ofSeconds(60), () -> runMainFile(date, testUrl),
                    "Main file execution for date " + date + " took longer than 60 seconds.");

            long endTime = System.currentTimeMillis(); // Measure end time
            System.out.println("Execution time: " + date + " " + (endTime - startTime) + " milliseconds");
        }
    }

    private void runMainFile(String date, String url) {
        Main main = new Main();
        String[] args = {date, url};
        main.main(args);
    }
}