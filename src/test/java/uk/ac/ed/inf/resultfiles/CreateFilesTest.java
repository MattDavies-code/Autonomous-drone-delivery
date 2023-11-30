package uk.ac.ed.inf.resultfiles;

import org.junit.Test;
import uk.ac.ed.inf.model.Move;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertTrue;

public class CreateFilesTest {

    @Test
    public void writeFlightpath_ValidInput_SuccessfullyWritesFile() {
        CreateFiles createFiles = new CreateFiles();
        HashMap<String, ArrayList<Move>> flightpath = createMockFlightpath();
        String date = "2023-11-15";

        createFiles.writeFlightpath(date, flightpath);

        Path filePath = Paths.get(System.getProperty("user.dir"), "resultFiles", "flightpath-" + date + ".json");
        assertTrue("Flightpath file should have been created", Files.exists(filePath));
    }

    @Test
    public void writeDrone_ValidInput_SuccessfullyWritesFile() {
        CreateFiles createFiles = new CreateFiles();
        HashMap<String, ArrayList<Move>> flightPaths = createMockFlightpath();
        String date = "2023-11-15";

        createFiles.writeDrone(date, flightPaths);

        Path filePath = Paths.get(System.getProperty("user.dir"), "resultFiles", "drone-" + date + ".geojson");
        assertTrue("Drone file should have been created", Files.exists(filePath));    }

    // Helper methods to create mock data

    private HashMap<String, ArrayList<Move>> createMockFlightpath() {
        HashMap<String, ArrayList<Move>> flightpath = new HashMap<>();
        ArrayList<Move> moves1 = new ArrayList<>();
        moves1.add(new Move("1", 0.0,0.0, 45.0, 1.0, 1.0));
        flightpath.put("1", moves1);

        ArrayList<Move> moves2 = new ArrayList<>();
        moves2.add(new Move("1", 1.0, 1.0, 90.0, 2.0, 2.0));
        flightpath.put("2", moves2);

        return flightpath;
    }
}