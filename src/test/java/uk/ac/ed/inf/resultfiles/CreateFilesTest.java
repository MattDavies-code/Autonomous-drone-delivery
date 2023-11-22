package uk.ac.ed.inf.resultfiles;

import org.junit.Test;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.model.Move;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CreateFilesTest {

    @Test
    public void writeDeliveries_ValidInput_SuccessfullyWritesFile() {
        CreateFiles createFiles = new CreateFiles();
        Order[] orders = createMockOrders();
        String date = "2023-11-15";

        createFiles.writeDeliveries(date, orders);

        Path filePath = Paths.get(System.getProperty("user.dir"), "resultFiles", "deliveries-" + date + ".json");
        assertTrue("Deliveries file should have been created", Files.exists(filePath));    }

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

    private Order[] createMockOrders() {
        Order order1 = new Order("1", LocalDate.now(), null, null, 1500, null, null);
        Order order2 = new Order("2", LocalDate.now(), null, null, 1100, null, null);
        return new Order[]{order1, order2};
    }

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