package uk.ac.ed.inf.resultfiles;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.model.Move;
import uk.ac.ed.inf.ilp.gsonUtils.LocalDateSerializer;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Creates a directory for the result files and writes the result files to the directory
 */
public class CreateFiles {

    // All result files are written to the directory resultFiles
    private final Path resultFiles;

    /**
     * Creates a directory for the result files
     */
    public CreateFiles() {
        // Use an absolute path for the resultFiles directory
        this.resultFiles = FileSystems.getDefault().getPath(System.getProperty("user.dir"), "resultFiles");

        try {
            Files.createDirectories(this.resultFiles);
        } catch (IOException e) {
            System.err.println("An error occurred creating the resultFiles directory: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * The first file (deliveries-YYYY-MM-DD.json) records both the deliveries and non-deliveries made by the drone
     * @param orders the orders that were retrieved
     * @param date the date of the orders that were retrieved
     */
    public void writeDeliveries(String date, Order[] orders) {
        try {
            // Convert Order objects to SimplifiedOrder objects to remove unnecessary fields
            List<SimplifiedOrder> simplifiedOrders = Arrays.stream(orders)
                    .map(SimplifiedOrder::fromOrder)
                    .collect(Collectors.toList());

            Gson gson = createGsonWithLocalDateSerializer();
            String fileName = "deliveries-" + date + ".json";
            FileWriter writer = new FileWriter(resultFiles.resolve(fileName).toString(), false);
            gson.toJson(simplifiedOrders, writer);
            writer.close();
        } catch (IOException e) {
            System.err.println("An error occurred writing the deliveries file: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Create a custom Gson instance with the LocalDateSerializer
     * @return Gson
     */
    private Gson createGsonWithLocalDateSerializer() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                .setPrettyPrinting()
                .create();
    }

    /**
     * The second file (flightpath-YYYY-MM-DD.json) records the flightpath of the drone move-by-move
     * @param date the date of the orders that were retrieved
     * @param flightpath the flightpath of the drone move-by-move for every order
     */
    public void writeFlightpath(String date, HashMap<String, ArrayList<Move>> flightpath) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String fileName = "flightpath-" + date + ".json";
            FileWriter writer = new FileWriter(resultFiles.resolve(fileName).toString(), false);
            JsonArray jsonflightpath = flightpathToJsonArray(flightpath);
            gson.toJson(jsonflightpath, writer);
            writer.close();
        } catch (IOException e) {
            System.err.println("An error occurred writing the flightpath file: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Converts a HashMap of flightpaths to a JsonArray
     * @param flightpath the flightpath of the drone move-by-move for every order
     * @return JsonArray
     */
    private JsonArray flightpathToJsonArray(HashMap<String, ArrayList<Move>> flightpath) {
        JsonArray jsonArray = new JsonArray();

        for (String orderNo : flightpath.keySet()) {
            ArrayList<Move> moves = flightpath.get(orderNo);
            for (Move move : moves) {
                jsonArray.add(createMoveJsonObject(orderNo, move));
            }
        }
        return jsonArray;
    }


    /**
     * Create a JsonObject for a move
     * @param orderNo the order number
     * @param move the move
     * @return Move JsonObject
     */
    private JsonObject createMoveJsonObject(String orderNo, Move move) {
        JsonObject moveObject = new JsonObject();
        moveObject.addProperty("orderNo", orderNo);
        moveObject.addProperty("fromLongitude", move.getFromLng());
        moveObject.addProperty("fromLatitude", move.getFromLat());
        moveObject.addProperty("angle", move.getAngle());
        moveObject.addProperty("toLongitude", move.getToLng());
        moveObject.addProperty("toLatitude", move.getToLat());

        return moveObject;
    }

    /**
     * The third file (drone-YYYY-MM-DD.geojson) is the drone’s flightpath in GeoJSON-format
     * @param date        the date in the format YYYY-MM-DD
     * @param flightPaths a HashMap containing the flight paths for each order
     */
    public void writeDrone(String date, HashMap<String, ArrayList<Move>> flightPaths) {
        try {
            Gson gson = createGsonWithLocalDateSerializer();
            String fileName = "drone-" + date + ".geojson";
            FileWriter writer = new FileWriter(resultFiles.resolve(fileName).toString(), false);
            // if there are no deliveries, create an empty file with just {} in it
            if (flightPaths.isEmpty()) {
                writer.write("{}");
                writer.close();
                return;
            }
            gson.toJson(createGeoJsonFeatureCollection(flightPaths), writer);
            writer.close();
        } catch (IOException e) {
            System.err.println("An error occurred writing the drone file: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Create a GeoJson feature collection
     * @param flightPaths a HashMap containing the flight paths for each order
     * @return HashMap containing the GeoJson feature collection
     */
    private HashMap<String, Object> createGeoJsonFeatureCollection(HashMap<String, ArrayList<Move>> flightPaths) {
        HashMap<String, Object> featureCollection = new HashMap<>();
        ArrayList<HashMap<String, Object>> features = new ArrayList<>();

        for (String orderNo : flightPaths.keySet()) {
            ArrayList<Move> flightPath = flightPaths.get(orderNo);
            features.add(createFeature(orderNo, flightPath));
        }

        featureCollection.put("type", "FeatureCollection");
        featureCollection.put("features", features);

        return featureCollection;
    }

    /**
     * Create a GeoJson feature
     * @param orderNo the order number
     * @param flightPath the flight path for a single order
     * @return HashMap containing the GeoJson feature
     */
    private HashMap<String, Object> createFeature(String orderNo, ArrayList<Move> flightPath) {
        HashMap<String, Object> feature = new HashMap<>();
        ArrayList<ArrayList<Double>> coordinates = new ArrayList<>();

        for (Move move : flightPath) {
            ArrayList<Double> point = new ArrayList<>();
            point.add(move.getFromLng());
            point.add(move.getFromLat());
            coordinates.add(point);
        }

        feature.put("type", "Feature");
        feature.put("geometry", createGeoJsonLineString(coordinates));
        feature.put("properties", createProperties(orderNo));

        return feature;
    }

    /**
     * Create the properties for a GeoJson feature
     * @param orderNo the order number
     * @return HashMap containing the properties
     */
    private HashMap<String, Object> createProperties(String orderNo) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("orderNo", orderNo);
        return properties;
    }

    /**
     * Create a GeoJson line string
     * @param coordinates the coordinates for the line string
     * @return HashMap containing the GeoJson line string
     */
    private HashMap<String, Object> createGeoJsonLineString(ArrayList<ArrayList<Double>> coordinates) {
        HashMap<String, Object> lineString = new HashMap<>();
        lineString.put("type", "LineString");
        lineString.put("coordinates", coordinates);
        return lineString;
    }
}
