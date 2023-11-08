package uk.ac.ed.inf.files;

/**
 * • The third file (drone-YYYY-MM-DD.geojson) is the drone’s flightpath in GeoJSON-format
 */
public class drone {
    /*
    import org.geojson.*;

    // Create a FeatureCollection for the GeoJSON file
    FeatureCollection featureCollection = new FeatureCollection();

    // Create a LineString for the drone's flightpath
    LineString lineString = new LineString();

    // Add coordinates to the LineString (replace with your actual coordinates)
    List<Position> coordinates = new ArrayList<>();
    coordinates.add(new Position(-3.186874, 55.944494));
    coordinates.add(new Position(-3.187874, 55.945494));
    // ... Add more coordinates ...

    lineString.setCoordinates(coordinates);

    // Create a Feature with the LineString
    Feature feature = new Feature();
    feature.setGeometry(lineString);

    // Add the Feature to the FeatureCollection
    featureCollection.add(feature);

    // Serialize the FeatureCollection to GeoJSON format
    ObjectMapper objectMapper = new ObjectMapper();
    String geoJson = objectMapper.writeValueAsString(featureCollection);

    // Write the GeoJSON to a file
    try (FileWriter fileWriter = new FileWriter("drone-YYYY-MM-DD.geojson")) {
        fileWriter.write(geoJson);
    }

     */
}
