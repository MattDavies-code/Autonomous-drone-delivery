package uk.ac.ed.inf.resultfiles;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
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
import java.util.HashMap;

/**
 * Creates a directory for the result files
 */
public class CreateFiles {
    // All result files are written to the directory resultFiles
    private final Path resultFiles;

    public CreateFiles() {
        // Use an absolute path for the resultFiles directory
        this.resultFiles = FileSystems.getDefault().getPath(System.getProperty("user.dir"), "resultFiles");

        try {
            Files.createDirectories(this.resultFiles);
        } catch (IOException e) {
            // Log the exception for debugging purposes
            e.printStackTrace();
        }
    }

    /**
     * The first file (deliveries-YYYY-MM-DD.json) records both the deliveries and non-deliveries made by the drone
     */
    public void writeDeliveries(String date, Order[] orders) {
        try {
            Gson gson = createGsonWithLocalDateSerializer();
            String fileName = "deliveries-" + date + ".json";
            FileWriter writer = new FileWriter(resultFiles.resolve(fileName).toString());
            gson.toJson(orders, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Gson createGsonWithLocalDateSerializer() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                .setPrettyPrinting()
                .create();
    }

    /**
     * The second file (flightpath-YYYY-MM-DD.json) records the flightpath of the drone move-by-move
     */
    public void writeFlightpath(String date, HashMap<String, ArrayList<Move>> flightpath) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String fileName = "flightpath-" + date + ".json";
            FileWriter writer = new FileWriter(resultFiles.resolve(fileName).toString());
            JsonArray jsonflightpath = flightpathToJsonArray(flightpath);
            gson.toJson(jsonflightpath, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    private JsonObject createMoveJsonObject(String orderNo, Move move) {
        JsonObject moveObject = new JsonObject();
        moveObject.addProperty("orderNo", orderNo);
        moveObject.addProperty("fromLongitude", move.getFromLng();
        moveObject.addProperty("fromLatitude", move.getFromLat();
        moveObject.addProperty("angle", move.getAngle());
        moveObject.addProperty("toLongitude", move.getToLng();
        moveObject.addProperty("toLatitude", move.getToLat();

        return moveObject;
    }

    /**
     * The third file (drone-YYYY-MM-DD.geojson) is the drone’s flightpath in GeoJSON-format
     */
    public void writeDrone(LocalDate date, ArrayList<Move> flightPath) {
        try {
            Gson gson = createGsonWithLocalDateSerializer();
            String fileName = "drone-" + date + ".geojson";
            FileWriter writer = new FileWriter(resultFiles.resolve(fileName).toString());
            gson.toJson(createGeoJsonFeatureCollection(flightPath), writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, Object> createGeoJsonFeatureCollection(ArrayList<Move> flightpath) {
        HashMap<String, Object> featureCollection = new HashMap<>();
        ArrayList<HashMap<String, Object>> features = new ArrayList<>();
        HashMap<String, Object> feature = new HashMap<>();
        ArrayList<ArrayList<Double>> coordinates = new ArrayList<>();

        for (Move move : flightpath) {
            ArrayList<Double> point = new ArrayList<>();
            point.add(move.getFrom().lng());
            point.add(move.getFrom().lat());
            coordinates.add(point);
        }

        feature.put("type", "Feature");
        feature.put("geometry", createGeoJsonLineString(coordinates));
        features.add(feature);

        featureCollection.put("type", "FeatureCollection");
        featureCollection.put("features", features);

        return featureCollection;
    }

    private HashMap<String, Object> createGeoJsonLineString(ArrayList<ArrayList<Double>> coordinates) {
        HashMap<String, Object> lineString = new HashMap<>();
        lineString.put("type", "LineString");
        lineString.put("coordinates", coordinates);
        return lineString;
    }

}
