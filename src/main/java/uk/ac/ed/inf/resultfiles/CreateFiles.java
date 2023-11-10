package uk.ac.ed.inf.resultfiles;

import uk.ac.ed.inf.ilp.data.Order;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Creates a directory for the result files
 */
public class CreateFiles {
    // ALl result files are written to the directory resultFiles
    Path resultFiles;
    public CreateFiles(){
        this.resultFiles = FileSystems.getDefault().getPath("resultFiles");

        try {
            Files.createDirectory(this.resultFiles);
        }
        // If the directory already exists, do nothing
        catch (IOException e) {
            System.err.println("Could not create directory");
        }
    }
    /**
     * The first file (deliveries-YYYY-MM-DD.json) records both the deliveries and non-deliveries made by the drone
     */
    public void writeDeliveries(String date, Order[] orders) {
    }
    /**
     * The second file (flightpath-YYYY-MM-DD.json) records the flightpath of the drone move-by-move
     */
    public void writeFlighpath(String date, Object flightPaths) {
    }
    /**
     * • The third file (drone-YYYY-MM-DD.geojson) is the drone’s flightpath in GeoJSON-format
     */
    public void writeDrone(String date, Object flightPaths) {
    }
}
